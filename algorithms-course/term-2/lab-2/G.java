import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

public class MoveToBegin {
    public static void main(String[] args) {
        try (TokenScanner sc = new TokenScanner(System.in)) {
            final int n = sc.nextInt();
            Treap treap = new Treap();
            IntStream.range(1, n + 1)
                    .forEach(treap::add);
            final int m = sc.nextInt();
            for (int i = 0; i < m; i++) {
                treap.moveToBegin(sc.nextInt(), sc.nextInt());
            }
            try (PrintWriter writer = new PrintWriter(System.out)) {
                treap.inorderTraversal().forEach(x -> writer.print(x + " "));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Treap {
    private static class Node {
        private final int x;
        private final int y;
        private int size = 1;
        private Node left;
        private Node right;

        public Node(final int x) {
            this.x = x;
            this.y = (int) (Math.random() * (1 << 20) + 1);
        }

        private static int size(Node current) {
            return current != null ? current.size : 0;
        }

        public void resize() {
            size = size(left) + size(right) + 1;
        }
    }

    private Node root;

    private Node merge(Node left, Node right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        if (left.y > right.y) {
            left.right = merge(left.right, right);
            left.resize();
            return left;
        } else {
            right.left = merge(left, right.left);
            right.resize();
            return right;
        }
    }

    private Node[] split(Node current, final int k) {
        if (current == null) {
            return new Node[] {null, null};
        }
        if (Node.size(current.left) < k) {
            Node[] tuple = split(current.right,
                    k - Node.size(current.left) - 1);
            current.right = tuple[0];
            current.resize();
            return new Node[] {current, tuple[1]};
        } else {
            Node[] tuple = split(current.left, k);
            current.left = tuple[1];
            current.resize();
            return new Node[] {tuple[0], current};
        }
    }


    public void add(final int v) {
        root = merge(root, new Node(v));
    }

    public void moveToBegin(final int left, final int right) {
        Node[] split1 = split(root, left - 1);
        Node[] split2 = split(split1[1], right - left + 1);
        root = merge(merge(split2[0], split1[0]), split2[1]);
    }

    private void inorderTraversal(Node current, List<Integer> result) {
        if (current == null) {
            return;
        }
        inorderTraversal(current.left, result);
        result.add(current.x);
        inorderTraversal(current.right, result);
    }

    public List<Integer> inorderTraversal() {
        List<Integer> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }
}

class TokenScanner implements AutoCloseable {
    private StringTokenizer stringTokenizer;
    private final BufferedReader bufferedReader;

    public TokenScanner(final InputStream inputStream) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public TokenScanner(final String path) throws IOException {
        bufferedReader = Files.newBufferedReader(Path.of(path));
    }

    public String next() throws IOException {
        while (stringTokenizer == null
                || !stringTokenizer.hasMoreElements()) {
            stringTokenizer =
                    new StringTokenizer(bufferedReader.readLine());
        }
        return stringTokenizer.nextToken();
    }

    public int nextInt() throws IOException {
        return Integer.parseInt(next());
    }

    public long nextLong() throws IOException {
        return Long.parseLong(next());
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }
}
