import java.io.*;
import java.nio.charset.StandardCharsets;

public class I {
    private record Point(int x, int y) {
        public boolean equals(final Point that) {
            return this.x == that.x && this.y == that.y;
        }

        public String toString() {
            return String.format("(%d %d)", x, y);
        }
    }

    private static int sign(int x) {
        return x >= 0 ? 1 : -1;
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            Point[] polygon = new Point[n];
            for (int i = 0; i < n; i++) {
                polygon[i] = new Point(sc.nextInt(), sc.nextInt());
            }
            int[] turns = new int[n];
            for (int i = 0; i < n; i++) {
                final Point point1 = polygon[i];
                final Point point2 = polygon[(i + 1) % n];
                final Point point3 = polygon[(i + 2) % n];
                turns[i] =  (point2.x - point1.x) * (point3.y - point2.y) -
                        (point2.y - point1.y) * (point3.x - point2.x);
            }

            boolean result = true;
            for (int i = 0; i < n - 1; i++) {
                if (sign(turns[i]) != sign(turns[i + 1])) {
                    result = false;
                    break;
                }
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(result ? "YES" : "NO");
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
