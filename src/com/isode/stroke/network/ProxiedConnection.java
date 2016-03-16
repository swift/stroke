/*
 * Copyright (c) 2012-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import java.util.logging.Logger;

import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.base.SafeByteArray;

public abstract class ProxiedConnection extends Connection {

	private boolean connected_;
	private DomainNameResolver resolver_;
	private ConnectionFactory connectionFactory_;	
	private TimerFactory timerFactory_;
	private String proxyHost_ = "";
	private int proxyPort_;
	private HostAddressPort server_;
	private Connector connector_;
	private Connection connection_;
	private SignalConnection onDataReadConnection_;
	private SignalConnection onDisconnectedConnection_;
	private SignalConnection onConnectFinishedConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public ProxiedConnection(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort) {
		this.resolver_ = resolver;
		this.connectionFactory_ = connectionFactory;
		this.timerFactory_ = timerFactory;
		this.proxyHost_ = proxyHost;
		this.proxyPort_ = proxyPort;
		this.server_ = new HostAddressPort(new HostAddress("0.0.0.0"), 0);
		this.connected_ = false;
	}

	protected void finalize() throws Throwable {
		try {
			cancelConnector();
			if (connection_ != null) {
				onDataReadConnection_.disconnect();
				onDisconnectedConnection_.disconnect();
			}
			if (connected_) {
				logger_.warning("Warning: Connection was still established.");
			}
		}
		finally {
			super.finalize();
		}
	}

	public void listen() {
		assert(false);
		connection_.listen();		
	}

	public void connect(final HostAddressPort server) {
		server_ = server;

		connector_ = Connector.create(proxyHost_, proxyPort_, null, resolver_, connectionFactory_, timerFactory_);
		onConnectFinishedConnection = connector_.onConnectFinished.connect(new Slot2<Connection, com.isode.stroke.base.Error>() {
			@Override
			public void call(Connection c, com.isode.stroke.base.Error e) {
				handleConnectFinished(c);
			}
		});
		connector_.start();
	}

	public void disconnect() {
	    cancelConnector();
		connected_ = false;
		if (connection_ != null) {
		    connection_.disconnect();
		}
	}

	public void write(final SafeByteArray data) {
		connection_.write(data);
	}

	public HostAddressPort getLocalAddress() {
		return connection_.getLocalAddress();
	}
	
	@Override
	public HostAddressPort getRemoteAddress() {
	    return connection_.getRemoteAddress();
	}

	private void handleConnectFinished(Connection connection) {
		cancelConnector();
		if (connection != null) {
			connection_ = connection;
			connection_.onDataRead.connect(new Slot1<SafeByteArray>() {
				@Override
				public void call(SafeByteArray s) {
					handleDataRead(s);
				}
			});
			connection_.onDisconnected.connect(new Slot1<Error>() {
				@Override
				public void call(Error e) {
					handleDisconnected(e);
				}
			});

			initializeProxy();
		}
		else {
			onConnectFinished.emit(true);
		}
	}

	private void handleDataRead(SafeByteArray data) {
		if (!connected_) {
			handleProxyInitializeData(data);
		}
		else {
			onDataRead.emit(data);
		}
	}

	private void handleDisconnected(final Error error) {
		onDisconnected.emit(error);
	}

	private void cancelConnector() {
		if (connector_ != null) {
			onConnectFinishedConnection.disconnect();
			connector_.stop();
			connector_ = null;
		}
	}

	protected void setProxyInitializeFinished(boolean success) {
		connected_ = success;
		if (!success) {
			disconnect();
		}
		onConnectFinished.emit(!success);		
	}

	protected abstract void initializeProxy();
	protected abstract void handleProxyInitializeData(SafeByteArray data);

	protected HostAddressPort getServer() {
		return server_;
	}
	
	protected void reconnect() {
	    if (onDataReadConnection_ != null) {
	        onDataReadConnection_.disconnect();
	        onDataReadConnection_ = null;
	    }
	    if (onDisconnectedConnection_ != null) {
	        onDisconnectedConnection_.disconnect();
	        onDisconnectedConnection_ = null;
	    }
	    if (connected_) {
	        connection_.disconnect();
	    }
	    connect(server_);
	}
	
}