import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class S {
    private static int[] d;
    private static int[] out;
    private static boolean[] visited;
    private static Queue<Integer> queue;
    private static ArrayList<Integer>[] g;

    private static void dfs(int v) {
        visited[v] = true;
        if (d[v] == 0) {
            for (int u : g[v]) {
                if (!visited[u]) {
                    if (d[u] == -1) {
                        d[u] = 1;
                        queue.add(u);
                    }
                }
            }
        } else {
            for (int u : g[v]) {
                if (!visited[u]) {
                    out[u]--;
                    if (out[u] == 0) {
                        d[u] = 0;
                        queue.add(u);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            try (MyScanner sc = new MyScanner(System.in)) {
                try (PrintWriter writer = new PrintWriter(System.out)) {
                    int t = sc.nextInt();
                    for (int i = 0; i < t; i++) {
                        int n = sc.nextInt();
                        int m = sc.nextInt();
                        d = new int[n + 1];
                        Arrays.fill(d, -1);
                        out = new int[n + 1];
                        visited = new boolean[n + 1];
                        g = new ArrayList[n + 1];
                        for (int j = 1; j <= n; j++) {
                            g[j] = new ArrayList<>();
                        }
                        for (int j = 0; j < m; j++) {
                            int u = sc.nextInt();
                            int v = sc.nextInt();
                            g[v].add(u);
                            out[u]++;
                        }
                        queue = new LinkedList<>();
                        for (int j = 1; j <= n; j++) {
                            if (out[j] == 0) {
                                d[j] = 0;
                                queue.add(j);
                            }
                        }
                        while (!queue.isEmpty()) {
                            dfs(queue.remove());
                        }
                        for (int j = 1; j <= n; j++) {
                            switch (d[j]) {
                                case 1 -> writer.println("FIRST");
                                case 0 -> writer.println("SECOND");
                                case -1 -> writer.println("DRAW");
                            }
                        }
                        writer.println();
                    }
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