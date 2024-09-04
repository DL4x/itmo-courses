import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
 
public class F {
    private static int[] colors;
    private static boolean[] visited;
    private static ArrayList<Integer> tout;
    private static ArrayList<Integer>[] g;
    private static ArrayList<Integer>[] gInv;
 
    private static void dfs1(int x) {
        visited[x] = true;
        for (int vertex : g[x]) {
            if (!visited[vertex]) {
                dfs1(vertex);
            }
        }
        tout.add(x);
    }
 
    private static void dfs2(int x, int c) {
        colors[x] = c;
        for (int vertex : gInv[x]) {
            if (colors[vertex] == 0) {
                dfs2(vertex, c);
            }
        }
    }
 
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            int result = 0;
            ArrayList<String> answer = new ArrayList<>();;
 
            try (MyScanner sc = new MyScanner(System.in)) {
                int n = sc.nextInt();
                int size = 2 * n;
                int m = sc.nextInt();
                String[] name = new String[n];
                Map<String, Integer> index = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    name[i] = sc.next();
                    index.put('+' + name[i], i);
                    index.put('-' + name[i], i + n);
                }
                g = new ArrayList[size];
                gInv = new ArrayList[size];
                for (int i = 0; i < size; i++) {
                    g[i] = new ArrayList<>();
                    gInv[i] = new ArrayList<>();
                }
                for (int i = 0; i < m; i++) {
                    int first = index.get(sc.next());
                    int invFirst = first - n < 0 ? first + n : first - n;
                    sc.next();
                    int second = index.get(sc.next());
                    int invSecond = second - n < 0 ? second + n : second - n;
                    g[first].add(second);
                    gInv[second].add(first);
                    g[invSecond].add(invFirst);
                    gInv[invFirst].add(invSecond);
                }
                tout = new ArrayList<>();
                visited = new boolean[size];
                for (int i = 0; i < size; i++) {
                    if (!visited[i]) {
                        dfs1(i);
                    }
                }
                int color = 0;
                colors = new int[size];
                for (int i = size - 1; i >= 0; i--) {
                    if (colors[tout.get(i)] == 0) {
                        dfs2(tout.get(i), ++color);
                    }
                }
                for (int i = 0; i < n; i++) {
                    if (colors[i] == colors[i + n]) {
                        result = -1;
                        break;
                    }
                }
                if (result == 0) {
                    for (int i = 0; i < n; i++) {
                        if (colors[i] > colors[i + n]) {
                            result++;
                            answer.add(name[i]);
                        }
                    }
                }
            }
 
            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(result);
                answer.forEach(writer::println);
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