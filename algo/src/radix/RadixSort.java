package radix;

public class RadixSort {
    int digit(int num, int exp) {
        return num / exp % 10;
    }

    void countingSortDigit(int[] nums, int exp) {
        int[] counter = new int[10];
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int d = digit(nums[i], exp);
            counter[d]++;
        }

        for (int i = 1; i < 10; i++) {
            counter[i] += counter[i - 1];
        }

        int[] res = new int[n];

        for (int i = n-1; i >=0 ; i--) {
            int d = digit(nums[i], exp);
            int j = counter[d] - 1;
            res[j] = nums[i];
            counter[d]--;
        }

        for (int i = 0; i < n; i++) {
            nums[i] = res[i];
        }
    }

    void radixSort(int[] nums) {
        int m = Integer.MIN_VALUE;
        for (int num : nums) {
            if (num > m)
                m = num;
        }

        for (int exp = 1; exp <= m; exp *= 10) {
            countingSortDigit(nums, exp);
        }
    }
}
