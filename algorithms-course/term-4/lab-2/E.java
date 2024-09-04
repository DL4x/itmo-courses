import java.io.*;
import java.nio.charset.StandardCharsets;

public class E {
    private record Point(long x, long y) {
        public static double distance(Point a, Point b) {
            return Math.sqrt((b.x - a.x) * (b.x - a.x) +
                    (b.y - a.y) * (b.y - a.y));
        }

        public boolean equals(final Point that) {
            return this.x == that.x && this.y == that.y;
        }
    }

    private record Segment(Point a, Point b) {
        public static boolean intersect(Segment segment1, Segment segment2) {
            final long min1 = Math.min(segment1.a.x, segment1.b.x);
            final long max1 = Math.max(segment1.a.x, segment1.b.x);
            final long min2 = Math.min(segment2.a.x, segment2.b.x);
            final long max2 = Math.max(segment2.a.x, segment2.b.x);

            final long min3 = Math.min(segment1.a.y, segment1.b.y);
            final long max3 = Math.max(segment1.a.y, segment1.b.y);
            final long min4 = Math.min(segment2.a.y, segment2.b.y);
            final long max4 = Math.max(segment2.a.y, segment2.b.y);

            final long area1 = Triangle.area(segment1.a, segment1.b, segment2.a);
            final long area2 = Triangle.area(segment1.a, segment1.b, segment2.b);
            final long area3 = Triangle.area(segment2.a, segment2.b, segment1.a);
            final long area4 = Triangle.area(segment2.a, segment2.b, segment1.b);

            return Math.max(min1, min2) <= Math.min(max1, max2)
                    && Math.max(min3, min4) <= Math.min(max3, max4)
                    && area1 * area2 <= 0 && area3 * area4 <= 0;
        }

        public boolean equals(final Segment that) {
            return this.a.equals(that.a) && this.b.equals(that.b);
        }
    }

    private record Triangle(Point a, Point b, Point c) {
        public static long area(Point a, Point b, Point c) {
            return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        }
    }

    private static double pointSegmentDistance(Point point, Segment segment) {
        final double distance1 = Point.distance(point, segment.a);
        final double distance2 = Point.distance(point, segment.b);

        double distance3 = Double.MAX_VALUE;

        double length = Point.distance(segment.a, segment.b);

        double dist1 = distance1 * distance1;
        double dist2 = distance2 * distance2;
        double dist3 = length * length;

        if (!segment.a.equals(segment.b) && !(dist1 > dist2 + dist3 || dist2 > dist1 + dist3)) {
            long a = segment.a.y - segment.b.y;
            long b = segment.b.x - segment.a.x;
            long c = segment.a.x * segment.b.y - segment.b.x * segment.a.y;

            distance3 = Math.abs(a * point.x + b * point.y + c) / Math.sqrt(a * a + b * b);
        }

        return Math.min(Math.min(distance1, distance2), distance3);
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final Segment segment1 = new Segment(
                    new Point(sc.nextLong(), sc.nextLong()),
                    new Point(sc.nextLong(), sc.nextLong())
            );
            final Segment segment2 = new Segment(
                    new Point(sc.nextLong(), sc.nextLong()),
                    new Point(sc.nextLong(), sc.nextLong())
            );

            try (PrintWriter writer = new PrintWriter(System.out)) {
                if (Segment.intersect(segment1, segment2)) {
                    writer.println(0.0);
                } else {
                   writer.println(
                            Math.min(
                                    Math.min(
                                            Math.min(
                                                    pointSegmentDistance(segment1.a, segment2),
                                                    pointSegmentDistance(segment1.b, segment2)
                                            ), pointSegmentDistance(segment2.a, segment1)
                                    ), pointSegmentDistance(segment2.b, segment1)
                            )
                    );
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
