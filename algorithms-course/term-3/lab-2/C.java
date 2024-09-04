import java.io.*;
import java.util.stream.IntStream;
 
public class C {
	public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            optimizedZFunction(reader.readLine());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
 
    public static void optimizedZFunction(String s) {
        int l = 0;
        int r = 0;
        int n = s.length();
        int[] z = new int[n];
        z[0] = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(z[i - l], r - i + 1);
            }
            while (z[i] + i < n && s.charAt(z[i]) == s.charAt(z[i] + i)) {
                z[i]++;
            }
            if (r < z[i] + i - 1) {
                l = i;
                r = z[i] + i - 1;
            }
        }
        try (PrintWriter writer = new PrintWriter(System.out);) {
            IntStream.range(1, n).forEach(i -> writer.print(z[i] + " "));
        }
    }
}