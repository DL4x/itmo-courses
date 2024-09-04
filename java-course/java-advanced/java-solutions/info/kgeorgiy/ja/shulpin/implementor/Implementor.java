package info.kgeorgiy.ja.shulpin.implementor;

import info.kgeorgiy.java.advanced.implementor.JarImpler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Generate <var>.java</var> or <var>.jar</var> implementation
 * of a specified type token. Produced result will be written to the
 * {@code root} directory. Implements {@link JarImpler} interface,
 * can throw {@link ImplerException} while using {@link #implement(Class, Path)}
 * or {@link #implementJar(Class, Path)}.
 *
 * @author Shulpin Egor
 */
public class Implementor implements JarImpler {
    /**
     * Default constructor.
     */
    public Implementor() {}

    /**
     * Generate implementation from console.
     * <p>
     * If first argument is {@code -jar} {@link #implementJar(Class, Path)}
     * will be called, {@link #implement(Class, Path)} otherwise.
     * If implementation can not be generated, warning message will be
     * written to {@link System#err}.
     * @param args program arguments
     */
    public static void main(String[] args) {
        if (args == null || (args.length != 2 && args.length != 3)
                || args[0] == null || args[1] == null || (args.length == 3 && args[2] == null)) {
            System.err.println("Invalid arguments passed. " +
                    "[Expected: <> \"../<Interface Name>\" \"../<Path Name>\"]");
            return;
        }

        if (args.length == 3 && !args[0].equals("-jar")) {
            System.err.format("Unknown option: %s%n" +
                    "Usage: [-jar]", args[0]);
            return;
        }

        Implementor implementor = new Implementor();
        try {
            if (args[0].equals("-jar")) {
                implementor.implementJar(Class.forName(args[1]), Path.of(args[2]));
            } else {
                implementor.implement(Class.forName(args[0]), Path.of(args[1]));
            }
        } catch (final ImplerException e) {
            System.err.format("Error while implementing [%s].%n", e.getMessage());
        } catch (final InvalidPathException e) {
            System.err.format("Path does not exists or not found by given path [%s].%n", e.getMessage());
        } catch (final ClassNotFoundException e) {
            System.err.format("Class by specified name could not be found [%s].%n", e.getMessage());
        }
    }

    /**
     * Write {@code token} package to implementation file using {@link
     * Class#getPackageName()} only if it is specified in the token.
     * @param token type token to create implementation for.
     * @param writer writer which white to <var>.java</var> file
     *          while implementing.
     * @throws IOException if an I/O error occurs.
     */
    private void writePackage(Class<?> token, final Writer writer) throws IOException {
        final String packageName = token.getPackageName();
        if (!packageName.isEmpty()) {
            writer.write(String.format("package %s;%n%n", packageName));
        }
    }

    /**
     * Encodes string to ascii.
     * @param string string which we need to encode.
     * @return encoded string.
     */
    private String encode(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            final char ch = string.charAt(i);
            stringBuilder.append(ch < 128
                    ? ch : String.format("\\u%04x", (int) ch));
        }
        return stringBuilder.toString();
    }

    /**
     * Write class body for specified type token.
     * <p>
     * Write class name using {@link Class#getSimpleName()} with
     * {@code Impl} suffix. Specifies token as the implemented interface
     * using {@link Class#getCanonicalName()}. To implement the methods,
     * {@link #writeMethodBody(Method, Writer)} is called for all
     * abstract methods of the token.
     * @param token type token to create implementation for.
     * @param writer writer which white to <var>.java</var> file
     *          while implementing.
     * @throws IOException if an I/O error occurs.
     */
    private void writeClassBody(Class<?> token, final Writer writer) throws IOException {
        writer.write(String.format("class %sImpl implements %s ",
                encode(token.getSimpleName()), encode(token.getCanonicalName())));

        writer.write("{" + System.lineSeparator());
        for (final Method method : token.getMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                writeMethodBody(method, writer);
            }
        }
        writer.write("}" + System.lineSeparator());
    }

    /**
     * Returns modifiers of the implemented method.
     * @param method method whose modifiers need to be returned.
     * @return method modifiers in the form of a string without
     *          {@code abstract}/{@code transient}.
     */
    private String getMethodModifiers(Method method) {
        return Modifier.toString(method.getModifiers()
                & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT);
    }

    /**
     * Returns parameters of the implemented method.
     * @param method method whose parameters need to be returned.
     * @return method parameters in the form of a string.
     *          Each parameter is written as its canonical name and
     * {@code arg} name with its number at the end.
     */
    private String getMethodParameters(Method method) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> String.format("%s %s",
                        parameter.getType().getCanonicalName(), parameter.getName()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns exceptions of the implemented method.
     * @param method method whose exceptions need to be returned.
     * @return method exceptions in the form of a string with
     *          a keyword {@code throws} at the beginning.
     */
    private String getMethodExceptions(Method method) {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length == 0) {
            return "";
        }
        return Arrays.stream(exceptionTypes)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", ", "throws ", " "));
    }

    /**
     * Returns return value of the implemented method.
     * @param method method whose default return value need to be returned.
     * <ul>
     *     <li>return {@code false} if {@code boolean};</li>
     *     <li>return {@code 0} if primitive number;</li>
     *     <li>return {@code null} if reference type;</li>
     *     <li>return empty string otherwise.</li>
     * </ul>
     * @return {@link Method#getReturnType()} default value in the form of a string.
     */
    private String getReturnDefaultValue(Method method) {
        Class<?> clazz = method.getReturnType();
        if (clazz.equals(void.class)) {
            return "";
        }
        if (clazz.equals(boolean.class)) {
            return "false";
        }
        if (clazz.isPrimitive()) {
            return "0";
        }
        return "null";
    }

    /**
     * Write body for specified method.
     * Method body consists of:
     * <ul>
     *     <li>Modifiers (using {@link #getMethodModifiers(Method)});</li>
     *     <li>Return type (using {@link Method#getReturnType()});</li>
     *     <li>Method name (using {@link Method#getName()});</li>
     *     <li>Parameters (using{@link #getMethodParameters(Method)});</li>
     *     <li>Exceptions (using{@link #getMethodExceptions(Method)});</li>
     *     <li>Return value (using{@link #getReturnDefaultValue(Method)}).</li>
     * </ul>
     * @param method method to create implementation for.
     * @param writer writer which white to <var>.java</var> file
     *          while implementing.
     * @throws IOException if an I/O error occurs.
     */
    private void writeMethodBody(Method method, final Writer writer) throws IOException {
        writer.write(String.format("\t%s %s %s(%s) %s",
                getMethodModifiers(method),
                method.getReturnType()
                        .getCanonicalName(),
                encode(method.getName()),
                getMethodParameters(method),
                getMethodExceptions(method)));

        writer.write("{" + System.lineSeparator());
        final String defaultValue = getReturnDefaultValue(method);
        writer.write(String.format("\t\treturn%s;%n",
                defaultValue.isEmpty() ? defaultValue : " " + defaultValue));
        writer.write("\t}" + System.lineSeparator());
    }

    /**
     * Delete file by specified path.
     * <p>
     * If deleting is not possible, warning message will be written to {@link System#err}.
     * @param path The path to delete the file.
     */
    private void delete(Path path) {
        try {
            Files.delete(path);
        } catch (final IOException | SecurityException e) {
            System.err.format("Error when deleting incorrectly created file [%s].%n", e.getMessage());
        }
    }

    /**
     * Generate <var>.java</var> implementation of a specified type token.
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException
     *          when implementation cannot be generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (!token.isInterface()) {
            throw new ImplerException("Token must be interface.");
        }

        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Interface must be not private.");
        }

        final Path path = root.resolve(
                token.getPackageName().replace(".", File.separator));

        try {
            Files.createDirectories(path);
        } catch (final IOException e) {
            throw new ImplerException("Can not create directories on the specified path.", e);
        }

        final Path output =
                path.resolve(token.getSimpleName() + "Impl.java");

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writePackage(token, writer);
            writeClassBody(token, writer);
        } catch (final IOException e) {
            delete(output);
            throw new ImplerException("Unexpected error while writing implementation.", e);
        }
    }

    /**
     * Converts to path leading from {@code root} to the file.
     * @param root root directory.
     * @param path path leading to file.
     * @return absolute resulting path.
     * @throws InvalidPathException
     *          if the path string cannot be converted to a Path.
     */
    private static Path getFile(Path root, String path) {
        return root.resolve(path).toAbsolutePath();
    }

    /**
     * Returns full implemented file name.
     * @param token type token to create implementation for.
     * @param extension file extension (<var>.java</var> for example).
     * @param separator separator in  file path.
     * @return full file name consists of {@link Class#getPackageName()},
     *          {@link Class#getSimpleName()} with {@code Impl} suffix
     *          and specified extension. All names are separated by
     *          {@link File#separator}.
     */
    private static String getFileName(Class<?> token,
                                      String extension, String separator) {
        return (token.getPackageName() + "." + token.getSimpleName())
                .replace(".", separator) + "Impl" + extension;
    }

    /**
     * Returns classpath of the type token.
     * @param token type token whose path need to get.
     * @return {@code token} classpath in the form of a string
     * @throws AssertionError if it is not possible to work with the
     *          required {@code URI}.
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Compile specified type token at runtime.
     * @param token type token to be compiled at runtime.
     * @param root root directory.
     * @throws NullPointerException if compiler could not be found.
     * @throws AssertionError if compiler exit code is not 0.
     */
    private void compile(Class<?> token, Path root) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new NullPointerException("Could not find java compiler, include tools.jar to classpath.");
        }
        final String file = getFile(root, getFileName(token, ".java", File.separator)).toString();
        final String classpath = root + File.pathSeparator + getClassPath(token);
        System.err.println(classpath);
        final String[] args = new String[] {file, "-cp", classpath, "-encoding", StandardCharsets.UTF_8.name()};
        if (compiler.run(null, null, null, args) != 0) {
            throw new AssertionError("Compiler exit code is not 0.");
        }
    }

    /**
     * Generate <var>.jar</var> implementation of a specified type token.
     * @param token type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException
     *          when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        final Path root = jarFile.getParent();

        implement(token, root);

        try {
            compile(token, root);
        } catch (final AssertionError | NullPointerException e) {
            throw new ImplerException(e.getMessage(), e);
        }

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(
                Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream jarOutputStream =
                     new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            final String file = getFileName(token, ".class", "/");
            jarOutputStream.putNextEntry(new ZipEntry(file));
            Files.copy(getFile(root, file), jarOutputStream);
        } catch (final IOException e) {
            throw new ImplerException("Unexpected error while writing .jar implementation.", e);
        } catch (final InvalidPathException e) {
            throw new ImplerException(String
                    .format("Path does not exists or not found by given path [%s].%n", e.getMessage()), e);
        }
    }
}
