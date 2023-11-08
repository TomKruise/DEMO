package com.tom.spark.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CountChars {
    /**
     * 封装一个方法实现以下下功能
     * 求出每个字母出现的次数
     * 入参：
     * accc33#
     * 返回结果：
     * a：1
     * c：3
     * 3：2
     * #：1
     */
    public Map<String, Integer> countChars(String str) {
        Map<String, Integer> answer = new HashMap<>();

        if (null != str && str.length() > 0) {
            char[] chars = str.toCharArray();
            for (char c : chars) {
                putCharAndCount(c, answer);
            }
        }

        return answer;
    }

    private void putCharAndCount(char c, Map<String, Integer> answer) {
        String key = String.valueOf(c);
        if (answer.containsKey(key)) {
            int num = answer.get(key);
            answer.put(key,num+1);
        } else {
            answer.put(key,1);
        }
    }

    public static void main(String[] args) {
        String s = "accc33#";
        CountChars obj = new CountChars();
        Map<String, Integer> map = obj.countChars(s);
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        entries.forEach(entry -> {
            System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
        });
    }
}
