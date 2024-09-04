import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.nio.charset.StandardCharsets;

public class M {
    private static int[][] dist;
    private static ArrayList<Pair<Integer, Integer>>[] g;

    private static void fordBellman(int n, int k, int s) {
        dist[s][0] = 0;
        for (int i = 1; i <= k; i++) {
            for (int u = 1; u <= n; u++) {
                for (Pair<Integer, Integer> edge : g[u]) {
                    if (dist[u][i - 1] != Integer.MAX_VALUE
                            && dist[edge.first][i] > dist[u][i - 1] + edge.second) {
                        dist[edge.first][i] = dist[u][i - 1] + edge.second;
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
                int k = sc.nextInt();
                int s = sc.nextInt();
                g = new ArrayList[n + 1];
                for (int i = 0; i <= n; i++) {
                    g[i] = new ArrayList<>();
                }
                for (int i = 0; i < m; i++) {
                    int u = sc.nextInt();
                    int v = sc.nextInt();
                    int w = sc.nextInt();
                    g[u].add(new Pair<>(v, w));
                }
                dist = new int[n + 1][k + 1];
                Arrays.stream(dist).forEach(arr -> Arrays.fill(arr, Integer.MAX_VALUE));
                fordBellman(n, k, s);
                IntStream.range(1, n + 1).forEach(i ->
                        System.out.println(dist[i][k] != Integer.MAX_VALUE ? dist[i][k] : -1));
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {
    public T1 first;
    public T2 second;
 
    public Pair(T1 u1, T2 u2) {
        this.first = u1;
        this.second = u2;
    }
 
    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }
 
    @Override
    public int compareTo(Pair<T1, T2> pair) {
        int result = this.first.compareTo(pair.first);
        return result == 0 ? this.second.compareTo(pair.second) : result;
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