import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class A {
    private static List<Integer>[][] weights;
    private static Pair<Integer, Integer>[] path;

    private static int bfs(int s, int t) {
        int flow = Integer.MAX_VALUE;
        Arrays.fill(path, null);
        Queue<Integer> queue = new ArrayDeque<>(List.of(s));

        while (!queue.isEmpty()) {
            final int v = queue.poll();
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[v][i].size(); j++) {
                    if (path[i] == null && weights[v][i].get(j) > 0) {
                        path[i] = new Pair<>(v, j);
                        flow = Math.min(flow, weights[v][i].get(j));
                        if (i == t) {
                            return flow;
                        }
                        queue.offer(i);
                    }
                }
            }
        }

        return 0;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            final int s = 0;
            final int t = n - 1;
            weights = new List[n][n];
            int[][] edges = new int[m][];
            List<Pair<Integer, Boolean>>[][] flows = new List[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++)  {
                    flows[i][j] = new ArrayList<>();
                    weights[i][j] = new ArrayList<>();
                }
            }
            for (int i = 0; i < m; i++) {
                int a = sc.nextInt() - 1;
                int b = sc.nextInt() - 1;
                int c = sc.nextInt();
                flows[a][b].add(new Pair<>(0, true));
                flows[b][a].add(new Pair<>(0, false));
                edges[i] = new int[]{a, b, weights[a][b].size()};
                weights[a][b].add(c);
                weights[b][a].add(c);
            }

            int w;
            int result = 0;
            path = new Pair[n];
            while ((w = bfs(s, t)) != 0) {
                int v = t;
                result += w;
                while (v != s) {
                    int p = path[v].first;
                    int index = path[v].second;
                    flows[p][v].get(index).first =
                            flows[p][v].get(index).first + (flows[p][v].get(index).second ? w : -w);
                    weights[p][v].set(index, weights[p][v].get(index) - w);
                    weights[v][p].set(index, weights[v][p].get(index) + w);
                    v = p;
                }
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(result);
                for (int[] edge : edges) {
                    int a = edge[0];
                    int b = edge[1];
                    int i = edge[2];
                    writer.println(flows[a][b].get(i).first != 0
                            ? flows[a][b].get(i).first
                            : flows[b][a].get(i).first);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
