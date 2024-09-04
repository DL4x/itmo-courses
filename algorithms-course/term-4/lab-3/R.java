import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class R {
    private static int[] d;
    private static ArrayList<Pair<Integer, Integer>>[] g;

    private static int weight(char current, char direction) {
        return current == direction ? 0 : 1;
    }

    private static void addEdge(int i, int j, int n, int m, int current, int direction, int w) {
        if (0 <= i && i < n && 0 <= j && j < m) {
            g[current].add(new Pair<>(direction, w));
        }
    }

    private static void dijkstra(int s) {
        d[s] = 0;
        PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>();
        queue.add(new Pair<>(0, s));
        while (!queue.isEmpty()) {
            Pair<Integer, Integer> pair = queue.remove();
            if (pair.first > d[pair.second]) {
                continue;
            }
            for (Pair<Integer, Integer> temp : g[pair.second]) {
                if (d[temp.first] > d[pair.second] + temp.second) {
                    d[temp.first] = d[pair.second] + temp.second;
                    queue.add(new Pair<>(d[temp.first], temp.first));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            d = new int[n * m];
            Arrays.fill(d, Integer.MAX_VALUE);
            g = new ArrayList[n * m];
            Arrays.setAll(g, ignored -> new ArrayList<>());
            final int max = n * m;
            for (int i = 0; i < n; i++) {
                String line = sc.next();
                for (int j = 0; j < m; j++) {
                    final int current = m * i + j;
                    final int north = m * (i - 1) + j;
                    final int west = m * i + (j - 1);
                    final int south = m * (i + 1) + j;
                    final int east = m * i + (j + 1);
                    addEdge(i - 1, j, n, m, current, north, weight(line.charAt(j), 'N'));
                    addEdge(i, j - 1, n, m, current, west, weight(line.charAt(j), 'W'));
                    addEdge(i + 1, j, n, m, current, south, weight(line.charAt(j), 'S'));
                    addEdge(i, j + 1, n, m, current, east, weight(line.charAt(j), 'E'));
                }
            }

            dijkstra(0);

            try (PrintWriter writer = new PrintWriter(System.out)) {
                writer.println(d[n * m - 1]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Pair<T1 extends Comparable<T1>,
        T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {

    public T1 first;
    public T2 second;

    public Pair(T1 u1, T2 u2) {
        this.first = u1;
        this.second = u2;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }

    @Override
    public int compareTo(Pair<T1, T2> pair) {
        int result = this.first.compareTo(pair.first);
        return result == 0 ? this.second.compareTo(pair.second) : result;
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
                return Long.parseLong(strInt.toString());
            }
        }
        return Long.parseLong(strInt.toString());
    }
    // Methods Block (End)
}
