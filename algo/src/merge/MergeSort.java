package merge;

public class MergeSort {
    void mergeSort(int[] nums, int left, int right) {
        if (left >= right)
            return;

        int mid = left + (right - left) / 2;
        mergeSort(nums, left, mid);
        mergeSort(nums, mid + 1, right);
        merge(nums, left, mid, right);
    }

    private void merge(int[] nums, int left, int mid, int right) {
        int[] tmp = new int[right-left+1];
        int i = left, j = mid + 1, k =0;
        while (i <= mid && j <= right) {
            if (nums[i] <= nums[j])
                tmp[k++] = nums[i++];
            else
                tmp[k++] = nums[j++];
        }
        while (i <= mid) {
            tmp[k++] = nums[i++];
        }
        while (j <= right) {
            tmp[k++] = nums[j++];
        }

        for (int i1 = 0; i1 < tmp.length; i1++) {
            nums[left+i1] = tmp[i1];
        }
    }
}
