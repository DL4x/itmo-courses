package info.kgeorgiy.ja.shulpin.walk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

public class WalkUtils {
    static void createOutputDirectory(final Path path) {
        try {
            final Path parent;
            if ((parent = path.getParent()) != null && !Files.exists(parent)) {
                Files.createDirectory(parent);
            }
        } catch (final IOException e) {
            System.err.format("Error when creating directories leading to specified file. [%s]%s",
                    e, System.lineSeparator());
        }
    }

    static void printInvalidPathError(final String fileType, final String fileName, final String e) {
        System.err.format("Path to %s file %s is incorrect. [%s]%s",
                fileType, fileName, e, System.lineSeparator());
    }

    static void printExistOrFoundError(final String fileType, final String fileName, final String e) {
        System.err.format("%s file %s does not exists or not found by given path. [%s]%s",
                fileType, fileName, e, System.lineSeparator());
    }

    static void printSecurityError(final String fileType, final String fileName, final String e) {
        System.err.format("Security error when trying to interact with %s file %s. [%s]%s",
                fileType, fileName, e, System.lineSeparator());
    }

    static void printIOError(final String fileType, final String fileName, final String e) {
        System.err.format("Error while working with %s file %s. [%s]%s",
                fileType, fileName, e, System.lineSeparator());
    }
}
