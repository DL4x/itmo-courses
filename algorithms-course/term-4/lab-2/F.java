import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

public class F {
    private static final Comparator<Point> POINT_COMPARATOR = Comparator
            .comparing(Point::x)
            .thenComparing(Point::y);

    private record Point(long x, long y) {
        public static double distance(Point a, Point b) {
            return Math.sqrt((b.x - a.x) * (b.x - a.x) +
                    (b.y - a.y) * (b.y - a.y));
        }

        public boolean equals(final Point that) {
            return this.x == that.x && this.y == that.y;
        }

        public String toString() {
            return String.format("(%d %d)", x, y);
        }
    }

    private record Triangle(Point a, Point b, Point c) {
        public static long area(Point a, Point b, Point c) {
            return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        }
    }

    private static class ConvexHull {
        private final List<Point> convexHull;

        public ConvexHull(List<Point> points) {
            points.sort(POINT_COMPARATOR);

            List<Point> lower = new ArrayList<>(List.of(points.getFirst()));
            List<Point> upper = new ArrayList<>(List.of(points.getFirst()));

            for (int i = 1; i < points.size(); i++) {
                if (i == points.size() - 1 || Triangle.area(points.getFirst(), points.get(i), points.getLast()) > 0) {
                    while (lower.size() >= 2 &&
                            Triangle.area(lower.get(lower.size() - 2), lower.getLast(), points.get(i)) <= 0) {
                        lower.removeLast();
                    }
                    lower.add(points.get(i));
                }
                if (i == points.size() - 1 || Triangle.area(points.getFirst(), points.get(i), points.getLast()) < 0) {
                    while (upper.size() >= 2 &&
                            Triangle.area(upper.get(upper.size() - 2), upper.getLast(), points.get(i)) >= 0) {
                        upper.removeLast();
                    }
                    upper.add(points.get(i));
                }
            }

            convexHull = Stream.concat(
                    upper.stream(),
                    IntStream.iterate(lower.size() - 2, i -> i -1)
                            .limit(lower.size() - 2)
                            .mapToObj(lower::get)
            ).toList();
        }

        public List<Point> points() {
            return new ArrayList<>(convexHull);
        }

        public double perimeter() {
            double result = 0;
            for (int i = 0, n = convexHull.size(); i < n; i++) {
                final Point point1 = convexHull.get(i);
                final Point point2 = convexHull.get((i + 1) % n);
                result += Point.distance(point1, point2);
            }
            return result;
        }
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            List<Point> points = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                points.add(new Point(sc.nextInt(), sc.nextInt()));
            }

            final ConvexHull convexHull = new ConvexHull(points);

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(convexHull.perimeter());
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
