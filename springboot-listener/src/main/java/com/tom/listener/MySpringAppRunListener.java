package com.tom.listener;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

public class MySpringAppRunListener implements SpringApplicationRunListener {
    public MySpringAppRunListener(SpringApplication app, String[] args) {

    }

    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        System.out.println("MyApp starting...");
    }

    public void starting() {

    }

    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        System.out.println("MyApp environmentPrepared...");
    }

    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("MyApp contextPrepared...");
    }

    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("MyApp contextLoaded...");
    }

    public void started(ConfigurableApplicationContext context) {
        System.out.println("MyApp started...");
    }

    public void running(ConfigurableApplicationContext context) {
        System.out.println("MyApp running...");
    }

    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("MyApp failed...");
    }
}
