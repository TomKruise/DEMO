package bucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BucketSort {
    void bucketSort(float[] nums) {
        int k = nums.length / 2;
        List<List<Float>> buckets = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            buckets.add(new ArrayList<>());
        }

        for (float num : nums) {
            int i = (int)(num * k);
            buckets.get(i).add(num);
        }

        buckets.forEach(b -> Collections.sort(b));

        int i = 0;
        for (List<Float> bucket : buckets) {
            for (Float f : bucket) {
                nums[i++]=f;
            }
        }
    }
}
