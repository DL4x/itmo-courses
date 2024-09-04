import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
 
public class B {
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                PrintWriter writer = new PrintWriter(System.out);
                Arrays.stream(optimizedPrefixFunction(sc.next()))
                        .forEach(x -> writer.print(x + " "));
                writer.close();
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
 
    public static int[] optimizedPrefixFunction(String s) {
        int n = s.length();
        int[] prefix = new int[n];
        for (int i = 1; i < n; i++) {
            int j = prefix[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = prefix[j - 1];
            }
            prefix[i] = s.charAt(i) == s.charAt(j) ? ++j : j;
        }
        return prefix;
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