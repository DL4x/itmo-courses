import java.io.*;
import java.nio.charset.StandardCharsets;
 
public class A {
    private static long[] pow;
    private static long[] hash;
 
    private static void setHash(String s) {
        final int n = s.length();
        pow = new long[n + 1];
        pow[0] = 1;
        hash = new long[n + 1];
        hash[0] = 0;
        for (int i = 0; i < n; i++) {
            pow[i + 1] = (pow[i] * 31) % Long.MAX_VALUE;
            hash[i + 1] = (hash[i] * 31 + s.charAt(i)) % Long.MAX_VALUE;
        }
    }
 
    private static long getHash(int l, int r) {
        return (hash[r] - hash[l] * pow[r - l]) % Long.MAX_VALUE;
    }
 
    public static void main(String[] args) {
        try {
            try (MyScanner sc = new MyScanner(System.in)) {
                String s = sc.next();
                setHash(s);
                int m = sc.nextInt();
                try (PrintWriter writer = new PrintWriter(System.out)) {
                    for (int i = 0; i < m; i++) {
                        int a = sc.nextInt();
                        int b = sc.nextInt();
                        int c = sc.nextInt();
                        int d = sc.nextInt();
                        writer.print((getHash(a - 1, b) == getHash(c - 1, d) ? "Yes" : "No") + System.lineSeparator());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
 
class MyScanner implements Closeable {
    // Reader Block (Start)
    private final Reader reader;
 
    public MyScanner(InputStream stream) throws IOException {
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
 
    public MyScanner(File file) throws FileNotFoundException, IOException {
        reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
    }
 
    @Override
    public void close() throws IOException {
        reader.close();
    }
    // Reader Block (End)
 
    // Methods Block (Start)
    private int WHITESPACE_CODE = 32;
 
    public boolean hasNext() throws IOException {
        int i = reader.read();
        while (Character.isWhitespace((char) i) && i != -1) {
            i = reader.read();
        }
        WHITESPACE_CODE = i;
        return !Character.isWhitespace((char) i) && i != -1;
    }
 
    public String next() throws IOException {
        int i = WHITESPACE_CODE;
        StringBuilder str = new StringBuilder();
        while (Character.isWhitespace((char) i)) {
            i = reader.read();
        }
        while (!Character.isWhitespace((char) i)) {
            str.append((char) i);
            i = reader.read();
            if (i == -1) {
                return str.toString();
            }
        }
        return str.toString();
    }
 
    public int nextInt() throws IOException {
        int i = WHITESPACE_CODE;
        StringBuilder strInt = new StringBuilder();
        while (!Character.isDigit((char) i) && (char) i != '-') {
            i = reader.read();
        }
        while (Character.isDigit((char) i) || (char) i == '-') {
            strInt.append((char) i);
            i = reader.read();
            if (i == -1) {
                return Integer.parseInt(strInt.toString());
            }
        }
        return Integer.parseInt(strInt.toString());
    }
 
    public long nextLong() throws IOException {
        int i = WHITESPACE_CODE;
        StringBuilder strInt = new StringBuilder();
        while (!Character.isDigit((char) i) && (char) i != '-') {
            i = reader.read();
        }
        while (Character.isDigit((char) i) || (char) i == '-') {
            strInt.append((char) i);
            i = reader.read();
            if (i == -1) {
                return Integer.parseInt(strInt.toString());
            }
        }
        return Long.parseLong(strInt.toString());
    }
    // Methods Block (End)
}
