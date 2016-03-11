/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.avatars.AvatarManager;
import com.isode.stroke.avatars.AvatarManagerImpl;
import com.isode.stroke.disco.CapsManager;
import com.isode.stroke.disco.ClientDiscoManager;
import com.isode.stroke.disco.EntityCapsManager;
import com.isode.stroke.disco.EntityCapsProvider;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.muc.MUCManager;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.network.NetworkFactories;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.presence.PresenceOracle;
import com.isode.stroke.presence.PresenceSender;
import com.isode.stroke.presence.StanzaChannelPresenceSender;
import com.isode.stroke.presence.SubscriptionManager;
import com.isode.stroke.pubsub.PubSubManager;
import com.isode.stroke.pubsub.PubSubManagerImpl;
import com.isode.stroke.queries.responders.SoftwareVersionResponder;
import com.isode.stroke.roster.XMPPRoster;
import com.isode.stroke.roster.XMPPRosterController;
import com.isode.stroke.roster.XMPPRosterImpl;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.tls.BlindCertificateTrustChecker;
import com.isode.stroke.filetransfer.FileTransferManager;
import com.isode.stroke.filetransfer.FileTransferManagerImpl;
import com.isode.stroke.jingle.JingleSessionManager;
import com.isode.stroke.filetransfer.DummyFileTransferManager;

/**
 * Provides the core functionality for writing XMPP client software.
 *
 * Besides connecting to an XMPP server, this class also provides interfaces for
 * performing most tasks on the XMPP network.
 */

public class Client extends CoreClient {

    private final MUCManager mucManager;
    private final MUCRegistry mucRegistry;
    private final DirectedPresenceSender directedPresenceSender; //NOPMD, this is not better as a local variable
    private final StanzaChannelPresenceSender stanzaChannelPresenceSender; //NOPMD, this is not better as a local variable
    private final SoftwareVersionResponder softwareVersionResponder;
    private final PubSubManager pubsubManager;
    private final XMPPRosterImpl roster;
    private final XMPPRosterController rosterController;
    private final PresenceOracle presenceOracle;
    private final Storages storages;
    private final MemoryStorages memoryStorages;
    private final VCardManager vcardManager;
    private final CapsManager capsManager;
    private final EntityCapsManager entityCapsManager;
    private final NickManager nickManager;
    private final NickResolver nickResolver;
    private final SubscriptionManager subscriptionManager;
    private final ClientDiscoManager discoManager;
    private final AvatarManager avatarManager;
    private final JingleSessionManager jingleSessionManager;
    private final FileTransferManager fileTransferManager;
    private final BlindCertificateTrustChecker blindCertificateTrustChecker;
    //private final WhiteboardSessionManager whiteboardSessionManager;
    private final ClientBlockListManager blockListManager;

    public final Signal1<Presence> onPresenceChange = new Signal1<Presence>();

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
    public  Client(final JID jid, final SafeByteArray password, final NetworkFactories networkFactories, Storages storages) {
        super(jid, password, networkFactories);
        
        this.storages = storages;
        memoryStorages = new MemoryStorages(networkFactories.getCryptoProvider());
        
        softwareVersionResponder = new SoftwareVersionResponder(getIQRouter());
        softwareVersionResponder.start();
        
        roster = new XMPPRosterImpl();
    	rosterController = new XMPPRosterController(getIQRouter(), roster, getStorages().getRosterStorage());

    	subscriptionManager = new SubscriptionManager(getStanzaChannel());
    	
    	presenceOracle = new PresenceOracle(getStanzaChannel(),roster);
    	presenceOracle.onPresenceChange.connect(onPresenceChange);

        stanzaChannelPresenceSender = new StanzaChannelPresenceSender(getStanzaChannel());
        directedPresenceSender = new DirectedPresenceSender(stanzaChannelPresenceSender);
        discoManager = new ClientDiscoManager(getIQRouter(), directedPresenceSender, networkFactories.getCryptoProvider());

        mucRegistry = new MUCRegistry();
        mucManager = new MUCManager(getStanzaChannel(), getIQRouter(), directedPresenceSender, mucRegistry);

        vcardManager = new VCardManager(jid, getIQRouter(), getStorages().getVCardStorage());
    	avatarManager = new AvatarManagerImpl(vcardManager, getStanzaChannel(), getStorages().getAvatarStorage(), networkFactories.getCryptoProvider(), mucRegistry);
        capsManager = new CapsManager(getStorages().getCapsStorage(), getStanzaChannel(), getIQRouter(), networkFactories.getCryptoProvider());
        entityCapsManager = new EntityCapsManager(capsManager, getStanzaChannel());

    	nickManager = new NickManagerImpl(jid.toBare(), vcardManager);
    	nickResolver = new NickResolver(jid.toBare(), roster, vcardManager, mucRegistry);

        blindCertificateTrustChecker = new BlindCertificateTrustChecker();

        jingleSessionManager = new JingleSessionManager(getIQRouter());
        blockListManager = new ClientBlockListManager(getIQRouter());

        /*whiteboardSessionManager = NULL;
        #ifdef SWIFT_EXPERIMENTAL_WB
            whiteboardSessionManager = new WhiteboardSessionManager(getIQRouter(), getStanzaChannel(), presenceOracle, getEntityCapsProvider());
        #endif*/

    	pubsubManager = new PubSubManagerImpl(getStanzaChannel(), getIQRouter());

        /*#ifdef SWIFT_EXPERIMENTAL_FT
            fileTransferManager = new FileTransferManagerImpl(
                    getJID(),
                    jingleSessionManager,
                    getIQRouter(),
                    getEntityCapsProvider(),
                    presenceOracle,
                    getNetworkFactories().getConnectionFactory(),
                    getNetworkFactories().getConnectionServerFactory(),
                    getNetworkFactories().getTimerFactory(),
                    getNetworkFactories().getDomainNameResolver(),
                    getNetworkFactories().getNetworkEnvironment(),
                    getNetworkFactories().getNATTraverser(),
                    getNetworkFactories().getCryptoProvider());
        #else*/
        fileTransferManager = new DummyFileTransferManager();
        //#endif
    }

