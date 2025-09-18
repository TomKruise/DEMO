package selection;

public class SelectionSort {
    public void selectionSort(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n - 1; i++) {
            int k = i;
            for (int j = i+1; j < n; j++) {
                if (nums[j] < nums[k]) k = j;
            }

            int temp = nums[i];
            nums[i] = nums[k];
            nums[k] = temp;
        }
    }
}
