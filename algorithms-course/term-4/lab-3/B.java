import java.io.*;
import java.nio.charset.StandardCharsets;

public class B {
	private static void code(int i, char[] chars) {
		chars[i] = chars[i + 1] = chars[i + 2] = '*';
	}

	private static boolean isStart(int i, int n, char[] chars) {
		return i < n - 2 && chars[i] == 'o' && chars[i + 1] == 'g' && chars[i + 2] == 'o';
	}

	private static boolean isRepeat(int i, int n, char[] chars) {
		return i < n - 2 && chars[i] == '*' && chars[i + 1] == 'g' && chars[i + 2] == 'o';
	}

	public static void main(String[] args) {
		try (MyScanner sc = new MyScanner(System.in)) {
			final int n = sc.nextInt();
			char[] chars = sc.next().toCharArray();
			StringBuilder builder = new StringBuilder();
			try (PrintWriter writer = new PrintWriter(System.out)) {
				for (int i = 0; i < n; i++) {
					if (isStart(i, n, chars)) {
						code(i, chars);
						writer.print("***");
						continue;
					}
					if (isRepeat(i, n, chars)) {
						code(i, chars);
						continue;
					}
					if (chars[i] != '*') {
						writer.print(chars[i]);
					}
				}
				writer.println();
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