    /**
     * Constructs a client for the given JID with the given password.
     *
     * \param storages The interfaces for storing cache information etc. If 
     *  this is NULL,
     *  all data will be stored in memory (and be lost on shutdown)
     */
    public  Client(final JID jid, final SafeByteArray password, final NetworkFactories networkFactories) {
        this(jid, password, networkFactories, null);
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
     * Get the manager for publish-subscribe
     * @return PubSub manager, not null
     */
    public PubSubManager getPubSubManager() {
        return pubsubManager;
    }

    /** 
     * Returns a representation of the roster.
     *
     * The roster is initially empty. To populate it, call requestRoster(),
     * which will request the roster from the server. When the roster has
     * been requested, it will also be kept up to date when it is updated on
     * the server side.
     *
     * This pointer remains the same across the lifetime of Client. All
     * changes to the roster (e.g. after the initial roster request, or after
     * subsequent roster updates) are notified through the XMPPRoster's
     * signals.
     *
     * \see requestRoster()
     */
    public XMPPRoster getRoster() {
    	return roster;
    }

    public void setSoftwareVersion(final String name, final String version) {
        setSoftwareVersion(name, version, "");
    }

    /**
     * Sets the software version of the client.                  
     *
     * This will be used to respond to version queries from other entities.
     */
    public void setSoftwareVersion(final String name, final String version, final String os) {
        softwareVersionResponder.setVersion(name, version, os);
    }

    /**
     * Requests the roster from the server.
     *
     * \see getRoster()
     */
    public void requestRoster() {
    	// FIXME: We should set this once when the session is finished, but there
    	// is currently no callback for this
    	if (getSession() != null) {
    		rosterController.setUseVersioning(getSession().getRosterVersioningSuported());
    	}
    	rosterController.requestRoster();
    }

    public Presence getLastPresence(final JID jid) {
    	return presenceOracle.getLastPresence(jid);
    }

    public Presence getHighestPriorityPresence(final JID bareJID) {
    	return presenceOracle.getHighestPriorityPresence(bareJID);
    }

    public PresenceOracle getPresenceOracle() {
		return presenceOracle;
	}
    
	public NickManager getNickManager() {
    	return nickManager;
    }

    public NickResolver getNickResolver() {
    	return nickResolver;
    }
    
    public SubscriptionManager getSubscriptionManager() {
    	return subscriptionManager;
    }
    
    public ClientDiscoManager getDiscoManager() {
        return discoManager;
    }

    public ClientBlockListManager getClientBlockListManager() {
        return blockListManager;
    }

    /**
     * Returns a FileTransferManager for the client. This is only available after the onConnected 
     * signal has been fired.
     *
     * WARNING: File transfer will only work if Swiften is built in 'experimental' mode.
     */
    public FileTransferManager getFileTransferManager() {
        return fileTransferManager;
    }

    /**
     * Configures the client to always trust a non-validating
     * TLS certificate from the server.
     * This is equivalent to setting a BlindCertificateTrustChecker
     * using setCertificateTrustChecker().
     */
    public void setAlwaysTrustCertificates() {
        setCertificateTrustChecker(blindCertificateTrustChecker);
    }

    //TO PORT
    /*public WhiteboardSessionManager getWhiteboardSessionManager() {
        return whiteboardSessionManager;
    }*/

    public VCardManager getVCardManager() {
    	return vcardManager;
    }
    
    public AvatarManager getAvatarManager() {
    	return avatarManager;
    }
    
    private Storages getStorages()  {
    	if (storages != null) {
    		return storages;
    	}
    	return memoryStorages;
    }

    protected void handleConnected() {
        discoManager.handleConnected();
    }

    public PresenceSender getPresenceSender() {
        return discoManager.getPresenceSender();
    }

    public EntityCapsProvider getEntityCapsProvider() {
        return entityCapsManager;
    }

    
}
