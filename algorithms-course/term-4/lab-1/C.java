import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.nio.charset.StandardCharsets;

public class C {
    private static int[][] path;
    private static boolean[] visited;
    private static ArrayList<Node>[] g;

    private static class Node {
        int v;
        int c;
        int xIndex;
        int yIndex;
        boolean direct;

        public Node(int v, int c, int xIndex, int yIndex, boolean direct) {
            this.v = v;
            this.c = c;
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.direct = direct;
        }

        @Override
        public String toString() {
            return String.format("{%d, %d}", v, c);
        }
    }

    private static void clear() {
        Arrays.fill(path, null);
        Arrays.fill(visited, false);
    }

    private static void writeReversedPath(PrintWriter writer, List<Integer> path) {
        IntStream.iterate(path.size() - 1, i -> i - 1)
                .limit(path.size())
                .map(path::get)
                .forEach(x -> writer.print(++x + " "));
        writer.println();
    }

    private static int bfs(int s, int t) {
        clear();
        path[s] = new int[] {};
        int flow = Integer.MAX_VALUE;
        Queue<Integer> queue = new ArrayDeque<>(List.of(s));

        while (!queue.isEmpty()) {
            final int v = queue.poll();
            visited[v] = true;
            for (Node edge : g[v]) {
                int u = edge.v;
                if (visited[u]) {
                    continue;
                }
                if (path[u] == null && edge.c > 0) {
                    path[u] = new int[] {v, edge.xIndex, edge.yIndex};
                    flow = Math.min(flow, edge.c);
                    if (u == t) {
                        return flow;
                    }
                    queue.offer(u);
                }
            }
        }

        return 0;
    }

    private static List<Integer> findPath(int s, int t) {
        clear();
        path[s] = new int[] {};
        List<Integer> result = new ArrayList<>();
        Queue<Integer> queue = new ArrayDeque<>(List.of(s));

        while (!queue.isEmpty()) {
            final int v = queue.poll();
            for (Node edge : g[v]) {
                int u = edge.v;
                if (!edge.direct) {
                    continue;
                }
                if (path[u] == null && edge.c != 1) {
                    path[u] = new int[] {v, edge.xIndex, edge.yIndex};
                    if (u == t) {
                        int x = t;
                        result.add(x);
                        while (x != s) {
                            int p = path[x][0];
                            int xIndex = path[x][1];
                            int yIndex = path[x][2];
                            g[p].get(xIndex).direct = false;
                            g[x].get(yIndex).direct = false;
                            x = p;
                            result.add(x);
                        }
                        return result;
                    }
                    queue.offer(u);
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            final int s = sc.nextInt() - 1;
            final int t = sc.nextInt() - 1;
            path = new int[n][];
            visited = new boolean[n];
            g = new ArrayList[n];
            Arrays.setAll(g, ignored -> new ArrayList<>());
            for (int i = 0; i < m; i++) {
                final int x = sc.nextInt() - 1;
                final int y = sc.nextInt() - 1;
                final int xIndex = g[x].size();
                final int yIndex = g[y].size();
                g[x].add(new Node(y, 1, xIndex, yIndex, true));
                g[y].add(new Node(x, 0, yIndex, xIndex, false));
            }
            int w;
            int result = 0;
            while ((w = bfs(s, t)) != 0) {
                int v = t;
                result += w;
                while (v != s) {
                    int p = path[v][0];
                    int xIndex = path[v][1];
                    int yIndex = path[v][2];
                    g[p].get(xIndex).c -= w;
                    g[v].get(yIndex).c += w;
                    v = p;
                }
            }

            boolean status = result >= 2;
            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(status ? "YES" : "NO");
                if (status) {
                    writeReversedPath(writer, Objects.requireNonNull(findPath(s, t)));
                    writeReversedPath(writer, Objects.requireNonNull(findPath(s, t)));
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
