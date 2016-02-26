/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.whiteboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.disco.EntityCapsProvider;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.presence.PresenceOracle;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot1;

public class WhiteboardSessionManager {

    private final Map<JID,WhiteboardSession> sessions_ = new HashMap<JID,WhiteboardSession>();
    private final IQRouter router_;
    private final StanzaChannel stanzaChannel_;
    private final PresenceOracle presenceOracle_;
    private final EntityCapsProvider capsProvider_;
    private WhiteboardResponder responder;
    
    public final Signal1<IncomingWhiteboardSession> onSessionRequest =
            new Signal1<IncomingWhiteboardSession>();

    public WhiteboardSessionManager(IQRouter router, StanzaChannel stanzaChannel, 
            PresenceOracle presenceOracle, EntityCapsProvider capsProvider) {
        router_ = router;
        stanzaChannel_ = stanzaChannel;
        presenceOracle_ = presenceOracle;
        capsProvider_ = capsProvider;
        responder = new WhiteboardResponder(this, router_);
        responder.start();
        stanzaChannel_.onPresenceReceived.connect(new Slot1<Presence>() {
            
            @Override
            public void call(Presence presence) {
                handlePresenceReceived(presence);
            }
            
        });
        stanzaChannel_.onAvailableChanged.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean p1) {
                handleAvailableChanged(p1.booleanValue());
            }
            
        });
    }
    
    // Unlike in C++ we can't put this in a destructor to automatically be called when object is
    // destroyed.  Must be called manually.
    public void stop() {
        responder.stop();
    }
    
    public WhiteboardSession getSession(JID to) {
        return sessions_.get(to);
    }
    
    public WhiteboardSession requestSession(JID to) {
        WhiteboardSession session = getSession(to);
        if (session == null) {
            OutgoingWhiteboardSession outgoingSession = createOutgoingSession(to);
            outgoingSession.startSession();
            return outgoingSession;
        } else {
            return session;
        }
    }
    
    private JID getFullJID(JID bareJID) {
        JID fullReceipientJID = null;
        int priority = Integer.MIN_VALUE;
    
        //getAllPresence(bareJID) gives you all presences for the bare JID (i.e. all resources) Remko Tronçon @ 11:11
        List<Presence> presences = 
                new ArrayList<Presence>(presenceOracle_.getAllPresence(bareJID));

        //iterate over them
        for (Presence pres : presences) {
            if (pres.getPriority() > priority) {
              // look up caps from the jid
              DiscoInfo info = capsProvider_.getCaps(pres.getFrom());
              if (info != null && info.hasFeature(DiscoInfo.WhiteboardFeature)) {
                  priority = pres.getPriority();
                  fullReceipientJID = pres.getFrom();
              }
          }
        }
    
        return fullReceipientJID;
    }
    
    private OutgoingWhiteboardSession createOutgoingSession(JID to) {
        JID fullJID = to;
        if (fullJID.isBare()) {
            fullJID = getFullJID(fullJID);
        }
        OutgoingWhiteboardSession session = new OutgoingWhiteboardSession(fullJID, router_);
        sessions_.put(fullJID, session);
        session.onSessionTerminated.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
               deleteSessionEntry(jid);
            }
            
        });
        session.onRequestRejected.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
                deleteSessionEntry(jid);
            }
            
        });
        return session;
    }
    
    public void handleIncomingSession(IncomingWhiteboardSession session) {
        sessions_.put(session.getTo(), session);
        session.onSessionTerminated.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
                deleteSessionEntry(jid);
            }
            
        });
        onSessionRequest.emit(session);
    }
    
    private void handlePresenceReceived(Presence presence) {
        if (!presence.isAvailable()) {
            WhiteboardSession session = getSession(presence.getFrom());
            if (session != null) {
                session.cancel();
            }
        }
    }
    private void handleAvailableChanged(boolean available) {
        if (!available) {
            Map<JID,WhiteboardSession> sessionsCopy = new HashMap<JID,WhiteboardSession>(sessions_);
            for (WhiteboardSession session : sessionsCopy.values()) {
                session.cancel();
            }
        }
    }
    
    private void deleteSessionEntry(JID contact) {
        sessions_.remove(contact);
    }

}
