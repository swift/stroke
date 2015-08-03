/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.network.Connection;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.DomainNameResolveError;
import com.isode.stroke.network.DomainNameAddressQuery;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *	- manages list of working S5B proxies
 *	- creates initial connections (for the candidates you provide)
 */
public class SOCKS5BytestreamProxiesManager {

	private ConnectionFactory connectionFactory_;
	private TimerFactory timerFactory_;
	private DomainNameResolver resolver_;
	private IQRouter iqRouter_;
	private JID serviceRoot_;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());
	private SignalConnection onSessionReadyConnection;
	private SignalConnection onFinishedConnection;

	private static class Pair {
		public JID jid;
		public SOCKS5BytestreamClientSession sock5;

		public Pair(JID j, SOCKS5BytestreamClientSession c) {this.jid = j; this.sock5 = c; }
	}

	private Map<String, Collection<Pair> > proxySessions_ = new HashMap<String, Collection<Pair> >();

	private SOCKS5BytestreamProxyFinder proxyFinder_;

	private Collection<S5BProxyRequest> localS5BProxies_;

	public SOCKS5BytestreamProxiesManager(ConnectionFactory connFactory, TimerFactory timeFactory, DomainNameResolver resolver, IQRouter iqRouter, final JID serviceRoot) {
		connectionFactory_ = connFactory;
		timerFactory_ = timeFactory;
		resolver_ = resolver;
		iqRouter_ = iqRouter;
		serviceRoot_ = serviceRoot;
	}

	public void addS5BProxy(S5BProxyRequest proxy) {
		if (proxy != null) {
			//SWIFT_LOG_ASSERT(HostAddress(proxy.getStreamHost().get().host).isValid(), warning) << std::endl;
			if (localS5BProxies_ == null) {
				localS5BProxies_ = new Vector<S5BProxyRequest>();
			}
			localS5BProxies_.add(proxy);
		}
	}

	/*
	 * Returns a list of external S5B proxies. If the optinal return value is not initialized a discovery process has been started and
	 * onDiscoveredProxiesChanged signal will be emitted when it is finished.
	 */
	public Collection<S5BProxyRequest> getOrDiscoverS5BProxies() {
		if (localS5BProxies_ == null && proxyFinder_ == null) {
			queryForProxies();
		}
		return localS5BProxies_;
	}

	public void connectToProxies(final String sessionID) {
		logger_.fine("session ID: " + sessionID + "\n");
		Collection<Pair> clientSessions = new Vector<Pair>();

		if (localS5BProxies_ != null) {
			for(S5BProxyRequest proxy : localS5BProxies_) {
				Connection conn = connectionFactory_.createConnection();

				HostAddressPort addressPort = new HostAddressPort(new HostAddress(proxy.getStreamHost().host), proxy.getStreamHost().port);
				//SWIFT_LOG_ASSERT(addressPort.isValid(), warning) << std::endl;
				final SOCKS5BytestreamClientSession session = new SOCKS5BytestreamClientSession(conn, addressPort, sessionID, timerFactory_);
				final JID proxyJid = proxy.getStreamHost().jid;
				clientSessions.add(new Pair(proxyJid, session));
				onSessionReadyConnection = session.onSessionReady.connect(new Slot1<Boolean>() {
					@Override
					public void call(Boolean b) {
						handleProxySessionReady(sessionID, proxyJid, session, b);
					}
				});
				onFinishedConnection = session.onFinished.connect(new Slot1<FileTransferError>() {
					@Override
					public void call(FileTransferError e) {
						handleProxySessionFinished(sessionID, proxyJid, session, e);
					}
				});
				session.start();
			}
		}

		proxySessions_.put(sessionID, clientSessions);
	}

	public SOCKS5BytestreamClientSession getProxySessionAndCloseOthers(final JID proxyJID, final String sessionID) {
		// checking parameters
		if (!proxySessions_.containsKey(sessionID)) {
			return null;
		}

		// get active session
		SOCKS5BytestreamClientSession activeSession = null;
		for(Pair i : proxySessions_.get(sessionID)) {
			i.sock5.onSessionReady.disconnectAll();
			i.sock5.onFinished.disconnectAll();
			if (i.jid.equals(proxyJID) && activeSession == null) {
				activeSession = i.sock5;
			}
			else {
				i.sock5.stop();
			}
		}

		proxySessions_.remove(sessionID);

		return activeSession;
	}

	public SOCKS5BytestreamClientSession createSOCKS5BytestreamClientSession(HostAddressPort addressPort, final String destAddr) {
		SOCKS5BytestreamClientSession connection = new SOCKS5BytestreamClientSession(connectionFactory_.createConnection(), addressPort, destAddr, timerFactory_);
		return connection;
	}

	public final Signal onDiscoveredProxiesChanged = new Signal();

	private void handleProxyFound(final S5BProxyRequest proxy) {
		if (proxy != null) {
			if (new HostAddress(proxy.getStreamHost().host).isValid()) {
				addS5BProxy(proxy);
				onDiscoveredProxiesChanged.emit();
			}
			else {
				DomainNameAddressQuery resolveRequest = resolver_.createAddressQuery(proxy.getStreamHost().host);
				resolveRequest.onResult.connect(new Slot2<Collection<HostAddress>, DomainNameResolveError>() {
					@Override
					public void call(Collection<HostAddress> c, DomainNameResolveError d) {
						handleNameLookupResult(c, d, proxy);
					}
				});
				resolveRequest.run();
			}
		}
		else {
			onDiscoveredProxiesChanged.emit();
		}
		proxyFinder_.stop();
		proxyFinder_ = null;
	}

	private void handleNameLookupResult(final Collection<HostAddress> addresses, DomainNameResolveError error, S5BProxyRequest proxy) {
		if (error != null) {
			onDiscoveredProxiesChanged.emit();
		}
		else {
			if (addresses.isEmpty()) {
				logger_.warning("S5B proxy hostname does not resolve.\n");
				onDiscoveredProxiesChanged.emit();
			}
			else {
				// generate proxy per returned address
				for (final HostAddress address : addresses) {
					S5BProxyRequest.StreamHost streamHost = proxy.getStreamHost();
					S5BProxyRequest proxyForAddress = proxy;
					streamHost.host = address.toString();
					proxyForAddress.setStreamHost(streamHost);
					addS5BProxy(proxyForAddress);
				}
				onDiscoveredProxiesChanged.emit();
			}
		}
	}

	private void queryForProxies() {
		proxyFinder_ = new SOCKS5BytestreamProxyFinder(serviceRoot_, iqRouter_);

		proxyFinder_.onProxyFound.connect(new Slot1<S5BProxyRequest>() {
			@Override
			public void call(S5BProxyRequest s) {
				handleProxyFound(s);
			}
		});
		proxyFinder_.start();
	}

	private void handleProxySessionReady(final String sessionID, final JID jid, SOCKS5BytestreamClientSession session, boolean error) {
		onSessionReadyConnection.disconnect();
		if (!error) {
			// The SOCKS5 bytestream session to the proxy succeeded; stop and remove other sessions.
			if (proxySessions_.containsKey(sessionID)) {
				for(Pair i : proxySessions_.get(sessionID)) {
					if ((i.jid.equals(jid)) && (!i.sock5.equals(session))) {
						i.sock5.stop();
						proxySessions_.get(sessionID).remove(i); //Swiften assigns i, so that iterator points to the next element.
					}
				}
			}
		}
	}

	private void handleProxySessionFinished(final String sessionID, final JID jid, SOCKS5BytestreamClientSession session, FileTransferError error) {
		onFinishedConnection.disconnect();
		if (error != null) {
			// The SOCKS5 bytestream session to the proxy failed; remove it.
			if (proxySessions_.containsKey(sessionID)) {
				for(Pair i : proxySessions_.get(sessionID)) {
					if ((i.jid.equals(jid)) && (i.sock5.equals(session))) {
						i.sock5.stop();
						proxySessions_.get(sessionID).remove(i); //Swiften assigns i, so that iterator points to the next element.
						break;
					}
				}
			}
		}
	}
}