/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.pubsub;
import com.isode.stroke.client.ClientError;
import com.isode.stroke.client.ClientOptions;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.JavaNetworkFactories;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.base.SafeByteArray;

public class Client {
    
    static boolean debugInfo = false;
    static boolean debugInfoXml = false;
    
    public Client(String name, JID jid, SafeByteArray password, JavaNetworkFactories networkFactories, final Slot connectCallback) {
        name_ = name;
        connecting_ = true;
        connected_ = false;
        disconnecting_ = false;
        
        client_ = new com.isode.stroke.client.Client(jid, password, networkFactories);
        
        client_.onConnected.connect(new Slot() {
            public void call() {
                if (debugInfo) {
                    System.out.println("[" + name_ + "] onConnected.");
                }
                connecting_ = false;
                connected_ = true;
                connectCallback.call();
            }
        });
        
        client_.onDisconnected.connect(new Slot1<ClientError>() {
            public void call(ClientError error) {
                if (debugInfo) {
                    System.out.println("[" + name_ + "] onDisconnected.");
                }
                connected_ = false;
            }
        });
        
        client_.onDataRead.connect(new Slot1<SafeByteArray>() {
            public void call(SafeByteArray xml) {
                if (!connecting_ && !disconnecting_) {
                    if (debugInfoXml) {
                        System.out.println("[" + name_ + "] Client.Read:");
                        System.out.println(xml + "\n");
                    }
                }
            }
        });
        
        client_.onDataWritten.connect(new Slot1<SafeByteArray>() {
            public void call(SafeByteArray xml) {
                if (!connecting_ && !disconnecting_) {
                    if (debugInfoXml) {
                        System.out.println("[" + name_ + "] Client.Write:");
                        System.out.println(xml + "\n");
                    }
                }
            }
        });
        
        client_.connect(new ClientOptions());
    }
    
    void disconnect() {
        disconnecting_ = true;
        client_.disconnect();
    }
    
    boolean isConnected() {
        return connected_;
    }
    
    boolean isConnecting() {
        return connecting_;
    }
    
    JID getJID() {
        return client_.getJID();
    }
    
    IQRouter getIQRouter() {
        return client_.getIQRouter();
    }
    
    PubSubManager getPubSubManager() {
        return client_.getPubSubManager();
    }
    
    com.isode.stroke.client.Client client_;
    String name_;
    boolean connected_;
    boolean connecting_;
    boolean disconnecting_;
}
