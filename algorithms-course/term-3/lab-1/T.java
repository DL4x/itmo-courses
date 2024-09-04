import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
 
public class T {
    private static int[] grundy;
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
 
    private static void g(int x) {
        Set<Integer> set = g[x].stream().map(v -> grundy[v])
                .collect(Collectors.toCollection(TreeSet::new));
        int count = 0;
        for (int v : set) {
            if (count != v) {
                break;
            }
            count++;
        }
        grundy[x] = count;
    }
 
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                int m = sc.nextInt();
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
                grundy = new int[n + 1];
                sorted.forEach(T::g);
                IntStream.range(1, n + 1).forEach(
                        i -> System.out.println(grundy[i]));
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