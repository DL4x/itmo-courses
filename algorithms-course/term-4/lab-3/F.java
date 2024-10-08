import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

public class F {
	public static void main(String[] args) {
		try (MyScanner sc = new MyScanner(System.in)) {
			final int n = sc.nextInt();
			int[] tables = new int[n];
			for (int i = 0; i < n; i++) {
				tables[i] = sc.nextInt();
			}
			tables = IntStream.of(tables)
					.boxed()
					.sorted(Comparator.reverseOrder())
					.mapToInt(i -> i)
					.toArray();
			
			final int k = sc.nextInt();
			int[] companies = new int[k];
			for (int i = 0; i < k; i++) {
				companies[i] = sc.nextInt();
			}
			companies = IntStream.of(companies)
					.boxed()
					.sorted(Comparator.reverseOrder())
					.mapToInt(i -> i)
					.toArray();

			int i = 0;
			int j = 0;
			long result = 0;
			while (i < n && j < k) {
				if (companies[j] <= tables[i]) {
					result += companies[j];
					i++;
				}
				j++;
			}

			try (PrintWriter writer = new PrintWriter(System.out)) {
				writer.println(result);
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
