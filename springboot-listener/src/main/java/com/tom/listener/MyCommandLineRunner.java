package com.tom.listener;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyCommandLineRunner implements CommandLineRunner {
    public void run(String... args) throws Exception {
        System.out.println("MyCommandLineRunner run ...");
        System.out.println(Arrays.asList(args));
    }
}
