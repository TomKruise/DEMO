package com.tom.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/websocket/{uid}")
public class MyWebSocket {
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) throws IOException {
        session.getBasicRemote().sendText("Hello " + uid + "!");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        session.close();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println(message);

        session.getBasicRemote().sendText("Copy that: " + message);
    }
}
