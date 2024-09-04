import java.io.*;
import java.nio.charset.StandardCharsets;

public class B {
    private record Point(int x, int y) {
        public boolean equals(final Point that) {
            return this.x == that.x && this.y == that.y;
        }
    }

    private record Vector(Point a, Point b) {
        public static int det(Vector vec1, Vector vec2) {
            return (vec1.b.x - vec1.a.x) * (vec2.b.y - vec2.a.y) -
                    (vec1.b.y - vec1.a.y) * (vec2.b.x - vec2.a.x);
        }

        public static int scalar(Vector vec1, Vector vec2) {
            return (vec1.b.x - vec1.a.x) * (vec2.b.x - vec2.a.x) +
                    (vec1.b.y - vec1.a.y) * (vec2.b.y - vec2.a.y);
        }

        public boolean equals(final Vector that) {
            return this.a.equals(that.a) && this.b.equals(that.b);
        }
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final Point p = new Point(sc.nextInt(), sc.nextInt());
            final Point a = new Point(sc.nextInt(), sc.nextInt());
            final Point b = new Point(sc.nextInt(), sc.nextInt());

            final Vector ab = new Vector(a, b);
            final Vector ap = new Vector(a, p);
            final Vector bp = new Vector(b, p);

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(Vector.det(ab, ap) == 0 &&
                        Vector.scalar(ap, bp) <= 0 ? "YES" : "NO");
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
