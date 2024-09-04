import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class B {
    private static int[] path;
    private static int[][] g;
    private static boolean[] visited;

    private static void clear() {
        Arrays.fill(path, 0);
        Arrays.fill(visited, false);
    }

    private static void dfs(int v) {
        visited[v] = true;
        for (int u = 0; u < g.length; u++) {
            if (visited[u]) {
                continue;
            }
            if (g[v][u] > 0) {
                dfs(u);
            }
        }
    }

    private static int bfs(int s, int t) {
        clear();
        int flow = Integer.MAX_VALUE;
        Queue<Integer> queue = new ArrayDeque<>(List.of(s));

        while (!queue.isEmpty()) {
            final int v = queue.poll();
            visited[v] = true;
            for (int u = 0; u < g.length; u++) {
                if (visited[u]) {
                    continue;
                }
                if (path[u] == 0 && g[v][u] > 0) {
                    path[u] = v;
                    flow = Math.min(flow, g[v][u]);
                    if (u == t) {
                        return flow;
                    }
                    queue.offer(u);
                }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            final int s = 0;
            final int t = n - 1;
            path = new int[n];
            visited = new boolean[n];
            g = new int[n][n];
            int[][] edges = new int[m][];
            for (int i = 0; i < m; i++) {
                int a = sc.nextInt() - 1;
                int b = sc.nextInt() - 1;
                int c = sc.nextInt();
                g[a][b] = c;
                g[b][a] = c;
                edges[i] = new int[]{a, b};
            }

            int w;
            int result = 0;
            while ((w = bfs(s, t)) != 0) {
                int v = t;
                result += w;
                while (v != s) {
                    int p = path[v];
                    g[p][v] -= w;
                    g[v][p] += w;
                    v = p;
                }
            }

            clear();
            dfs(s);
            List<Integer> cut = new ArrayList<>();
            for (int i = 0; i < edges.length; i++) {
                int a = edges[i][0];
                int b = edges[i][1];
                if (visited[a] ^ visited[b]) {
                    cut.add(i + 1);
                }
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(cut.size() + " " + result);
                cut.forEach(x -> writer.print(x + " "));
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
