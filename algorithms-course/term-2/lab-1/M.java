import java.io.*;
import java.util.StringTokenizer;

public class GreatChineseWall {
    public static void main(String[] args) {
        try (TokenScanner sc = new TokenScanner(System.in)) {
            final int n = sc.nextInt();
            final int m = sc.nextInt();
            SegmentTree tree = new SegmentTree(n);
            try (PrintWriter writer = new PrintWriter(System.out)) {
                for (int i = 0; i < m; i++) {
                    String type = sc.next();
                    int left = sc.nextInt() - 1;
                    int right = sc.nextInt();
                    switch (type) {
                        case "defend" -> tree.rangeSet(left, right, sc.nextInt());
                        case "attack" -> {
                            final int[] result = tree.rangeMin(left, right);
                            writer.println(result[0] + " " + result[1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class SegmentTree {
    private final int size;
    private final int[][] tree;
    private final Integer[] flag;

    public SegmentTree(final int n) {
        this.size = n;
        this.tree = new int[4 * n][2];
        this.flag = new Integer[4 * n];
        build(0, 0, n);
    }

    private void build(int v, int vl, int vr) {
        if (vl + 1 == vr) {
            tree[v][1] = vl + 1;
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(2 * v + 1, vl, vm);
        build(2 * v + 2, vm, vr);
        tree[v] = tree[2 * v + 1][0] <= tree[2 * v + 2][0]
                ? tree[2 * v + 1]
                : tree[2 * v + 2];
    }

    private void push(int v, int vl, int vr) {
        if (flag[v] != null) {
            if (vl + 1 == vr) {
                tree[v][0] = Math.max(flag[v], tree[v][0]);
                flag[v] = null;
            } else {
                tree[v][0] = Math.max(flag[v], tree[v][0]);
                flag[2 * v + 1] = flag[2 * v + 1] == null
                        ? flag[v]
                        : Math.max(flag[v], flag[2 * v + 1]);
                flag[2 * v + 2] = flag[2 * v + 2] == null
                        ? flag[v]
                        : Math.max(flag[v], flag[2 * v + 2]);
                flag[v] = null;
            }
        }
    }

    public void rangeSet(int left, int right, int value) {
        rangeSet(left, right, value, 0, 0, size);
    }

    private void rangeSet(int left, int right, int value, int v, int vl, int vr) {
        push(v, vl, vr);
        if (left >= vr || vl >= right) {
            return;
        }
        if (vl >= left && right >= vr) {
            flag[v] = value;
            push(v, vl, vr);
            return;
        }
        int vm = vl + (vr - vl) / 2;
        rangeSet(left, right, value, 2 * v + 1, vl, vm);
        rangeSet(left, right, value, 2 * v + 2, vm, vr);
        tree[v] = tree[2 * v + 1][0] <= tree[2 * v + 2][0]
                ? tree[2 * v + 1]
                : tree[2 * v + 2];
    }

    public int[] rangeMin(int left, int right) {
        return rangeMin(left, right, 0, 0, size);
    }

    private int[] rangeMin(int left, int right, int v, int vl, int vr) {
        push(v, vl, vr);
        if (left >= vr || vl >= right) {
            return new int[] { Integer.MAX_VALUE, 0 };
        }
        if (vl >= left && right >= vr) {
            return tree[v];
        }
        int vm = vl + (vr - vl) / 2;
        int[] min1 = rangeMin(left, right, 2 * v + 1, vl, vm);
        int[] min2 = rangeMin(left, right, 2 * v + 2, vm, vr);
        return min1[0] <= min2[0] ? min1 : min2;
    }
}

class TokenScanner implements AutoCloseable {
    BufferedReader br;
    StringTokenizer st;

    public TokenScanner(InputStream in) {
        br = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public void close() throws IOException {
        br.close();
    }

    String next() throws IOException {
        while (st == null || !st.hasMoreElements()) {
            st = new StringTokenizer(br.readLine());
        }
        return st.nextToken();
    }

    int nextInt() throws IOException {
        return Integer.parseInt(next());
    }
}
