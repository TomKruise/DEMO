package com.tom.redis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

class Main01 {
    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        BufferedWriter writer = new BufferedWriter(new FileWriter("newfile.txt"));
        writer.write("Hello SnapPay!");
        writer.close();

        String path = "./";
        File file = new File(path);
        File[] fs = file.listFiles();
        StringBuilder builder = new StringBuilder();
        for (File f : fs) {
            if (!f.isDirectory()) {
                String name = f.getName() + ", ";
                System.out.print(name);
                builder.append(name);
            }
        }

        builder.append("geqmd3o4y82a");
        for (int i = 1; i <= builder.length(); i++) {
            if (i % 3 == 0) {
                builder.setCharAt(i, 'X');
            }
        }
        System.out.println(builder);
    }
}