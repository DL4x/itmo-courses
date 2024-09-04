import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
 
public class K {
    private static int[] d;
    private static ArrayList<Pair<Integer, Integer>>[] g;
 
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
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                int s = sc.nextInt();
                int f = sc.nextInt();
                d = new int[n + 1];
                Arrays.fill(d, Integer.MAX_VALUE);
                g = new ArrayList[n + 1];
                for (int i = 0; i <= n; i++) {
                    g[i] = new ArrayList<>();
                }
                int w;
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        w = sc.nextInt();
                        if (i != j && 0 <= w) {
                            g[i].add(new Pair<>(j, w));
                        }
                    }
                }
                dijkstra(s);
                System.out.println(d[f] != Integer.MAX_VALUE ? d[f] : -1);
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
    public int compareTo(Pair<T1, T2> pair) {
        return this.first.compareTo(pair.first);
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