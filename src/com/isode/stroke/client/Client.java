/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.jid.JID;
import com.isode.stroke.muc.MUCManager;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.network.NetworkFactories;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.presence.StanzaChannelPresenceSender;
import com.isode.stroke.queries.responders.SoftwareVersionResponder;

/**
 * Provides the core functionality for writing XMPP client software.
 *
 * Besides connecting to an XMPP server, this class also provides interfaces for
 * performing most tasks on the XMPP network.
 */

public class Client extends CoreClient {

    private final MUCManager mucManager;
    private final MUCRegistry mucRegistry;
    //NOPMD, this is not better as a local variable
    private final DirectedPresenceSender directedPresenceSender;
    //NOPMD, this is not better as a local variable
    private final StanzaChannelPresenceSender stanzaChannelPresenceSender;
    private final SoftwareVersionResponder softwareVersionResponder;

    /**
     * Constructor.
     * 
     * @param eventLoop Event loop used by the class, must not be null. The
     *            Client creates threads to do certain tasks. However, it
     *            posts events that it expects to be done in the application's
     *            main thread to this eventLoop. The application should
     *            use an appropriate EventLoop implementation for the application type. This
     *            EventLoop is just a way for the Client to pass these
     *            events back to the main thread, and should not be used by the
     *            application for its own purposes.
     * @param jid User JID used to connect to the server, must not be null
     * @param password User password to use, must not be null
     * @param networkFactories An implementation of network interaction, must
     *            not be null.
     */
    public  Client(final JID jid, final String password, final NetworkFactories networkFactories) {
        super(jid, password, networkFactories);
        stanzaChannelPresenceSender = new StanzaChannelPresenceSender(getStanzaChannel());
        directedPresenceSender = new DirectedPresenceSender(stanzaChannelPresenceSender);

        mucRegistry = new MUCRegistry();
        mucManager = new MUCManager(getStanzaChannel(), getIQRouter(), directedPresenceSender, mucRegistry);

        softwareVersionResponder = new SoftwareVersionResponder(getIQRouter());
        softwareVersionResponder.start();
    }

    /**
     * Get the manager for multi user chat rooms
     * @return MUC manager, not null
     */
    public MUCManager getMUCManager() {
        return mucManager;
    }

    /**
     * Get the registry for multi user chat rooms
     * @return MUC registry, not null
     */
    public MUCRegistry getMUCRegistry() {
        return mucRegistry;
    }
    
    /**
     * Sets the software version of the client.                  
     *
     * This will be used to respond to version queries from other entities.
     */
    public void setSoftwareVersion(final String name, final String version, final String os) {
        softwareVersionResponder.setVersion(name, version, os);
    }
}
