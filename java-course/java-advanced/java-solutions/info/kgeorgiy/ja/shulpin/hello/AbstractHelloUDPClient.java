package info.kgeorgiy.ja.shulpin.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

abstract class AbstractHelloUDPClient implements HelloClient {

    protected static final int TIMEOUT = 50;
    protected static final int BUFFER_SIZE = 1 << 10;

    protected static void mainImpl(String[] args, Class<? extends HelloClient> clazz) {
        if (args == null || args.length != 5
                || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Invalid arguments passed. " +
                    "[Expected: <Host name or ip-address> <Port number> " +
                    "<Requests prefix> <Number of threads> <Number of requests>]");
            return;
        }

        try {
            final String host = args[0];
            final int port = Integer.parseInt(args[1]);
            String prefix = args[2];
            final int threads = Integer.parseInt(args[3]);
            final int requests = Integer.parseInt(args[4]);

            HelloClient client = clazz.getDeclaredConstructor().newInstance();
            client.run(host, port, prefix, threads, requests);
        } catch (final NumberFormatException e) {
            System.err.format("Invalid arguments format (port number, number of threads " +
                    "and number of requests must be positive numbers). [%s]%n", e.getMessage());
        } catch (final RuntimeException e) {
            System.err.format("Unexpected error occurred. [%s]%n", e.getMessage());
        } catch (InvocationTargetException | NoSuchMethodException
                 | IllegalAccessException | InstantiationException e) {
            System.err.format("Error occurred while creating new instance of client [%s]%n", e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        runImpl(host, port, prefix, threads, requests);
    }

    protected String getMessage(String prefix, int thread, int request) {
        return String.format("%s%s_%s", prefix, thread, request);
    }

    protected abstract void runImpl(String host, int port, String prefix, int threads, int requests);
}
