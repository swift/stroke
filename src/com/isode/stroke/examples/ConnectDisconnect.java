/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.examples;

import com.isode.stroke.client.ClientError;
import com.isode.stroke.client.ClientOptions;
import com.isode.stroke.client.CoreClient;
import com.isode.stroke.elements.Message;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.JavaNetworkFactories;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.base.SafeByteArray;

/**
 * Simple example.
 * Connects,
 * disconnects,
 * connects,
 * sends a message,
 * receives the message,
 * logs out.
 * @author kismith
 */
public class ConnectDisconnect {

    private boolean running = true;
    private JID jid;
    int connectCount = 0;
    CoreClient client;
    ClientOptions options = new ClientOptions();

    private void handleConnected() {
        connectCount++;
        if (connectCount == 1) {
            System.out.println("First connection");
            client.disconnect();
        } else {
            System.out.println("Second connection");
            Message message = new Message();
            message.setTo(client.getJID());
            message.setBody("Ooh, a message");
            message.setID("BLAH");
            client.sendMessage(message);
        }
    }

    private void handleDisconnected(ClientError error) {
        if (connectCount == 1) {
            System.out.println("First disconnection");
            client.connect(options);
        } else {
            System.out.println("Last connection");
            running = false;
        }
    }

    private void handleMessageReceived(Message message) {
        if (connectCount == 2 && message.getID().equals("BLAH")) {
            System.out.println("Message received");
            client.disconnect();
        }
    }

    public void go(String args[]) {
        jid = new JID(args[0]);
        SafeByteArray password = new SafeByteArray(args[1]);
        DummyEventLoop eventLoop = new DummyEventLoop();
        JavaNetworkFactories factories = new JavaNetworkFactories(eventLoop);

        client = new CoreClient(jid, password, factories);
        client.onConnected.connect(new Slot() {

            public void call() {
                handleConnected();
            }
        });
        client.onDisconnected.connect(new Slot1<ClientError>() {

            public void call(ClientError p1) {
                handleDisconnected(p1);
            }
        });
        client.onMessageReceived.connect(new Slot1<Message>() {

            public void call(Message p1) {
                handleMessageReceived(p1);
            }
        });
        client.connect(options);
        while (running) {
            eventLoop.processEvents();
        }

    }

    public static void main(String args[]) {
        new ConnectDisconnect().go(args);
    }
}
