import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.nio.charset.StandardCharsets;

public class Q {
    private static int[] component;
    private static Pair<Integer, Pair<Integer, Integer>>[] edges;

    private static int find(int v) {
        if (component[v] == v) {
            return v;
        }
        component[v] = find(component[v]);
        return component[v];
    }

    private static void union(int u, int v) {
        u = find(u);
        v = find(v);
        component[u] = v;
    }

    private static long kruskal() {
        long result = 0;
        Arrays.sort(edges);
        for (Pair<Integer, Pair<Integer, Integer>> e : edges) {
            int u = e.second.first;
            int v = e.second.second;
            int w = e.first;
            if (find(u) != find(v)) {
                union(u, v);
                result += w;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                int m = sc.nextInt();
                component = IntStream.range(0, n + 1).toArray();
                edges = new Pair[m];
                for (int i = 0; i < m; i++) {
                    int u = sc.nextInt();
                    int v = sc.nextInt();
                    int w = sc.nextInt();
                    edges[i] = new Pair<>(w, new Pair<>(u, v));
                }
                System.out.println(kruskal());
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