import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class D {
    @SuppressWarnings("unused")
    private static class SegmentTree {
        private final int size;
        private final int[][] tree;

        public SegmentTree(int[] values) {
            size = values.length;
            tree = new int[4 * size][];
            build(0, 0, size, values);
        }

        private void build(int v, int vl, int vr, int[] values) {
            if (vl + 1 == vr) {
                tree[v] = new int[] {values[vl]};
                return;
            }
            int vm = vl + (vr - vl) / 2;
            build(2 * v + 1, vl, vm, values);
            build(2 * v + 2, vm, vr, values);
            tree[v] = IntStream.concat(
                    Arrays.stream(tree[2 * v + 1]),
                    Arrays.stream(tree[2 * v + 2])
            ).toArray();
        }

        public int find(int l, int r, int value) {
            return find(l, r, value, 0, 0, size);
        }

        private int find(int l, int r, int value, int v, int vl, int vr) {
            if (l >= vr || vl >= r) {
                return Integer.MAX_VALUE;
            }
            if (vl >= l && r >= vr) {
                final int index = Arrays.binarySearch(tree[v], value);
                final int lower =  index < 0 ? Math.abs(index) - 1 : index;
                return lower != tree[v].length ? tree[v][lower] : Integer.MAX_VALUE;
            }
            int vm = vl + (vr - vl) / 2;
            int find1 = find(l, r, value, 2 * v + 1, vl, vm);
            int find2 = find(l, r, value, 2 * v + 2, vm, vr);
            return Math.min(find1, find2);
        }
    }

    private static int lowerBound(int[] keys, int l, int r, int value) {
        final int index = Arrays.binarySearch(keys, l, r, value);
        final int lower = index < 0 ? Math.abs(index) - 1 : index;
        return keys[lower];
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            int[] keys = new int[n];
            long[] keysSum = new long[n + 1];
            for (int i = 0; i < n; i++) {
                keys[i] = sc.nextInt();
                keysSum[i + 1] = keysSum[i] + (long) keys[i];
            }

            try (PrintWriter writer = new PrintWriter(System.out)) {
                for (int i = 0; i < m; i++) {
                    final int l = sc.nextInt() - 1;
                    final int r = sc.nextInt();
                    final int key = (int) (Math.ceil(
                        (keysSum[r] - keysSum[l]) / ((r - l) * 1.0)));
                    writer.println(lowerBound(keys, l, r - 1, key));
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
                return Long.parseLong(strInt.toString());
            }
        }
        return Long.parseLong(strInt.toString());
    }
    // Methods Block (End)
}
