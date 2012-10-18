/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.base.Error;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import java.util.logging.Logger;

/**
 * StanzaChannel implementation around a ClientSession.
 */
public class ClientSessionStanzaChannel extends StanzaChannel {
    private final IDGenerator idGenerator = new IDGenerator();
    private ClientSession session;
    private static final Logger logger_ = Logger.getLogger(ClientSessionStanzaChannel.class.getName());
    private SignalConnection sessionInitializedConnection;
    private SignalConnection sessionFinishedConnection;
    private SignalConnection sessionStanzaReceivedConnection;
    private SignalConnection sessionStanzaAckedConnection;

    public void setSession(final ClientSession session) {
        assert this.session == null;
        this.session = session;
        sessionInitializedConnection = session.onInitialized.connect(new Slot() {

            public void call() {
                handleSessionInitialized();
            }
        });
        sessionFinishedConnection = session.onFinished.connect(new Slot1<Error>() {

            public void call(final Error p1) {
                handleSessionFinished(p1);
            }
        });
        sessionStanzaReceivedConnection = session.onStanzaReceived.connect(new Slot1<Stanza>() {

            public void call(final Stanza p1) {
                handleStanza(p1);
            }
        });
        sessionStanzaAckedConnection = session.onStanzaAcked.connect(new Slot1<Stanza>() {

            public void call(final Stanza p1) {
                handleStanzaAcked(p1);
            }
        });
    }

    public void sendIQ(final IQ iq) {
        send(iq);
    }

    public void sendMessage(final Message message) {
        send(message);
    }

    public void sendPresence(final Presence presence) {
        send(presence);
    }

    public boolean getStreamManagementEnabled() {
        if (session != null) {
            return session.getStreamManagementEnabled();
        }
        return false;
    }

    public boolean isAvailable() {
        return session != null && ClientSession.State.Initialized.equals(session.getState());
    }

    public String getNewIQID() {
        return idGenerator.generateID();
    }

    private void send(final Stanza stanza) {
        if (!isAvailable()) {
            logger_.warning("Warning: Client: Trying to send a stanza while disconnected.");
            return;
        }
        session.sendStanza(stanza);
    }

    private void handleSessionFinished(final Error error) { // NOPMD, ignore that Error isn't used.
        sessionFinishedConnection.disconnect();
        sessionStanzaReceivedConnection.disconnect();
        sessionStanzaAckedConnection.disconnect();
        sessionInitializedConnection.disconnect();
        session = null;
        onAvailableChanged.emit(false);
    }

    private void handleStanza(final Stanza stanza) {
        if (stanza instanceof Message) {
            onMessageReceived.emit((Message)stanza);
        }
        if (stanza instanceof Presence) {
            onPresenceReceived.emit((Presence)stanza);
        }
        if (stanza instanceof IQ) {
            onIQReceived.emit((IQ)stanza);
        }
    }

    private void handleStanzaAcked(final Stanza stanza) {
        onStanzaAcked.emit(stanza);
    }

    private void handleSessionInitialized() {
        onAvailableChanged.emit(true);
    }

}
