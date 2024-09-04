package info.kgeorgiy.ja.shulpin.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * UDP server that sends hello response to received message.
 * Implementation of {@link HelloServer}.
 *
 * @author Shulpin Egor
 */
public class HelloUDPServer extends AbstractHelloUDPServer implements NewHelloServer {

    private List<DatagramSocket> sockets;

    /**
     * Allows to run UDP server from the command line.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        mainImpl(args, HelloUDPServer.class);
    }

    protected void initImpl(int threads, Map<Integer, String> ports) throws IOException {
        service = Executors.newFixedThreadPool(threads + ports.size());

        sockets = new ArrayList<>();
        for (final int port : ports.keySet()) {
            final DatagramSocket socket = new DatagramSocket(port);
            socket.setSoTimeout(TIMEOUT);
            sockets.add(socket);
        }
    }

    @Override
    public void startImpl(int threads, Map<Integer, String> ports) {
        sockets.forEach(socket -> {
            service.submit(() -> {
                try {
                    final int bufferSize = socket.getReceiveBufferSize();
                    final DatagramPacket receivedPacket =
                            new DatagramPacket(new byte[bufferSize], bufferSize);
                    while (!Thread.interrupted()) {
                        socket.receive(receivedPacket);

                        String message = new String(
                                receivedPacket.getData(),
                                receivedPacket.getOffset(),
                                receivedPacket.getLength(),
                                StandardCharsets.UTF_8
                        );
                        final byte[] result = getMessage(socket, ports, message);

                        socket.send(new DatagramPacket(
                                result,
                                result.length,
                                receivedPacket.getSocketAddress()
                        ));
                    }
                } catch (final IOException ignored) {
                }
            });
        });
    }

    @Override
    protected void closeImpl() {
        sockets.forEach(DatagramSocket::close);
    }
}
