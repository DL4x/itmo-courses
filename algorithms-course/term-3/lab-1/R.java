import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class R {
    private static boolean[] win;
    private static boolean[] visited;
    private static ArrayList<Integer> sorted;
    private static ArrayList<Integer>[] g;

    private static void topsort(int x) {
        visited[x] = true;
        for (int vertex : g[x]) {
            if (!visited[vertex]) {
                topsort(vertex);
            }
        }
        sorted.add(x);
    }

    private static void victory(int x) {
        g[x].forEach(vertex -> win[x] |= !win[vertex]);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                int m = sc.nextInt();
                int s = sc.nextInt();
                visited = new boolean[n + 1];
                sorted = new ArrayList<>();
                g = new ArrayList[n + 1];
                for (int i = 0; i <= n; i++) {
                    g[i] = new ArrayList<>();
                }
                for (int i = 0; i < m; i++) {
                    int u = sc.nextInt();
                    int v = sc.nextInt();
                    g[u].add(v);
                }
                for (int i = 1; i <= n; i++) {
                    if (!visited[i]) {
                        topsort(i);
                    }
                }
                win = new boolean[n + 1];
                sorted.forEach(R::victory);
                System.out.println(win[s]
                        ? "First player wins"
                        : "Second player wins");
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