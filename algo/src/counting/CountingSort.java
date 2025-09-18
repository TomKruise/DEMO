package counting;

public class CountingSort {
    void countingSort(int[] nums) {
        int m = 0;
        for (int num : nums) {
            m = Math.max(m, num);
        }
        int[] counter = new int[m+1];
        for (int num : nums) {
            counter[num]++;
        }

        for (int i = 0; i < m; i++) {
            counter[i+1] += counter[i];
        }

        int n = nums.length;
        int[] res = new int[n];
        for (int i = n-1; i >= 0; i--) {
            int num = nums[i];
            res[counter[num]-1] = num;
            counter[num]--;
        }

        for (int i = 0; i < n; i++) {
            nums[i] = res[i];
        }
    }
}
