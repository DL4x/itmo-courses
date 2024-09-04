import java.io.*;
import java.nio.charset.StandardCharsets;

public class L {
    private record Point(int x, int y) {
        public boolean equals(final Point that) {
            return this.x == that.x && this.y == that.y;
        }

        public String toString() {
            return String.format("(%d %d)", x, y);
        }
    }

    private record Line(int a, int b, int c) {
        private static double deviation(Line line, Point point) {
            return (line.a * point.x + line.b * point.y + line.c)
                    / Math.sqrt(line.a * line.a + line.b * line.b);
        }

        /**
         * The method finds the position of
         * a given point relative to a given straight line.
         * @param point point whose position need to find out.
         * @return
         * <ul>
         *     <li>1 if origin and point are on one side of line;</li>
         *     <li>0 if point lies on line;</li>
         *     <li>-1 if origin and point are on different sides of line.</li>
         * </ul>
         */
        public static int position(Line line, Point point) {
            final double delta = deviation(line, point);
            if (delta == 0) {
                return 0;
            }
            return delta > 0 ? 1 : -1;
        }
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            Point a = new Point(sc.nextInt(), sc.nextInt());
            Point b = new Point(sc.nextInt(), sc.nextInt());
            Line line = new Line(sc.nextInt(), sc.nextInt(), sc.nextInt());

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(Line.position(line, a) ==
                        Line.position(line, b) ? "YES" : "NO");
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
