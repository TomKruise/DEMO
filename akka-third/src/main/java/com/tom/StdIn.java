package com.tom;

import java.io.IOException;

public class StdIn {
    public static void readLine() {
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }
}
