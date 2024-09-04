import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
 
public class G {
    private static int[] d;
    private static Pair<Integer, Integer>[] path;
    private static ArrayList<Pair<Integer, Integer>>[][] g;
 
    private static void clear() {
        Arrays.fill(d, Integer.MAX_VALUE);
    }
 
    private static boolean dijkstra(int s, int t) {
        clear();
        d[s] = 0;
        PriorityQueue<Pair<Integer, Integer>> queue
                = new PriorityQueue<>(List.of(new Pair<>(0, s)));
        while (!queue.isEmpty()) {
            var edge = queue.poll();
            int v = edge.second;
            if (edge.first > d[v]) {
                continue;
            }
            for (int u = 0; u < g.length; u++) {
                for (int i = 0; i < g[v][u].size(); i++) {
                    int w = g[v][u].get(i).second;
                    if (g[v][u].get(i).first <= 0) {
                        continue;
                    }
                    if (d[u] > d[v] + w) {
                        path[u] = new Pair<>(v, i);
                        d[u] = d[v] + w;
                        queue.offer(new Pair<>(d[u], u));
                    }
                }
            }
        }
        return d[t] < Integer.MAX_VALUE;
    }
 
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            final int s = 0;
            final int t = n - 1;
            d = new int[n];
            path = new Pair[n];
            g = new ArrayList[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    g[i][j] = new ArrayList<>();
                }
            }
            for (int i = 0; i < m; i++) {
                int a = sc.nextInt() - 1;
                int b = sc.nextInt() - 1;
                int c = sc.nextInt();
                int w = sc.nextInt();
                g[a][b].add(new Pair<>(c, w));
                g[b][a].add(new Pair<>(0, -w));
            }
            int v;
            long result = 0;
            while (dijkstra(s, t)) {
                v = t;
                int flow = Integer.MAX_VALUE;
                while (v != s) {
                    int p = path[v].first;
                    int index = path[v].second;
                    flow = Math.min(flow, g[p][v].get(index).first);
                    v = p;
                }
                v = t;
                while (v != s) {
                    int p = path[v].first;
                    int index = path[v].second;
                    g[p][v].get(index).first -= flow;
                    g[v][p].get(index).first += flow;
                    result += g[p][v].get(index).second * (long) flow;
                    v = p;
                }
            }
            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(result);
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
