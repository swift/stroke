/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2013-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.jid.JID;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Signal1;
import java.util.Vector;
import java.util.logging.Logger;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.S5BProxyRequest;

public class LocalJingleTransportCandidateGenerator {

	public static final int LOCAL_PREFERENCE = 0;
	private SOCKS5BytestreamServerManager s5bServerManager;
	private SOCKS5BytestreamProxiesManager s5bProxy;
	private JID ownJID;
	private IDGenerator idGenerator;
	//private SOCKS5BytestreamServerInitializeRequest s5bServerInitializeRequest;
	private SOCKS5BytestreamServerResourceUser s5bServerResourceUser_;
	private SOCKS5BytestreamServerPortForwardingUser s5bServerPortForwardingUser_;
	private boolean triedServerInit_;
	private boolean triedForwarding_;
	private boolean triedProxyDiscovery_;
	private FileTransferOptions options_;
	private SignalConnection onDiscoveredProxiesChangedConnection;
	private Logger logger_  = Logger.getLogger(this.getClass().getName());

	public LocalJingleTransportCandidateGenerator(
			SOCKS5BytestreamServerManager s5bServerManager,
			SOCKS5BytestreamProxiesManager s5bProxy, 
			final JID ownJID,
			IDGenerator idGenerator,
			final FileTransferOptions options) {
		this.s5bServerManager = s5bServerManager;
		this.s5bProxy = s5bProxy;
		this.ownJID = ownJID;
		this.idGenerator = idGenerator;
		triedProxyDiscovery_ = false;
		triedServerInit_ = false;
		triedForwarding_ = false;
		this.options_ = options;
	}

	public void start() {
		//assert(s5bServerInitializeRequest == null);
		if (options_.isDirectAllowed() || options_.isAssistedAllowed()) {
			s5bServerResourceUser_ = s5bServerManager.aquireResourceUser();
			if (s5bServerResourceUser_.isInitialized()) {
				handleS5BServerInitialized(true);
			}
			else {
				s5bServerResourceUser_.onSuccessfulInitialized.connect(new Slot1<Boolean>() {
					@Override
					public void call(Boolean b) {
						handleS5BServerInitialized(b);
					}
				});
			}
		} else {
			handleS5BServerInitialized(false);
		}

		if (options_.isProxiedAllowed()) {
			onDiscoveredProxiesChangedConnection = s5bProxy.onDiscoveredProxiesChanged.connect(new Slot() {
				@Override
				public void call() {
					handleDiscoveredProxiesChanged();
				}
			});
			if (s5bProxy.getOrDiscoverS5BProxies() != null) {
				handleDiscoveredProxiesChanged();
			}
		}
	}

	public void stop() {
		onDiscoveredProxiesChangedConnection.disconnect();
		if (s5bServerPortForwardingUser_ != null) {
			s5bServerPortForwardingUser_.onSetup.disconnectAll();
			s5bServerPortForwardingUser_ = null;
		}
		if (s5bServerResourceUser_ != null) {
			s5bServerResourceUser_.onSuccessfulInitialized.disconnectAll();
			s5bServerResourceUser_ = null;
		}
	}

	public Signal1<Vector<JingleS5BTransportPayload.Candidate>> onLocalTransportCandidatesGenerated = new Signal1<Vector<JingleS5BTransportPayload.Candidate>>();

	private void handleS5BServerInitialized(boolean success) {
		if (s5bServerResourceUser_ != null) {
			s5bServerResourceUser_.onSuccessfulInitialized.disconnectAll();
		}
		triedServerInit_ = true;
		if (success) {
			if (options_.isAssistedAllowed()) {
				// try to setup port forwarding
				s5bServerPortForwardingUser_ = s5bServerManager.aquirePortForwardingUser();
				s5bServerPortForwardingUser_.onSetup.connect(new Slot1<Boolean>() {
					@Override
					public void call(Boolean b) {
						handlePortForwardingSetup(b);
					}
				});
				if (s5bServerPortForwardingUser_.isForwardingSetup()) {
					handlePortForwardingSetup(true);
				}
			}
		}
		else {
			logger_.warning("Unable to start SOCKS5 server\n");
			if (s5bServerResourceUser_ != null) {
				s5bServerResourceUser_.onSuccessfulInitialized.disconnectAll();
			}
			s5bServerResourceUser_ = null;
			handlePortForwardingSetup(false);
		}
		checkS5BCandidatesReady();
	}

