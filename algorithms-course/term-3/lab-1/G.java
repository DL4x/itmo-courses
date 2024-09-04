import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
 
public class G {
    private static int[] d;
    private static int[] h;
    private static boolean[] visited;
    private static Set<Integer> result;
    private static ArrayList<Pair<Integer, Integer>>[] g;
 
    private static void dfs(int x, int p) {
        visited[x] = true;
        d[x] = h[x] = p != -1 ? h[p] + 1 : 0;
        for (Pair<Integer, Integer> vertex : g[x]) {
            if (vertex.first != p) {
                if (visited[vertex.first]) {
                    d[x] = Math.min(d[x], h[vertex.first]);
                } else {
                    dfs(vertex.first, x);
                    d[x] = Math.min(d[x], d[vertex.first]);
                    if (h[x] < d[vertex.first]) {
                        result.add(vertex.second);
                    }
                }
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
                d = new int[n + 1];
                h = new int[n + 1];
                visited = new boolean[n + 1];
                result = new TreeSet<>();
                g = new ArrayList[n + 1];
                for (int i = 0; i <= n; i++) {
                    g[i] = new ArrayList<>();
                }
                int u, v;
                for (int i = 1; i <= m; i++) {
                    u = sc.nextInt();
                    v = sc.nextInt();
                    g[u].add(new Pair<>(v, i));
                    g[v].add(new Pair<>(u, i));
                }
                for (int i = 1; i <= n; i++) {
                    if (!visited[i]) {
                        dfs(i, -1);
                    }
                }
                System.out.println(result.size());
                for (int bridge : result) {
                    System.out.println(bridge + " ");
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
 
class Pair<T1, T2> {
    public T1 first;
    public T2 second;
 
    public Pair(T1 u1, T2 u2) {
        this.first = u1;
        this.second = u2;
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