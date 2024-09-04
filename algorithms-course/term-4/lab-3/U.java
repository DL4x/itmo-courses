import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.StringTokenizer;

public class U {
    private static class StringHash {
        private final long[] pow;
        private final long[] hash;

        public StringHash(String s) {
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

        public long getHash(int l, int r) {
            return (hash[r] - hash[l] * pow[r - l]) % Long.MAX_VALUE;
        }
    }

    private static int[] optimizedPrefixFunction(String s) {
        int n = s.length();
        int[] prefix = new int[n];
        for (int i = 1; i < n; i++) {
            int j = prefix[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = prefix[j - 1];
            }
            prefix[i] = s.charAt(i) == s.charAt(j) ? ++j : j;
        }
        return prefix;
    }

    private static void printString(String s1, String s2, String s3,
                                    int n, int inter1, int inter2, PrintWriter writer) {
        writer.print(s1);
        for (int i = inter1; i < n; i++) {
            writer.print(s2.charAt(i));
        }
        for (int i = inter2; i < n; i++) {
            writer.print(s3.charAt(i));
        }
        writer.println();
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final String p = sc.next();
            final String q = sc.next();
            final String r = sc.next();

            int intersectionPQ = optimizedPrefixFunction(q + "#" + p)[2 * n];
            int intersectionPR = optimizedPrefixFunction(r + "#" + p)[2 * n];
            int intersectionQP = optimizedPrefixFunction(p + "#" + q)[2 * n];
            int intersectionQR = optimizedPrefixFunction(r + "#" + q)[2 * n];
            int intersectionRP = optimizedPrefixFunction(p + "#" + r)[2 * n];
            int intersectionRQ = optimizedPrefixFunction(q + "#" + r)[2 * n];

            final int l = 3 * n;
            final int case1 = intersectionPQ + intersectionQR;
            final int case2 = intersectionPR + intersectionRQ;
            final int case3 = intersectionQP + intersectionPR;
            final int case4 = intersectionQR + intersectionRP;
            final int case5 = intersectionRP + intersectionPQ;
            final int case6 = intersectionRQ + intersectionQP;

            final int diff = Math.max(
                    case1,
                    Math.max(
                            case2,
                            Math.max(
                                    case3,
                                    Math.max(
                                            case4,
                                            Math.max(
                                                    case5,
                                                    case6
                                            )
                                    )
                            )
                    )
            );

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(l - diff);
                if (diff == case1) {
                    printString(p, q, r, n, intersectionPQ, intersectionQR, writer);
                } else if (diff == case2) {
                    printString(p, r, q, n, intersectionPR, intersectionRQ, writer);
                } else if (diff == case3) {
                    printString(q, p, r, n, intersectionQP, intersectionPR, writer);
                } else if (diff == case4) {
                    printString(q, r, p, n, intersectionQR, intersectionRP, writer);
                } else if (diff == case5) {
                    printString(r, p, q, n, intersectionRP, intersectionPQ, writer);
                } else {
                    printString(r, q, p, n, intersectionRQ, intersectionQP, writer);
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
                return Long.parseLong(strInt.toString());
            }
        }
        return Long.parseLong(strInt.toString());
    }
    // Methods Block (End)
}
