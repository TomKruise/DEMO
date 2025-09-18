package bubble;

public class BubbleSort {
    public void bubbleSort(int[] nums) {
        int n = nums.length;
        for (int i = n-1; i < 0; i--) {
            boolean f = false;
            for (int j = 0; j < i; j++) {
                if (nums[j] > nums[j+1]) {
                    int temp = nums[j];
                    nums[j] = nums[j+1];
                    nums[j+1] = temp;
                    f = true;
                }
            }

            if (!f) break;
        }
    }
}
