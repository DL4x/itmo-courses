import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class H {
    private static final int K = 53;
    private static final int SIZE = 220;

    private static int[] d;
    private static int[] path;
    private static Pair<Integer, Integer>[][] g;

    private static int encode(final char ch) {
        return (ch >= 'a' ? ch - 'a' : ch - 'A' + 26) + 1;
    }

    private static char decode(final int x) {
        return (char) ((x <= 26 ? x + 'a' : x + 'A' - 26) - 1);
    }

    private static void clear() {
        Arrays.fill(d, Integer.MAX_VALUE);
        Arrays.fill(path, Integer.MAX_VALUE);
    }

    private static boolean dijkstra(int s, int t) {
        clear();
        d[s] = 0;
        PriorityQueue<Pair<Integer, Integer>> queue
                = new PriorityQueue<>(List.of(new Pair<>(0, s)));

        while (!queue.isEmpty()) {
            final var edge = queue.poll();
            int v = edge.second;
            if (edge.first > d[v]) {
                continue;
            }
            for (int u = 0; u < g.length; u++) {
                if (g[v][u] == null) {
                    continue;
                }
                int w = g[v][u].second;
                if (g[v][u].first <= 0) {
                    continue;
                }
                if (d[u] > d[v] + w) {
                    path[u] = v;
                    d[u] = d[v] + w;
                    queue.offer(new Pair<>(d[u], u));
                }
            }
        }

        return d[t] < Integer.MAX_VALUE;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int k = sc.nextInt();

            final int s = 2 * k + 1;
            final int t = 2 * k + 2;

            d = new int[SIZE];
            path = new int[SIZE];
            g = new Pair[SIZE][SIZE];

            String code = sc.next();
            String answer = sc.next();

            int[][] count = new int[K][K];
            for (int i = 0; i < n; i++) {
                count[encode(code.charAt(i))][encode(answer.charAt(i))]++;
            }

            Pair<Integer, Integer>[][] id = new Pair[K][K];
            for (int i = 1; i <= k; i++) {
                for (int j = 1; j <= k; j++) {
                    id[i][j] = new Pair<>(i, j + k);
                    g[i][j + k] = new Pair<>(1, -count[i][j]);
                    g[j + k][i] = new Pair<>(0, count[i][j]);
                }
            }

            for (int i = 1; i <= k; i++) {
                g[s][i] = new Pair<>(1, 0);
                g[i][s] = new Pair<>(0, 0);
            }
            for (int i = 1; i <= k; i++) {
                g[i + k][t] = new Pair<>(1, 0);
                g[t][i + k] = new Pair<>(0, 0);
            }

            int v;
            long result = 0;
            while (dijkstra(s, t)) {
                v = t;
                int flow = Integer.MAX_VALUE;
                while (v != s) {
                    int p = path[v];
                    flow = Math.min(flow, g[p][v].first);
                    v = p;
                }
                v = t;
                while (v != s) {
                    int p = path[v];
                    g[p][v].first -= flow;
                    g[v][p].first += flow;
                    result += g[p][v].second * (long) flow;
                    v = p;
                }
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(-result);
                for (int i = 1; i <= k; i++) {
                    for (int j = 1; j <= k; j++) {
                        var p = id[i][j];
                        if (g[p.first][p.second].first == 0) {
                            writer.print(decode(j));
                        }
                    }
                }
                writer.println();
            }
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
