/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.client;

import java.util.Vector;

import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.jid.JID;

/**
 * Dummy Stanza Channel for Unit Testing
 *
 */
public class DummyStanzaChannel extends StanzaChannel {

    public Vector<Stanza> sentStanzas = new Vector<Stanza>();
    public boolean available_;

    public DummyStanzaChannel()  {
        available_ = true;
    }

    public void sendStanza(Stanza stanza) {
        sentStanzas.add(stanza);
    }

    public void setAvailable(boolean available) {
        available_ = available;
        onAvailableChanged.emit(available);
    }

    public void sendIQ(IQ iq) {
        sentStanzas.add(iq);
    }

    public void sendMessage(Message message) {
        sentStanzas.add(message);
    }

    public void sendPresence(Presence presence) {
        sentStanzas.add(presence);
    }

    public String getNewIQID() {
        return "test-id";
    }

    public boolean isAvailable() {
        return available_;
    }

    public boolean getStreamManagementEnabled() {
        return false;
    }

    public <T extends Payload> boolean isRequestAtIndex(int index, JID jid, IQ.Type type, T plType) {
        if (index >= sentStanzas.size()) {
            return false;
        }
        Stanza stanza = (sentStanzas.get(index));
        IQ iqStanza = null;
        if(stanza instanceof IQ) {
            iqStanza = (IQ)(sentStanzas.get(index));
        }
        return iqStanza != null && iqStanza.getType() == type && iqStanza.getTo().equals(jid) 
        && iqStanza.getPayload(plType) != null;
    }
    
    public boolean isResultAtIndex(int index, String id) {
        if (index >= sentStanzas.size()) {
                return false;
        }
        Stanza stanza = (sentStanzas.get(index));
        IQ iqStanza = null;
        if(stanza instanceof IQ) {
            iqStanza = (IQ)(sentStanzas.get(index));
        }
        return iqStanza != null && iqStanza.getType() == IQ.Type.Result && iqStanza.getID().equals(id) ;
}


    public boolean isErrorAtIndex(int index, String id) {
        if (index >= sentStanzas.size()) {
            return false;
        }
        Stanza stanza = (sentStanzas.get(index));
        IQ iqStanza = null;
        if(stanza instanceof IQ) {
            iqStanza = (IQ)(sentStanzas.get(index));
        }
        return iqStanza != null && iqStanza.getType() == IQ.Type.Error && iqStanza.getID().equals(id);
    }

    public <T> T getStanzaAtIndex(T object,int index) {
        if (sentStanzas.size() <= index) {
            return null;
        }
        Stanza stanza = sentStanzas.get(index);
        T obj = null;
        if(object.getClass().isAssignableFrom(stanza.getClass())) {
            return (T)(sentStanzas.get(index));
        }
        return null;
    }   
}
