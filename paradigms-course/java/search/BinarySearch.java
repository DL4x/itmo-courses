package search;

public class BinarySearch {
    /*
     * Pred: 
     * args.length ≥ 1 
     * First argument - search value
     * Other arguments - array elements
     * Post:
     * outputs the result of the binary search function to the console
     */
    public static void main(String[] args) {
        // First argument: x - search value
        int x = Integer.parseInt(args[0]);
        // Other arguments: array elements
        int[] array = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            array[i] = Integer.parseInt(args[i + 1]);
        }
        // Output of the iterative binary search result
        System.out.println(iterativeBinarySearch(x, array));
        // Output of the recursive binary search result
        // System.out.println(recursiveBinarySearch(x, array, 0, array.length));
    }

    /*
     * Pred: 
     * x - search value
     * &&
     * forall i in [0, array.length - 1): array[i] ≥ array[i + 1]
     * Post: 
     * right' has a minimum value at which array[right'] ≤ x
     */
    static int iterativeBinarySearch(int x, int[] array) {
        int left = 0;
        int right = array.length;
        // Invariant: array[left] ≥ x ≥ array[right]
        while (left < right) {
            /*
             * array[left'] ≥ x ≥ array[right']
             * &&
             * left' < right'
             */
            int mid =  left + (right - left) / 2;
            /*
             * left' < mid' < right'
             * Therefore:
             * array[left'] ≥ array[mid'] ≥ array[right']
             */
            if (array[mid] <= x) {
                /*
                 * array[left'] ≥ x ≥ array[right']
                 * &&
                 * left' < right'
                 * &&
                 * array[mid'] ≤ x
                 */
                right = mid;
                /*
                 * array[mid'] ≤ x
                 * && 
                 * right' = mid'
                 * Therefore:
                 * right' = mid
                 * &&
                 * array[left'] ≥ x ≥ array[mid']
                 * &&
                 * right'' - left'' < right' - left'
                 */
            } else {
                /*
                 * array[left'] ≥ x ≥ array[right']
                 * && 
                 * left' < right' 
                 * &&
                 * array[mid'] > x
                 */
                left = mid + 1;
                /*
                 * array[mid'] > x
                 * && 
                 * left' = mid' + 1
                 * Therefore:
                 * left' = mid' + 1
                 * &&
                 * array[mid' + 1] ≥ x ≥ array[right']
                 * &&
                 * right'' - left'' < right' - left'
                 */
            }
            /* 
             * array[left'] ≥ x ≥ array[right']
             * left' < right'
             */
        }
        /*
         * x - search value
         * &&
         * forall i in [0, array.length - 1): array[i] ≥ array[i + 1]
         * &&
         * array[left'] ≥ x ≥ array[right']
         * &&
         * left' ≥ right'
         * Therefore:
         * right' has a minimum value at which array[right'] ≤ x
         */
        return right;
    }

    /*
     * Pred: 
     * x - search value
     * &&
     * forall i in [0, array.length - 1): array[i] ≥ array[i + 1]
     * &&
     * left ≥ 0
     * && 
     * right ≤ array.length
     * &&  
     * array[left] ≥ x ≥ array[right]
     * Post: 
     * right' has a minimum value at which array[right'] ≤ x
     */
    static int recursiveBinarySearch(int x, int[] array, int left, int right) {
        if (!(left < right)) {
            /*
             * x - search value
             * &&
             * forall i in [0, array.length - 1): array[i] ≥ array[i + 1]
             * &&
             * left' ≥ 0
             * &&
             * right' ≤ array.length
             * &&
             * array[left'] ≥ x ≥ array[right']
             * && 
             * left' ≥ right'
             * Therefore:
             * right' has a minimum value at which array[right'] ≤ x
             */ 
            return left;
        }
        /*
         * array[left'] ≥ x ≥ array[right']
         * &&
         * left' < right'
         */
        int mid = left + (right - left) / 2;
        /*
         * left' < mid' < right'
         * Therefore:
         * array[left'] ≥ array[mid'] ≥ array[right']
         */
        if (array[mid] <= x) {
            /*
             * array[left'] ≥ x ≥ array[right']
             * &&
             * left' < right'
             * &&
             * array[mid'] ≤ x
             * Therefore:
             * use right' = mid', because array[left'] ≥ x ≥ array[mid']
             * && 
             * right'' - left'' < right' - left'
             */
            return recursiveBinarySearch(x, array, left, mid);
        } else {
            /*
             * array[left'] ≥ x ≥ array[right']
             * &&
             * left' < right'
             * &&
             * array[mid'] > x
             * Therefore:
             * use left' = mid' + 1, because array[mid' + 1] ≥ x ≥ array[right']
             * &&
             * right'' - left'' < right' - left'
             */
            return recursiveBinarySearch(x, array, mid + 1, right);
        }
    }
}