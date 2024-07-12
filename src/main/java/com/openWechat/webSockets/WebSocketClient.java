package com.openWechat.webSockets;

import javax.websocket.*;
import java.net.URI;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
public class WebSocketClient {

    private Session session;
    private URI endpointURI;
    private boolean running = true;

    public WebSocketClient(URI endpointURI) {
        this.endpointURI = endpointURI;
        connect();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Session closed: " + closeReason);
        reconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
        reconnect();
    }

    public void sendMessage(String message) {
        if (this.session != null && this.session.isOpen()) {
            this.session.getAsyncRemote().sendText(message);
        } else {
            System.out.println("Failed to send message, session is not open");
        }
    }

    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            System.out.println("Failed to connect: " + e.getMessage());
            reconnect();
        }
    }

    private void reconnect() {
        if (running) {
            System.out.println("Reconnecting...");
            try {
                TimeUnit.SECONDS.sleep(5); // Wait for 5 seconds before reconnecting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connect();
        }
    }

    public void stop() {
        running = false;
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        URI uri = URI.create("ws://localhost:8080/ws");
        WebSocketClient client = new WebSocketClient(uri);

        // Keep the client running to listen to messages
        Runtime.getRuntime().addShutdownHook(new Thread(client::stop));

        // Example: Keep sending a message every 10 seconds
        new Thread(() -> {
            while (client.running) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println("10秒心跳检测连接正常！");
                    // Send a message every 10 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
