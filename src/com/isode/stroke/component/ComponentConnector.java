/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.component;

import com.isode.stroke.network.Connection;
import com.isode.stroke.network.JavaConnection;
import com.isode.stroke.network.Timer;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.DomainNameResolveError;
import com.isode.stroke.network.DomainNameAddressQuery;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;
import java.util.Vector;
import java.util.Collection;

public class ComponentConnector {

	private String hostname = "";
	private int port;
	private DomainNameResolver resolver;
	private ConnectionFactory connectionFactory;
	private TimerFactory timerFactory;
	private int timeoutMilliseconds;
	private Timer timer;
	private DomainNameAddressQuery addressQuery;
	private Vector<HostAddress> addressQueryResults = new Vector<HostAddress>();
	private Connection currentConnection;
	private SignalConnection onConnectFinishedConnection;
	private SignalConnection onTickConnection;
	private SignalConnection onResultConnection;

	public ComponentConnector(final String hostname, int port, DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory) {
		this.hostname = hostname;
		this.port = port;
		this.resolver = resolver;
		this.connectionFactory = connectionFactory;
		this.timerFactory = timerFactory;
		this.timeoutMilliseconds = 0;
	}

	public static ComponentConnector create(final String hostname, int port, DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory) {
		return new ComponentConnector(hostname, port, resolver, connectionFactory, timerFactory);
	}

	public void setTimeoutMilliseconds(int milliseconds) {
		timeoutMilliseconds = milliseconds;
	}

	public void start() {
		assert(currentConnection == null);
		assert(timer == null);
		assert(addressQuery == null);
		addressQuery = resolver.createAddressQuery(hostname);
		onResultConnection = addressQuery.onResult.connect(new Slot2<Collection<HostAddress>, DomainNameResolveError>() {
			@Override
			public void call(Collection<HostAddress> c1, DomainNameResolveError d1) {
				handleAddressQueryResult(c1, d1);	
			}
		});
		if (timeoutMilliseconds > 0) {
			timer = timerFactory.createTimer(timeoutMilliseconds);
			onTickConnection = timer.onTick.connect(new Slot() {
				@Override
				public void call() {
					handleTimeout();
				}
			});
			timer.start();
		}
		addressQuery.run();
	}

	public void stop() {
		finish((Connection)null);
	}

	public final Signal1<Connection> onConnectFinished = new Signal1<Connection>();

	private void handleAddressQueryResult(final Collection<HostAddress> addresses, DomainNameResolveError error) {
		addressQuery = null;
		if (error != null || addresses.isEmpty()) {
			finish((Connection)null);
		}
		else {
			addressQueryResults.addAll(addresses);
			tryNextAddress();
		}
	}

	private void tryNextAddress() {
		assert(!addressQueryResults.isEmpty());
		HostAddress address = addressQueryResults.remove(0);
		tryConnect(new HostAddressPort(address, port));
	}

	private void tryConnect(final HostAddressPort target) {
		assert(currentConnection == null);
		currentConnection = connectionFactory.createConnection();
		onConnectFinishedConnection = currentConnection.onConnectFinished.connect(new Slot1<Boolean>() {
			@Override
			public void call(Boolean b) {
				handleConnectionConnectFinished(b);
			}
		});
		currentConnection.connect(target);
	}

	private void handleConnectionConnectFinished(boolean error) {
		onConnectFinishedConnection.disconnect();
		if (error) {
			currentConnection = null;
			if (!addressQueryResults.isEmpty()) {
				tryNextAddress();
			}
			else {
				finish((Connection)null);
			}
		}
		else {
			finish(currentConnection);
		}
	}

	private void finish(Connection connection) {
		if (timer != null) {
			timer.stop();
			onTickConnection.disconnect();
			timer = null;
		}
		if (addressQuery != null) {
			onResultConnection.disconnect();
			addressQuery = null;
		}
		if (currentConnection != null) {
			onConnectFinishedConnection.disconnect();
			currentConnection = null;
		}
		onConnectFinished.emit(connection);
	}

	private void handleTimeout() {
		finish((Connection)null);
	}
}