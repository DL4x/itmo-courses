package search;

public class BinarySearchShift {
	/*
	 * Pred:
	 * args.length ≥ 0
	 * All arguments - array elements
	 * Post:
	 * outputs the result of the binary search (shift) function to the console 
	 */
	public static void main(String[] args) {
		// All arguments: array elements
		int[] array = new int[args.length];
		int sum = 0;
		for (int i = 0; i < args.length; i++) {
			int num = Integer.parseInt(args[i]);
			sum += num;
			array[i] = num;
		}
		if (sum % 2 == 0) {
			/*
			 * The sum of all numbers is even
			 * Therefore:
			 * Output of the iterative binary search (shift) result
			 */ 
			System.out.println(recursiveBinarySearchShift(array, -1, array.length));
		} else {
			/*
			 * The sum of all numbers isn't even
			 * Therefore:
			 * Output of the recursive binary search (shift) result
			 */
			System.out.println(iterativeBinarySearchShift(array));
		}
	}

	/*
	 * Pred:
	 * forall i in [0, k - 1): array[i] > array[i + 1]
	 * &&
	 * forall j in [k, array.length - 1): array[j] > array[j + 1]
	 * &&
	 * when i = k - 1, j = k: array[i] > array[j]
	 * Post:
	 * k - the value of the cyclic shift
	 */
	static int iterativeBinarySearchShift(int[] array) {
		int left = -1;
		int right = array.length;
		// Invariant: left < k ≤ right
		while (right - left > 1) {
			/*
			 * left' < k ≤ right'
			 * &&
			 * right' - left' > 1
			 */
			int mid = left + (right - left) / 2;
			// left' < mid' < right'
			if (array[mid] < array[array.length - 1]) {
				/*
				 * left' < k ≤ right'
				 * &&
				 * right' - left' > 1
				 * &&
				 * array[mid'] < array[array.length - 1]
				 * Therefore:
				 * array[mid'] in [k, array.length)
				 */
				left = mid;
				/*
				 * array[mid'] < array[array.length - 1]
				 * &&
				 * right' - left' > 1
				 * Therefore:
				 * left' = mid'
				 * &&
				 * mid' < k ≤ right'
				 * &&
				 * right'' - left'' < right' - left'
				 */
			} else {
				/*
				 * left' < k ≤ right'
				 * &&
				 * right' - left' > 1
				 * &&
				 * array[mid'] > array[array.length - 1]
				 * Therefore:
				 * array[mid'] in [0, k)
				 */
				right = mid;
				/*
				 * array[mid'] > array[array.length - 1]
				 * &&
				 * right' - left' > 1
				 * Therefore:
				 * right' = mid
				 * &&
				 * left' < k ≤ mid'
				 * &&
				 * right'' - left'' < right' - left'
				 */
			}
			/*
			 * left' < k ≤ right'
			 * right - left > 1
			 */
		}
		/*
		 * forall i in [0, k - 1): array[i] > array[i + 1]
		 * &&
		 * forall j in [k, array.length - 1): array[j] > array[j + 1]
		 * &&
		 * when i = k - 1, j = k: array[i] > array[j]
		 * &&
		 * left' < k ≤ right' 
		 * &&
		 * right' - left' ≤ 1
		 * Therefore:
		 * k - the value of the cyclic shift
		 */
		return right;
	}

	/*
	 * Pred:
	 * forall i in [0, k - 1): array[i] > array[i + 1]
	 * &&
	 * forall j in [k, array.length - 1): array[j] > array[j + 1]
	 * &&
	 * when i = k - 1, j = k: array[i] > array[j]
	 * &&
	 * left ≥ -1
	 * && 
	 * right ≤ array.length
	 * &&
	 * left < k ≤ right
	 * Post:
	 * k - the value of the cyclic shift
	 */
	static int recursiveBinarySearchShift(int[] array, int left, int right) {
		if (!(right - left > 1)) {
			/*
			 * Pred:
			 * forall i in [0, k - 1): array[i] > array[i + 1]
			 * &&
			 * forall j in [k, array.length - 1): array[j] > array[j + 1]
			 * &&
			 * when i = k - 1, j = k: array[i] > array[j]
			 * &&
			 * left' ≥ -1
			 * && 
			 * right' ≤ array.length
			 * &&
			 * left' < k ≤ right'
			 * && 
			 * left' < k ≤ right'
			 * &&
			 * right' - left' ≤ 1
			 */
			return right;
		}
		/*
		 * left' < k ≤ right'
		 * &&
		 * right' - left' > 1
		 */
		int mid = left + (right - left) / 2;
		// left' < mid' < right'
		if (array[mid] < array[array.length - 1]) {
			/*
			 * left' < k ≤ right'
			 * &&
			 * right' - left' > 1
			 * && 
			 * array[mid'] < array[array.length - 1]
			 * Therefore:
			 * use left' = mid, because mid' < k ≤ right'
			 * &&
			 * right'' - left'' < right' - left'
			 */
			return recursiveBinarySearchShift(array, mid, right);
		} else {
			/*
			 * left' < k ≤ right'
			 * &&
			 * right' - left' > 1
			 * && 
			 * array[mid'] > array[array.length - 1]
			 * Therefore:
			 * use right' = mid, because left' < k ≤ mid'
			 * && 
			 * right'' - left'' < right' - left'
			 */
			return recursiveBinarySearchShift(array, left, mid);
		}
	}
}