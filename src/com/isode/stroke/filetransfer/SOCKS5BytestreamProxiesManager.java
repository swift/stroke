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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.DomainNameAddressQuery;
import com.isode.stroke.network.DomainNameResolveError;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;

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

	private static class Pair {
		public JID jid;
		public SOCKS5BytestreamClientSession sock5;

		public Pair(JID j, SOCKS5BytestreamClientSession c) {this.jid = j; this.sock5 = c; }
	}

	private Map<String, Collection<Pair> > proxySessions_ = new HashMap<String, Collection<Pair> >();

	private SignalConnection onProxiesFoundConnection;
    /**
	 * Map between {@link SOCKS5BytestreamClientSession} and a {@link SignalConnection} to their
	 * {@link SOCKS5BytestreamClientSession#onSessionReady}
	 */
	private Map<SOCKS5BytestreamClientSession,SignalConnection> onSessionReadyConnectionMap =
	        new HashMap<SOCKS5BytestreamClientSession,SignalConnection>();
	
	/**
     * Map between {@link SOCKS5BytestreamClientSession} and a {@link SignalConnection} to their
     * {@link SOCKS5BytestreamClientSession#onFinished}
     */
	private Map<SOCKS5BytestreamClientSession,SignalConnection> onFinishedConnectionMap =
	        new HashMap<SOCKS5BytestreamClientSession,SignalConnection>();
	
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
				SignalConnection onSessionReadyConnection = session.onSessionReady.connect(new Slot1<Boolean>() {
					@Override
					public void call(Boolean b) {
						handleProxySessionReady(sessionID, proxyJid, session, b);
					}
				});
				onSessionReadyConnectionMap.put(session, onSessionReadyConnection);
				SignalConnection onFinishedConnection = session.onFinished.connect(new Slot1<FileTransferError>() {
					@Override
					public void call(FileTransferError e) {
						handleProxySessionFinished(sessionID, proxyJid, session, e);
					}
				});
				onFinishedConnectionMap.put(session, onFinishedConnection);
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
		    SignalConnection onSessionReadyConnection = 
		            onSessionReadyConnectionMap.remove(i.sock5);
		    if (onSessionReadyConnection != null) {
		        onSessionReadyConnection.disconnect();
		    }
		    SignalConnection onFinishedConnection =
		            onFinishedConnectionMap.remove(i.sock5);
		    if (onFinishedConnection != null) {
		        onFinishedConnection.disconnect();
		    }
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
	
    private void handleProxiesFound(Collection<? extends S5BProxyRequest> proxyHosts) {
	    if (onProxiesFoundConnection != null) {
	        onProxiesFoundConnection.disconnect();
	        onProxiesFoundConnection = null;
	    }
	    for (final S5BProxyRequest proxy : proxyHosts) {
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
	    }
	    proxyFinder_.stop();
	    proxyFinder_ = null;
	    if (proxyHosts.isEmpty()) {
	        onDiscoveredProxiesChanged.emit();
	    }
	}
	
	private void handleNameLookupResult(final Collection<HostAddress> addresses, DomainNameResolveError error, S5BProxyRequest proxy) {
		if (error != null) {
			onDiscoveredProxiesChanged.emit();
		}
		else {
			if (addresses.isEmpty()) {
				logger_.warning("S5B proxy hostname does not resolve.\n");
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
			}
			onDiscoveredProxiesChanged.emit();
		}
	}

	private void queryForProxies() {
		proxyFinder_ = new SOCKS5BytestreamProxyFinder(serviceRoot_, iqRouter_);
		onProxiesFoundConnection = proxyFinder_.onProxiesFound.connect(new Slot1<List<S5BProxyRequest>>() {
			@Override
			public void call(List<S5BProxyRequest> s) {
				handleProxiesFound(s);
			}
		});
		proxyFinder_.start();
	}

	private void handleProxySessionReady(final String sessionID, final JID jid, SOCKS5BytestreamClientSession session, boolean error) {
	    SignalConnection onSessionReadyConnection = onSessionReadyConnectionMap.remove(session);
	    if (onSessionReadyConnection != null) {
	        onSessionReadyConnection.disconnect();
	    }
		if (!error) {
			// The SOCKS5 bytestream session to the proxy succeeded; stop and remove other sessions.
			if (proxySessions_.containsKey(sessionID)) {
				Iterator<Pair> iterator = proxySessions_.get(sessionID).iterator();
				while (iterator.hasNext()) {
				    Pair i = iterator.next();
				    if ((i.jid.equals(jid)) && (!i.sock5.equals(session))) {
                        i.sock5.stop();
                        iterator.remove();; //Swiften assigns i, so that iterator points to the next element.
                    }
				}
			}
		}
	}

	private void handleProxySessionFinished(final String sessionID, final JID jid, SOCKS5BytestreamClientSession session, FileTransferError error) {
	    SignalConnection onFinishedConnection = onFinishedConnectionMap.remove(session);
	    if (onFinishedConnection != null) {
	        onFinishedConnection.disconnect();
	    }
		if (error != null) {
			// The SOCKS5 bytestream session to the proxy failed; remove it.
			if (proxySessions_.containsKey(sessionID)) {
			    Iterator<Pair> iterator = proxySessions_.get(sessionID).iterator();
			    while (iterator.hasNext()) {
			        Pair i = iterator.next();
			        if ((i.jid.equals(jid)) && (i.sock5.equals(session))) {
                        i.sock5.stop();
                        iterator.remove();; //Swiften assigns i, so that iterator points to the next element.
                        break;
                    }
			    }
			}
		}
	}
}