	private void handlePortForwardingSetup(boolean success) {
		if (s5bServerPortForwardingUser_ != null) {
			s5bServerPortForwardingUser_.onSetup.disconnectAll();
		}
		triedForwarding_ = true;
		checkS5BCandidatesReady();
	}

	private void handleDiscoveredProxiesChanged() {
		if (s5bProxy != null) {
			s5bProxy.onDiscoveredProxiesChanged.disconnectAll();
		}
		triedProxyDiscovery_ = true;
		checkS5BCandidatesReady();
	}

	private void checkS5BCandidatesReady() {
		if ((!options_.isDirectAllowed()  || (options_.isDirectAllowed()  && triedServerInit_)) &&
			(!options_.isProxiedAllowed() || (options_.isProxiedAllowed() && triedProxyDiscovery_)) &&
			(!options_.isAssistedAllowed()  || (options_.isAssistedAllowed()  && triedForwarding_))) {
			emitOnLocalTransportCandidatesGenerated();
		}
	}

	private void emitOnLocalTransportCandidatesGenerated() {
		Vector<JingleS5BTransportPayload.Candidate> candidates = new Vector<JingleS5BTransportPayload.Candidate>();

		if (options_.isDirectAllowed()) {
			// get direct candidates
			Vector<HostAddressPort> directCandidates = s5bServerManager.getHostAddressPorts();
			for(HostAddressPort addressPort : directCandidates) {
				JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
				candidate.type = JingleS5BTransportPayload.Candidate.Type.DirectType;
				candidate.jid = ownJID;
				candidate.hostPort = addressPort;
				candidate.priority = 65536 * 126 + LOCAL_PREFERENCE;
				candidate.cid = idGenerator.generateID();
				candidates.add(candidate);
			}
		}

		if (options_.isAssistedAllowed()) {
			// get assissted candidates
			Vector<HostAddressPort> assisstedCandidates = s5bServerManager.getAssistedHostAddressPorts();
			for(HostAddressPort addressPort : assisstedCandidates) {
				JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
				candidate.type = JingleS5BTransportPayload.Candidate.Type.AssistedType;
				candidate.jid = ownJID;
				candidate.hostPort = addressPort;
				candidate.priority = 65536 * 120 + LOCAL_PREFERENCE;
				candidate.cid = idGenerator.generateID();
				candidates.add(candidate);
			}
		}

		if (options_.isProxiedAllowed() && s5bProxy.getOrDiscoverS5BProxies() != null) {
			for(S5BProxyRequest proxy : s5bProxy.getOrDiscoverS5BProxies()) {
				if (proxy.getStreamHost() != null) { // FIXME: Added this test, because there were cases where this wasn't initialized. Investigate this. (Remko)
					JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
					candidate.type = JingleS5BTransportPayload.Candidate.Type.ProxyType;
					candidate.jid = (proxy.getStreamHost()).jid;
					HostAddress address = new HostAddress((proxy.getStreamHost()).host);
					assert(address.isValid());
					candidate.hostPort = new HostAddressPort(address, (proxy.getStreamHost()).port);
					candidate.priority = 65536 * 10 + LOCAL_PREFERENCE;
					candidate.cid = idGenerator.generateID();
					candidates.add(candidate);
				}
			}
		}

		onLocalTransportCandidatesGenerated.emit(candidates);
	}
}