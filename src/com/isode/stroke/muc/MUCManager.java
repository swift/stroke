/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.muc;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.jid.JID;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.queries.IQRouter;

/**
 * Class representing a manager for Multi user chat
 *
 */
public class MUCManager {
    private StanzaChannel stanzaChannel_;
    private IQRouter iqRouter_;
    private DirectedPresenceSender presenceSender_;
    private MUCRegistry mucRegistry_;

    /**
     * Create the MUC manager
     * @param stanzaChannel stanza channel, not null
     * @param iqRouter IQ router, not null
     * @param presenceSender Presence sender, not null
     * @param mucRegistry MUC Registry, not null
     */
    public MUCManager(StanzaChannel stanzaChannel, IQRouter iqRouter, 
            DirectedPresenceSender presenceSender, MUCRegistry mucRegistry) { 
        stanzaChannel_ = stanzaChannel;
        iqRouter_ = iqRouter; 
        presenceSender_ = presenceSender; 
        mucRegistry_ = mucRegistry; 
    }

    /**
     * Create a multi user chat room
     * @param jid Room Jabber ID, not null
     * @return MUC room, not null
     */
    public MUC createMUC(JID jid) {
        return new MUC(stanzaChannel_, iqRouter_, presenceSender_, jid, mucRegistry_);
    }
}
