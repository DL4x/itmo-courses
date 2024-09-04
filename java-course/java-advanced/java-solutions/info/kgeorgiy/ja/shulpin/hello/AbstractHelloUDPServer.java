package info.kgeorgiy.ja.shulpin.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;

abstract class AbstractHelloUDPServer implements NewHelloServer {

    protected ExecutorService service;
    protected static final int TIMEOUT = 50;
    protected static final int BUFFER_SIZE = 1 << 10;

    protected static void mainImpl(String[] args, Class<? extends HelloServer> clazz) {
        if (args == null || args.length != 2
                || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments passed. " +
                    "[Expected: <Port Number> <Number of threads>]");
            return;
        }

        try (HelloServer server = clazz.getDeclaredConstructor().newInstance()) {
            final int port = Integer.parseInt(args[0]);
            final int threads = Integer.parseInt(args[1]);

            server.start(port, threads);
        } catch (final NumberFormatException e) {
            System.err.format("Invalid arguments format " +
                    "(must be positive numbers). [%s]%n", e.getMessage());
        } catch (final RuntimeException e) {
            System.err.format("Unexpected error occurred. [%s]%n", e.getMessage());
        } catch (InvocationTargetException | NoSuchMethodException
                 | IllegalAccessException | InstantiationException e) {
            System.err.format("Error occurred while creating new instance of server [%s]%n", e.getMessage());
        }
    }

    private void init(int threads, Map<Integer, String> ports) {
        try {
            initImpl(threads, ports);
        } catch (final IOException e) {
            throw new RuntimeException("Error while starting.", e);
        }
    }

    @Override
    public void start(int threads, Map<Integer, String> ports) {
        init(threads, ports);

        startImpl(threads, ports);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        closeImpl();
        service.close();
    }

    protected byte[] getMessage(DatagramSocket socket, Map<Integer, String> ports, String message) {
        return ports.get(socket.getLocalPort()).replaceAll("\\$", message).getBytes(StandardCharsets.UTF_8);
    }

    protected abstract void initImpl(int threads, Map<Integer, String> ports) throws IOException;

    protected abstract void startImpl(int threads, Map<Integer, String> ports);

    protected abstract void closeImpl();
}
