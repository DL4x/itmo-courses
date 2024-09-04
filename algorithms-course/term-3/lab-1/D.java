import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class D {
    private static int color;
    private static int[] colors;
    private static boolean[] visited;
    private static ArrayList<Integer>[] g;
    private static ArrayList<Integer>[] inv_g;
    private static ArrayList<Integer> tout;

    private static void dfs1(int x) {
        visited[x] = true;
        for (int vertex : g[x]) {
            if (!visited[vertex]) {
                dfs1(vertex);
            }
        }
        tout.add(x);
    }

    private static void dfs2(int x) {
        colors[x] = color;
        for (int vertex : inv_g[x]) {
            if (colors[vertex] == 0) {
                dfs2(vertex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                int m = sc.nextInt();
                colors = new int[n + 1];
                visited = new boolean[n + 1];
                g = new ArrayList[n + 1];
                inv_g = new ArrayList[n + 1];
                for (int i = 0; i <= n; i++) {
                    g[i] = new ArrayList<>();
                    inv_g[i] = new ArrayList<>();
                }
                int u, v;
                for (int i = 0; i < m; i++) {
                    u = sc.nextInt();
                    v = sc.nextInt();
                    g[u].add(v);
                    inv_g[v].add(u);
                }
                tout = new ArrayList<>();
                for (int i = 1; i <= n; i++) {
                    if (!visited[i]) {
                        dfs1(i);
                    }
                }
                Collections.reverse(tout);
                for (int i : tout) {
                    if (colors[i] == 0) {
                        color++;
                        dfs2(i);
                    }
                }
                AtomicInteger result = new AtomicInteger();
                Map<Integer, HashSet<Integer>> map = new HashMap<>();
                for (int i = 0; i <= color; i++) {
                    map.put(i, new HashSet<>());
                }
                for(int i = 1; i < g.length; i++) {
                    for (int vertex : g[i]) {
                        if (colors[i] != colors[vertex]) {
                            map.get(colors[i]).add(colors[vertex]);
                        }
                    }
                }
                map.values().forEach(set -> 
                        result.addAndGet(set.size())
                );
                System.out.println(result.get());
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class MyScanner {
    private final Reader reader;
 
    public MyScanner(InputStream stream) throws IOException {
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
 
    public void close() throws IOException {
        reader.close();
    }
 
    private int j;
 
    public String next() throws IOException {
        StringBuilder str = new StringBuilder();
        int i = j;
        while (i == 0 || Character.isWhitespace((char) i)) {
            i = reader.read();
        }
        while (i != 0 && !Character.isWhitespace((char) i)) {
            str.append((char) i);
            i = reader.read();
            if (i == -1) {
                return str.toString();
            }
        }
        return str.toString();
    }
 
    public int nextInt() throws IOException {
        StringBuilder strInt = new StringBuilder();
        int i = j;
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
}