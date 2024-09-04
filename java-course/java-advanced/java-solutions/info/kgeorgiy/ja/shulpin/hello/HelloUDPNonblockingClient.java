package info.kgeorgiy.ja.shulpin.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * UDP client that sends, receives, and outputs received response to console.
 * Implementation of {@link HelloClient}.
 *
 * @author Shulpin Egor
 */
public class HelloUDPNonblockingClient extends AbstractHelloUDPClient {

    /**
     * Allows to run UDP client from the command line.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        mainImpl(args, HelloUDPNonblockingClient.class);
    }

    @Override
    protected void runImpl(String host, int port, String prefix, int threads, int requests) {
        try (Selector selector = Selector.open();
             DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_WRITE);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            InetSocketAddress address = new InetSocketAddress(host, port);

            for (int thread = 1; thread <= threads; thread++) {
                for (int request = 1; request <= requests; request++) {
                    String result = "";
                    String message = getMessage(prefix, thread, request);
                    byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

                    System.out.println("sent: " + message);

                    do {
                        if (selector.select(TIMEOUT) == 0) {
                            selector.keys().forEach(key -> key.interestOps(SelectionKey.OP_WRITE));
                        }
                        for (final Iterator<SelectionKey> iterator =
                             selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                            try {
                                final SelectionKey key = iterator.next();
                                if (key.isWritable()) {
                                    channel.send(ByteBuffer.wrap(messageBytes), address);

                                    key.interestOps(SelectionKey.OP_READ);
                                }
                                if (key.isReadable()) {
                                    channel.receive(buffer);

                                    result = new String(
                                            buffer.array(),
                                            buffer.arrayOffset(),
                                            buffer.position(),
                                            StandardCharsets.UTF_8
                                    );

                                    buffer.clear();
                                    key.interestOps(SelectionKey.OP_WRITE);
                                }
                            } finally {
                                iterator.remove();
                            }
                        }
                    } while (!result.contains(message));

                    System.out.println("received: " + result);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException("Error occurred while opening.", e);
        }
    }
}
