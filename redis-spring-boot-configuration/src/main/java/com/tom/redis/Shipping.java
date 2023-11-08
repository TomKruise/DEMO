package com.tom.redis;

public class Shipping {
    public static int minimalNumberOfPackages(int items, int availableLargePackages, int availableSmallPackages) {
        int largeItems = 5 * availableLargePackages;
        int smallItems = availableSmallPackages;

        if (items > 5 * availableLargePackages + availableLargePackages) {
            return -1;
        }

        return items < (largeItems + smallItems) ? (items - 5*availableLargePackages) + availableLargePackages : -1;
    }
    
    public static void main(String[] args) {
        System.out.println(minimalNumberOfPackages(16, 2, 10));
    }
}