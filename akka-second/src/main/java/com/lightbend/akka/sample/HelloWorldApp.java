package com.lightbend.akka.sample;


import akka.actor.typed.ActorSystem;

public class HelloWorldApp {
    public static void main(String[] args) {
        ActorSystem<HelloWorld.Command> system = ActorSystem.create(HelloWorld.create(), "MySystem");

        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(new HelloWorld.ChangeMessage("Hi Tom, this is actor world!!!"));
        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(HelloWorld.SayHello.INSTANCE);
        system.tell(HelloWorld.SayHello.INSTANCE);
    }
}
