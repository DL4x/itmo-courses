package info.kgeorgiy.ja.shulpin.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UDP client that sends, receives, and outputs received response to console.
 * Implementation of {@link HelloClient}.
 *
 * @author Shulpin Egor
 */
public class HelloUDPClient extends AbstractHelloUDPClient {

    /**
     * Allows to run UDP client from the command line.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        mainImpl(args, HelloUDPClient.class);
    }

    @Override
    protected void runImpl(String host, int port, String prefix, int threads, int requests) {
        try (ExecutorService service = Executors.newFixedThreadPool(threads)) {
            for (int thread = 1; thread <= threads; thread++) {
                final int threadI = thread;
                service.submit(() -> {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        socket.setSoTimeout(TIMEOUT);
                        for (int request = 1; request <= requests; request++) {
                            String result = "";
                            String message = getMessage(prefix, threadI, request);
                            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

                            System.out.println("sent: " + message);

                            final int bufferSize = socket.getReceiveBufferSize();
                            final DatagramPacket receivedPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
                            do {
                                try {
                                    socket.send(new DatagramPacket(
                                            messageBytes,
                                            messageBytes.length,
                                            new InetSocketAddress(host, port)
                                    ));

                                    socket.receive(receivedPacket);

                                    result = new String(
                                            receivedPacket.getData(),
                                            receivedPacket.getOffset(),
                                            receivedPacket.getLength(),
                                            StandardCharsets.UTF_8
                                    );
                                } catch (final IOException e) {
                                    System.err.format("Unexpected error has occurred " +
                                            "when sending/receiving. [%s]%n", e.getMessage());
                                }
                            } while (!result.contains(message));

                            System.out.println("received: " + result);
                        }
                    } catch (final SocketException e) {
                        throw new RuntimeException("Error occurred while opening the socket.", e);
                    }
                });
            }
        }
    }
}
