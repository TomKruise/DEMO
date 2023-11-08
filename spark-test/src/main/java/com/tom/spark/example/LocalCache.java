package com.tom.spark.example;

import java.util.HashMap;

public class LocalCache {
    private static HashMap<String,Object> cache = new HashMap();

    public void add(String key, Object value) {
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public Object delete(String key) {
        Object answer = get(key);
        cache.remove(key);
        return answer;
    }

    public static void main(String[] args) {
        LocalCache haha = new LocalCache();

        haha.add("key","Ebay");
        System.out.println(haha.get("key"));
        System.out.println(haha.delete("key"));
    }
}
