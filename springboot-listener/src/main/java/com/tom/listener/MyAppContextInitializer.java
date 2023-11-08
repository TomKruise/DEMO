package com.tom.listener;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MyAppContextInitializer implements ApplicationContextInitializer {
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("Hello Listener");
    }
}
