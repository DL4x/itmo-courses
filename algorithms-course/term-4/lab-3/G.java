import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class G {
	private static void init(long[] dp, int[] cost) {
		Arrays.fill(dp, Long.MIN_VALUE);
		dp[0] = cost[0];
	}

	private static void set(int i, int j, long[] dp, int[] cost) {
		if (cost.length <= j) {
			return;
		}
		if (dp[i] == Long.MIN_VALUE) {
			return;
		}
		dp[j] = cost[i] != cost[j] ? Math.max(dp[i] + cost[j], dp[j]) : dp[j];
	}

	public static void main(String[] args) {
		try (MyScanner sc = new MyScanner(System.in)) {
			final int n = sc.nextInt();
			int[] cost = new int[n];
			for (int i = 0; i < n; i++) {
				cost[i] = sc.nextInt();
			}
			long[] dp = new long[n];
			init(dp, cost);
			for (int i = 0; i < n; i++) {
				set(i, i + 1, dp, cost);
				set(i, i + 2, dp, cost);
				set(i, i + 3, dp, cost);
			}

			try (PrintWriter writer = new PrintWriter(System.out)) {
				writer.println(dp[n - 1] != Long.MIN_VALUE 
					? dp[n - 1]
					: "Impossible");
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