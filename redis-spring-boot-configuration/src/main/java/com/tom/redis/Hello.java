package com.tom.redis;

import java.util.*;
import java.util.regex.Pattern;

public class Hello {
    public static void main(String[] args) {
        //只实现功能，没有校验
        List<int[]> list = new ArrayList<int[]>();
        List<Integer> top10 = getTop10(list);
    }

    private static List<Integer> getTop10(List<int[]> list) {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>();
        List<Integer> answer = new ArrayList<Integer>();
        for (int[] ints : list) {
            int max = getMax(ints);
            priorityQueue.add(max);
        }
        for (int i = 0; i < priorityQueue.size() - 10; i++) {
            priorityQueue.poll();
        }
        for (Integer integer : priorityQueue) {
            answer.add(integer);
        }

        return answer;
    }

    private static int getMax(int[] ints) {
        int max = 0;
        for (int i : ints) {
            max = Math.max(max, i);
        }

        return max;
    }

}