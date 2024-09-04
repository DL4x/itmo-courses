import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class P {
    private static double[] minEdge;
    private static boolean[] visited;
    private static Pair<Integer, Integer>[] g;

    private static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2));
    }

    private static double prim() {
        double result = 0;
        int n = g.length;
        for (int i = 0; i < n; i++) {
            int v = -1;
            for (int u = 0; u < n; u++) {
                if (!visited[u] && (v == -1 || minEdge[u] < minEdge[v])) {
                    v = u;
                }
            }
            visited[v] = true;
            result += minEdge[v];
            for (int u = 0; u < n; u++) {
                double w = distance(g[v].first, g[v].second, g[u].first, g[u].second);
                if (w < minEdge[u]) {
                    minEdge[u] = w;
                }
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
                visited = new boolean[n];
                minEdge = new double[n];
                Arrays.fill(minEdge, 1, n, Double.MAX_VALUE);
                g = new Pair[n];
                for (int i = 0; i < n; i++) {
                    g[i] = new Pair<>(sc.nextInt(), sc.nextInt());
                }
                System.out.println(prim());
            } finally {
                sc.close();
            }
        } catch(IOException e) {
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