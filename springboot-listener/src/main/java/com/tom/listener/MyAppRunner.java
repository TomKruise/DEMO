package com.tom.listener;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyAppRunner implements ApplicationRunner {
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("MyAppRunner run ...");
        System.out.println(Arrays.asList(args.getSourceArgs()));
    }
}
