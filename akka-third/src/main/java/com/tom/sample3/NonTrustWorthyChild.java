package com.tom.sample3;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class NonTrustWorthyChild extends AbstractLoggingActor {
    public static class Command {}

    private long message = 0L;

    {
        receive(ReceiveBuilder.match(Command.class, this::onCommand).build());
    }

    private void onCommand(Command c) {
        message++;
        if (message % 4 == 0) {
            throw new RuntimeException("Oh no, I got four commands, I can't handle any more");
        } else {
            log().info("Got a command " + message);
        }
    }

    public static Props props() {
        return Props.create(NonTrustWorthyChild.class);
    }
}
