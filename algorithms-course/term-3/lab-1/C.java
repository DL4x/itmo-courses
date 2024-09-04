import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class C {
	private static int color = 1;
    private static int[] colors;
    private static ArrayList<Integer>[] g;

    private static void dfs(int x) {
        colors[x] = color;
        for (int vertex : g[x]) {
            if (colors[vertex] == 0) {
                dfs(vertex);
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
                g = new ArrayList[n + 1];
                for (int i = 0; i <= n; i++) {
                    g[i] = new ArrayList<>();
                }
                int u, v;
                for (int i = 0; i < m; i++) {
                    u = sc.nextInt();
                    v = sc.nextInt();
                    g[u].add(v);
                    g[v].add(u);
                }
                for (int i = 1; i <= n; i++) {
                    if (colors[i] == 0) {
                        dfs(i);
                        color++;
                    }
                }
                for (int i = 1; i <= n; i++) {
                    System.out.print(colors[i] + " ");
                }
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