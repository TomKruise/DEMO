package heap;

public class HeapSort {

    public static void main(String[] args) {
        HeapSort heapSort = new HeapSort();
        int[] nums = new int[] {2,3,1,4,5,6,9,8,7};
        System.out.println(nums);
        heapSort.heapSort(nums);
        System.out.println(nums);
    }

    void heapSort(int[] nums) {
        for (int i = nums.length/2 -1; i >= 0; i--) {
            siftDown(nums, nums.length, i);
        }
        for(int i = nums.length - 1; i > 0; i--) {
            int tmp = nums[0];
            nums[0] = nums[i];
            nums[i] = tmp;
            siftDown(nums, i, 0);
        }
    }

    private void siftDown(int[] nums, int length, int i) {
        while (true) {
            int l = 2 * i + 1;
            int r = l + 1;
            int ma = i;
            if (l < length && nums[l] > nums[ma]) {
                ma = l;
            }
            if (r < length && nums[r] > nums[ma]) {
                ma = r;
            }
            if (ma == i)
                break;

            int temp = nums[i];
            nums[i] = nums[ma];
            nums[ma] = temp;

            i = ma;
        }
    }
}
