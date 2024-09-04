import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

public class E {
    private static int[] cost;
    private static int[] count;
    private static int[] recipe;
    private static final int SIZE = 3;
    private static final long MAX = 10_000_000_000_000L;

	private static int ingredient(char ing) {
		return switch (ing) {
            case 'B' -> 0;
            case 'S' -> 1;
            case 'C' -> 2;
            default -> throw new RuntimeException();
        };
	}

    private static long price(long value, int ing) {
        long diff = value * recipe[ing] - count[ing];
        return diff < 0 ? 0 : diff * cost[ing];
    }

	private static boolean isPossible(long value, long r) {
		long result = 0;
        result += price(value, ingredient('B'));
        result += price(value, ingredient('S'));
        result += price(value, ingredient('C'));
		return result <= r;
	}

	public static void main(String[] args) {
		try (MyScanner sc = new MyScanner(System.in)) {
			final String s = sc.next();
			recipe = new int[SIZE];
			for (int i = 0, l = s.length(); i < l; i++) {
				recipe[ingredient(s.charAt(i))]++;
			}
			count = new int[SIZE];
			for (int i = 0; i < SIZE; i++) {
				count[i] = sc.nextInt();
			}
		    cost = new int[SIZE];
			for (int i = 0; i < SIZE; i++) {
				cost[i] = sc.nextInt();
			}
			long r = sc.nextLong();

			long left = 0;
			long right = MAX;
			while (left < right - 1) {
				long mid = left + right >>> 1;
				if (isPossible(mid, r)) {
					left = mid;
				} else {
					right = mid;
				}
			}

			try (PrintWriter writer = new PrintWriter(System.out)) {
				writer.println(left);
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
