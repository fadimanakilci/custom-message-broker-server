/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebrokerserver;

import com.sparksign.messagebroker.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private final String                host;
    private final int                   port;
    private final AtomicInteger         retryLimit;
    private final ExecutorService       service;
    private       Socket                socket;
    private       ObjectInputStream     inputStream;
    private       ObjectOutputStream    outputStream;

    public Main(String host, int port) {
        this.host                       = host;
        this.port                       = port;
        this.retryLimit                 = new AtomicInteger(3);
        this.service                    = Executors.newCachedThreadPool();
    }

    public void start() {
        service.execute(this::connect);
    }

    private void handleServer() {
        service.execute(() -> new Thread(() -> {
            while (true) {
                try {
                    Message message = (Message) inputStream.readObject();
                    System.out.println("Received from message broker to Server: " +
                            message.getContent());
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Message broker disconnected.");
                    retryConnection();
                    break;
                }
            }
        }).start());
    }

    public void connect() {
        service.execute(() -> {
            try {
                socket = new Socket(host, port);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("Server connected to message broker");
                clearRetry();
                handleServer();
            } catch (UnknownHostException e) {
                System.err.println("Unknown host");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (IOException e) {
                retryConnection();
            }
        });
    }

    public void disconnect() {
        try {
            retryLimit.set(0);
            socket.close();
            System.out.println("DISCONNECTED");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearRetry() {
        retryLimit.set(3);
    }

    private void retryConnection() {
        try {
            System.err.println("Failed to connect to message broker. Remaining retry " + retryLimit.get() + "...");
            Thread.sleep(2000);
            if(retryLimit.getAndDecrement() != 0) {
                connect();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendMessage(String content) {
        if(socket.isConnected()) {
            try {
                Message message = new Message(content);
                outputStream.writeObject(message);
                outputStream.flush();
                System.out.println("Publish Server to Message Broker: " + message.getId() + " - " + message.getContent() + " - " + message.getTimestamp());
            } catch (Exception e) {
                System.err.println("Failed to send message!");
            }
        } else {
            // Local DB
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Main server = new Main("localhost", 5678);
        server.start();

        TimeUnit.SECONDS.sleep(2);

        server.sendMessage("{\"message_type\":1,\"event_type\":1001,\"id\":1,\"device_id\":3250734," +
                "\"old_value_i32\":\"gnss_status i32\",\"new_value_i32\":\"gnss_status i32\"," +
                "\"device_date_time\":1701301149,\"server_date_time\":1701301149}");

//        TimeUnit.SECONDS.sleep(15);
//        server.disconnect();
    }
}