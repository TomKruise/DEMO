package com.tom.sample1;


import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.tom.StdIn;

public class App {

    static class Counter extends AbstractLoggingActor {
        //protocol
        static class Message{}

        private int counter=0;

        {
            receive(ReceiveBuilder.match(Message.class, this::onMessage).build());
        }

        private void onMessage(Message message) {
            counter++;
            log().info("Incresed counter " + counter);
        }

        public static Props props() {
            return Props.create(Counter.class);
        }
    }


    public static void main(String[] args) {
        ActorSystem sample1 = ActorSystem.create("sample1");
        final ActorRef counter = sample1.actorOf(Counter.props(), "counter");
        for (int i = 0; i < 5; i++) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < 5; j++) {
                                counter.tell(new Counter.Message(),ActorRef.noSender());

                            }
                        }
                    }
            ).start();
        }
        System.out.println("ENTER to terminate");
        StdIn.readLine();
        sample1.terminate();
    }
}
