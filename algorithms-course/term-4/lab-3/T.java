import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class T {
    private static long[] d;
    private static ArrayList<Edge>[] g;
 
    private record Edge(int v, int a, int b, int d) {
        @Override
        public String toString() {
            return String.format("(%d %d %d)", a, b, d);
        }
    }
 
    private static void dijkstra(int s) {
        d[s] = 0;
        PriorityQueue<Pair<Long, Integer>> queue =
                new PriorityQueue<>(List.of(new Pair<>(0L, s)));
        while (!queue.isEmpty()) {
            var pair = queue.poll();
            final long x = pair.first;
            final int u = pair.second;
            if (x > d[u]) {
                continue;
            }
            for (var edge : g[u]) {
                if (edge.a < edge.d) {
                    continue;
                }
                if (x % (edge.a + edge.b) + edge.d <= edge.a
                        && d[edge.v] > x + edge.d) {
                    d[edge.v] = x + edge.d;
                    queue.offer(new Pair<>(d[edge.v], edge.v));
                    continue;
                }
                final long time = (long) Math.ceil((double) x
                        / (edge.a + edge.b)) * (edge.a + edge.b) + edge.d;
                if (d[edge.v] > time) {
                    d[edge.v] = time;
                    queue.offer(new Pair<>(d[edge.v], edge.v));
                }
            }
        }
    }
 
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in);
             PrintWriter writer = new PrintWriter(System.out)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            final int s = sc.nextInt();
            final int t = sc.nextInt();
 
            d = new long[n + 1];
            Arrays.fill(d, Long.MAX_VALUE);
            g = new ArrayList[n + 1];
            Arrays.setAll(g, ignored -> new ArrayList<>());
 
            for (int i = 0; i < m; i++) {
                final int u = sc.nextInt();
                final int v = sc.nextInt();
                final int a = sc.nextInt();
                final int b = sc.nextInt();
                final int d = sc.nextInt();
                g[u].add(new Edge(v, a, b, d));
                g[v].add(new Edge(u, a, b, d));
            }
 
            dijkstra(s);
 
            writer.println(d[t] != Long.MAX_VALUE ? d[t] : -1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Pair<T1 extends Comparable<T1>,
        T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {

    public T1 first;
    public T2 second;

    public Pair(T1 u1, T2 u2) {
        this.first = u1;
        this.second = u2;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }

    @Override
    public int compareTo(Pair<T1, T2> pair) {
        int result = this.first.compareTo(pair.first);
        return result == 0 ? this.second.compareTo(pair.second) : result;
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
