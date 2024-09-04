package info.kgeorgiy.ja.shulpin.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import static info.kgeorgiy.ja.shulpin.walk.WalkUtils.*;

public class Walk {
    private static final byte[] buffer = new byte[1 << 12];

    public static String jenkinsHash(final String path) {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            int hash = 0;
            int bufferLength;
            while ((bufferLength = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bufferLength; i++) {
                    hash += (buffer[i] & 0xff);
                    hash += (hash << 10);
                    hash ^= (hash >>> 6);
                }
            }
            hash += hash << 3;
            hash ^= hash >>> 11;
            hash += hash << 15;
            return String.format("%08x", hash);
        } catch (final NoSuchFileException | FileNotFoundException e) {
            printExistOrFoundError("Specified", path, e.toString());
        } catch (final SecurityException e) {
            printSecurityError("specified", path, e.toString());
        } catch (final IOException e) {
            printIOError("specified", path, e.toString());
        }
        return "0".repeat(8);
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2
                || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments passed. " +
                    "[Expected: \"../<Input file>\" \"../<Output File>\"]");
            return;
        }

        try {
            final Path inputFile = Path.of(args[0]);
            try (BufferedReader bufferedReader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
                try {
                    final Path outputFile = Path.of(args[1]);
                    createOutputDirectory(outputFile);
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                        String fileName;
                        while ((fileName = bufferedReader.readLine()) != null) {
                            try {
                                bufferedWriter.write(String.format("%s %s%s",
                                        jenkinsHash(fileName), fileName, System.lineSeparator()));
                            } catch (final IOException e) {
                                System.err.printf("Error occurred while writing the hash to the output file. [%s]%s",
                                        e, System.lineSeparator());
                            }
                        }
                    } catch (final SecurityException e) {
                        printSecurityError("output", outputFile.toString(), e.toString());
                    } catch (final IOException e) {
                        printIOError("output", outputFile.toString(), e.toString());
                    }
                } catch (final InvalidPathException e) {
                    printInvalidPathError("output", args[1], e.toString());
                }
            } catch (final NoSuchFileException | FileNotFoundException e) {
                printExistOrFoundError("Input", inputFile.toString(), e.toString());
            } catch (final SecurityException e) {
                printSecurityError("input", inputFile.toString(), e.toString());
            } catch (final IOException e) {
                printIOError("input", inputFile.toString(), e.toString());
            }
        } catch (final InvalidPathException e) {
            printInvalidPathError("input", args[0], e.toString());
        }
    }
}
