package com.tom.sample3;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;
import static akka.actor.SupervisorStrategy.*;

public class Supervisor extends AbstractLoggingActor {

//    public static final OneForOneStrategy STRATEGY = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> restart()).build());
//    public static final OneForOneStrategy STRATEGY = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> resume()).build());
//    public static final OneForOneStrategy STRATEGY = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> stop()).build());
    public static final OneForOneStrategy STRATEGY = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> escalate()).build());


    {
        //    /user/supervisor/child
        final ActorRef child = getContext().actorOf(NonTrustWorthyChild.props(), "child");

        receive(ReceiveBuilder.matchAny(any -> child.forward(any, getContext())).build());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return STRATEGY;
    }

    public static Props props() {
        return Props.create(Supervisor.class);
    }
}
