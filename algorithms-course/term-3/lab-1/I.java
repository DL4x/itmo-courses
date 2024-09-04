import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
 
public class I {
    private static int maxColor;
    private static int[] d;
    private static int[] h;
    private static int[] colors;
    private static boolean[] visited;
    private static ArrayList<Integer>[] g;

    private static boolean isUnique(int x, int vertex) {
        return Collections.frequency(g[x], vertex) == 1;
    }

    private static void dfs1(int x, int p) {
        visited[x] = true;
        d[x] = h[x] = p != -1 ? h[p] + 1 : 0;
        for (int vertex : g[x]) {
            if (vertex != p) {
                if (visited[vertex]) {
                    d[x] = Math.min(d[x], h[vertex]);
                } else {
                    dfs1(vertex, x);
                    d[x] = Math.min(d[x], d[vertex]);
                }
            }
        }
    }

    private static void dfs2(int x, int color) {
        colors[x] = color;
        for (int vertex : g[x]) {
            if (colors[vertex] == 0) {
                if (h[x] < d[vertex] && isUnique(x, vertex)) {
                    maxColor++;
                    dfs2(vertex, maxColor);
                } else {
                    dfs2(vertex, color);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            try (MyScanner sc = new MyScanner(System.in)) {
                int n = sc.nextInt();
                int m = sc.nextInt();
                g = new ArrayList[n + 1];
                for (int i = 1; i <= n; i++) {
                    g[i] = new ArrayList<>();
                }
                for (int i = 0; i < m; i++) {
                    int u = sc.nextInt();
                    int v = sc.nextInt();
                    g[u].add(v);
                    g[v].add(u);
                }
                d = new int[n + 1];
                h = new int[n + 1];
                visited = new boolean[n + 1];
                for (int i = 1; i <= n; i++) {
                    if (!visited[i]) {
                        dfs1(i, -1);
                    }
                }
                colors = new int[n + 1];
                for (int i = 1; i <= n; i++) {
                    if (colors[i] == 0) {
                        maxColor++;
                        dfs2(i, maxColor);
                    }
                }
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(maxColor);
                for (int i = 1; i < colors.length; i++) {
                    writer.print(colors[i] + " ");
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
                return Integer.parseInt(strInt.toString());
            }
        }
        return Long.parseLong(strInt.toString());
    }
    // Methods Block (End)
}