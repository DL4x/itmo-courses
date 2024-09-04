import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class O {
    private static int[][] g;
    private static int color;
    private static int[] colors;
    private static boolean[] visited;
    private static ArrayList<Integer> tout;

    private static void dfs1(int x, int cost) {
        visited[x] = true;
        for (int i = 0; i < g.length; i++) {
            if (!visited[i] && g[x][i] <= cost) {
                dfs1(i, cost);
            }
        }
        tout.add(x);
    }

    private static void dfs2(int x, int cost) {
        colors[x] = color;
        for (int i = 0; i < g.length; i++) {
            if (colors[i] == 0 && g[i][x] <= cost) {
                dfs2(i, cost);
            }
        }
    }

    private static boolean check(int n, int cost) {
        color = 0;
        tout.clear();
        Arrays.fill(colors, 0);
        Arrays.fill(visited, false);
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs1(i, cost);
            }
        }
        Collections.reverse(tout);
        for (int i : tout) {
            if (colors[i] == 0) {
                color++;
                dfs2(i, cost);
            }
        }
        return color == 1;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            int result = 0;

            try (MyScanner sc = new MyScanner(System.in)) {
                int n = sc.nextInt();
                g = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        g[i][j] = sc.nextInt();
                    }
                }
                colors = new int[n];
                visited = new boolean[n];
                tout = new ArrayList<>();
                int l = 0;
                int r = Integer.MAX_VALUE;
                while (l < r) {
                    int mid = l + (r - l) / 2;
                    if (check(n, mid)) {
                        r = mid;
                    } else {
                        l = mid + 1;
                    }
                }
                result = r;
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.print(result);
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