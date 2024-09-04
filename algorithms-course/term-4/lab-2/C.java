import java.io.*;
import java.nio.charset.StandardCharsets;

public class C {
    private record Point(int x, int y) {
        public static double distance(Point a, Point b) {
            return Math.sqrt((b.x - a.x) * (b.x - a.x) +
                    (b.y - a.y) * (b.y - a.y));
        }

        public boolean equals(final Point that) {
            return this.x == that.x && this.y == that.y;
        }
    }

    private record Segment(Point a, Point b) {
        public boolean equals(final Segment that) {
            return this.a.equals(that.a) && this.b.equals(that.b);
        }
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final Point p = new Point(sc.nextInt(), sc.nextInt());
            final Segment ab = new Segment(
                    new Point(sc.nextInt(), sc.nextInt()),
                    new Point(sc.nextInt(), sc.nextInt())
            );

            final double distance1 = Point.distance(p, ab.a);
            final double distance2 = Point.distance(p, ab.b);

            double distance3 = Double.MAX_VALUE;

            double length = Point.distance(ab.a, ab.b);

            double dist1 = distance1 * distance1;
            double dist2 = distance2 * distance2;
            double dist3 = length * length;

            if (!ab.a.equals(ab.b) && !(dist1 > dist2 + dist3 || dist2 > dist1 + dist3)) {
                int a = ab.a.y - ab.b.y;
                int b = ab.b.x - ab.a.x;
                int c = ab.a.x * ab.b.y - ab.b.x * ab.a.y;

                distance3 = Math.abs(a * p.x + b * p.y + c) / Math.sqrt(a * a + b * b);
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(Math.min(Math.min(distance1, distance2), distance3));
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
