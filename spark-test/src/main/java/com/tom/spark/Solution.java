package com.tom.spark;

import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        if(null == nums || nums.length <=1) return result;
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int a = nums[i];
            int b = target - a;
            if (map.containsKey(b)) {
                result[0] = map.get(a);
                result[1] = map.get(b);
            } else {
                map.put(a,i);
            }
        }
        return result;
    }
}