/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.presence;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.Presence;

/**
 * Class representing a PresenceSender  for StanzaChannel.
 * StanzaChannelPresenceSender sends the presence straight through 
 * to the stanza channel.
 *
 */
public class StanzaChannelPresenceSender implements PresenceSender {

    private StanzaChannel channel_;

    /**
     * Constructor
     * @param channel stanza channel, not null
     */
    public StanzaChannelPresenceSender(StanzaChannel channel) {
        this.channel_ = channel;
    }

    @Override
    public boolean isAvailable() {
        return channel_.isAvailable();
    }

    @Override
    public void sendPresence(Presence presence) {
        channel_.sendPresence(presence);
    }
}
