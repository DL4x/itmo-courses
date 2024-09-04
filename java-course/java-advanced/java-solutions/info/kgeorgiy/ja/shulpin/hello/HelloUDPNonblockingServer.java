package info.kgeorgiy.ja.shulpin.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * UDP server that sends hello response to received message.
 * Implementation of {@link HelloServer}.
 *
 * @author Shulpin Egor
 */
public class HelloUDPNonblockingServer extends AbstractHelloUDPServer {

    private Selector selector;
    private ExecutorService single;
    private List<DatagramChannel> channels;

    /**
     * Allows to run UDP server from the command line.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        mainImpl(args, HelloUDPNonblockingServer.class);
    }

    @Override
    protected void initImpl(int threads, Map<Integer, String> ports) throws IOException {
        channels = new ArrayList<>();

        selector = Selector.open();

        single = Executors.newSingleThreadExecutor();
        service = Executors.newFixedThreadPool(threads);
    }

    @Override
    protected void startImpl(int threads, Map<Integer, String> ports) {
        ports.keySet().forEach(port -> {
            DatagramChannel channel;
            Queue<AbstractMap.SimpleEntry<byte[], SocketAddress>> writableQueue = new ConcurrentLinkedQueue<>();
            try {
                channel = DatagramChannel.open()
                        .bind(new InetSocketAddress(port));
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                channels.add(channel);
            } catch (final IOException e) {
                throw new RuntimeException("Error while binding socket.", e);
            }
            single.submit(() -> {
                while (!Thread.interrupted()) {
                    try {
                        selector.select();
                        for (final Iterator<SelectionKey> iterator =
                             selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                            try {
                                final SelectionKey key = iterator.next();
                                if (key.isWritable()) {
                                    final AbstractMap.SimpleEntry<byte[], SocketAddress> response;
                                    if ((response = writableQueue.poll()) == null) {
                                        key.interestOps(SelectionKey.OP_READ);
                                        continue;
                                    }
                                    channel.send(ByteBuffer.wrap(response.getKey()), response.getValue());
                                }
                                if (key.isReadable()) {
                                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                                    final SocketAddress address = channel.receive(buffer);

                                    service.submit(() -> {
                                        final String message = new String(
                                                buffer.array(),
                                                buffer.arrayOffset(),
                                                buffer.position(),
                                                StandardCharsets.UTF_8
                                        );
                                        final byte[] bytes = getMessage(channel.socket(), ports, message);

                                        writableQueue.offer(new AbstractMap.SimpleEntry<>(bytes, address));

                                        key.interestOps(SelectionKey.OP_WRITE);
                                        selector.wakeup();
                                    });
                                }
                            } finally {
                                iterator.remove();
                            }
                        }
                    } catch (final IOException ignored) {
                    }
                }
            });
        });
    }

    @Override
    protected void closeImpl() {
        try {
            selector.close();
            for (final DatagramChannel channel : channels) {
                channel.close();
            }
        } catch (IOException e) {
            System.err.format("Error occurred " +
                    "while closing. [%s]%n", e.getMessage());
        }
        single.close();
    }
}
