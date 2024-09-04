import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class C {
	private static final int UNLIMITED = 100_000;

	private static class SegmentTree {
		private int[] tree;

		public SegmentTree(final int n) {
			tree = new int[4 * n];
			build(0, 0, n);
		}

		private void build(int v, int vl, int vr) {
			if (vl + 1 == vr) {
	            tree[v] = vl + 1;
	            return;
	        }
	        int vm = vl + (vr - vl) / 2;
	        build(2 * v + 1, vl, vm);
	        build(2 * v + 2, vm, vr);
	        tree[v] = Math.min(tree[2 * v + 1], tree[2 * v + 2]);
		}

		private void set(int i, int x, int v, int vl, int vr) {
	        if (vl + 1 == vr) {
	            tree[v] = x;
	            return;
	        }
	        int vm = vl + (vr - vl) / 2;
	        if (i < vm) {
	            set(i, x, 2 * v + 1, vl, vm);
	        } else {
	            set(i, x, 2 * v + 2, vm, vr);
	        }
	        tree[v] = Math.min(tree[2 * v + 1], tree[2 * v + 2]);
	    }

	    public void set(int i, int x) {
	    	set(i, x, 0, 0, tree.length / 4);
	    }

	    private int min(int l, int r, int v, int vl, int vr) {
	        if (l >= vr || vl >= r) {
	            return Integer.MAX_VALUE;
	        }
	        if (vl >= l && r >= vr) {
	            return tree[v];
	        }
	        int vm = vl + (vr - vl) / 2;
	        int min1 = min(l, r, 2 * v + 1, vl, vm);
	        int min2 = min(l, r, 2 * v + 2, vm, vr);
	        return Math.min(min1, min2);
	    }

	    public int min(int l, int r) {
	    	return min(l, r, 0, 0, tree.length / 4);
	    }
	}

	public static void main(String[] args) {
		try (MyScanner sc = new MyScanner(System.in)) {
			final int m = sc.nextInt();
			int[] position = new int[UNLIMITED];
			SegmentTree tree = new SegmentTree(UNLIMITED);
			List<Integer> result = new ArrayList<>();
			for (int i = 0; i < m; i++) {
				String operation = sc.next();
				int car = sc.nextInt() - 1;
				switch (operation) {
					case "+" -> {
						int index = tree.min(0, UNLIMITED);
						tree.set(index - 1, Integer.MAX_VALUE);
						result.add(index);
						position[car] = index;
					}
					case "-" -> {
						tree.set(position[car] - 1, position[car]);
					}
				}
			}

			try (PrintWriter writer = new PrintWriter(System.out)) {
				result.forEach(writer::println);
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
