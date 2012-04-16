/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.presence;

import java.util.HashSet;
import java.util.Set;

import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;

/**
 * Class representing a directed presence sender.
 * DirectedPresenceSender tracks who you want to send directed presence to and sends the
 * presence to the directed targets.
 *
 */
public class DirectedPresenceSender implements PresenceSender {

    public enum SendPresence {
        AndSendPresence, 
        DontSendPresence
    };

    private Presence lastSentUndirectedPresence_;
    private PresenceSender sender_;
    private Set<JID> directedPresenceReceivers_ = new HashSet<JID>();

    /**
     * Constructor
     * @param sender PresenceSender, not null
     */
    public DirectedPresenceSender(PresenceSender sender) {
        sender_ = sender;
    }

    /**
     * Send future broadcast presence also to this JID.
     * @param jid Non-roster JID to receive global presence updates.
     * @param sendPresence Also send the current global presence immediately.     
     */

    public void addDirectedPresenceReceiver(JID jid, SendPresence sendPresence) {
        directedPresenceReceivers_.add(jid);
        if (sendPresence == SendPresence.AndSendPresence && sender_.isAvailable()) {
            if (lastSentUndirectedPresence_ != null && 
                    lastSentUndirectedPresence_.getType() == Presence.Type.Available) {
                Presence presenceCopy = new Presence(lastSentUndirectedPresence_);
                presenceCopy.setTo(jid);
                sender_.sendPresence(presenceCopy);
            }
        }
    }

    /**
     * Remove the sender from the list of JIDs to whom broadcast presence will be sent.
     * @param jid  Non-roster JID to stop receiving global presence updates.
     * @param sendPresence Also send presence type=unavailable immediately to jid.
     */
    public void removeDirectedPresenceReceiver(JID jid, SendPresence sendPresence) {
        directedPresenceReceivers_.remove(jid);
        if (sendPresence == SendPresence.AndSendPresence && sender_.isAvailable()) {
            Presence presence = new Presence();
            presence.setType(Presence.Type.Unavailable);
            presence.setTo(jid);
            sender_.sendPresence(presence);
        }
    }

    @Override
    public void sendPresence(Presence presence) {
        if (!sender_.isAvailable()) {
            return;
        }
        sender_.sendPresence(presence);
        if (presence.getTo() == null || !presence.getTo().isValid()) {
            Presence presenceCopy = new Presence(presence);
            for(JID jid : directedPresenceReceivers_) {
                presenceCopy.setTo(jid);
                sender_.sendPresence(presenceCopy);
            }

            lastSentUndirectedPresence_ = presence;
        }
    }

    /**
     * Gets either the last broadcast presence, or an empty stanza if none has been sent.
     * @return presence, not null 
     * 
     */
    public Presence getLastSentUndirectedPresence(){
        if(lastSentUndirectedPresence_ == null) {
            return new Presence();
        }else {
            return new Presence(lastSentUndirectedPresence_);
        }
    }

    @Override
    public boolean isAvailable() {
        return sender_.isAvailable();
    }
}
