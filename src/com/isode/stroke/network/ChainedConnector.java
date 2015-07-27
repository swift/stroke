/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import java.util.Vector;
import java.util.logging.Logger;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.network.Connection;

public class ChainedConnector {

	private String hostname = "";
	private int port;
	private String serviceLookupPrefix;
	private DomainNameResolver resolver;
	private Vector<ConnectionFactory> connectionFactories = new Vector<ConnectionFactory>();
	private TimerFactory timerFactory;
	private int timeoutMilliseconds;
	private Vector<ConnectionFactory> connectionFactoryQueue = new Vector<ConnectionFactory>();
	private Connector currentConnector;
	private com.isode.stroke.base.Error lastError;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());
	private SignalConnection onConnectFinishedConnection;

	public ChainedConnector(final String hostname, int port, final String serviceLookupPrefix, DomainNameResolver resolver, final Vector<ConnectionFactory> connectionFactories, TimerFactory timer) {
		this.hostname = hostname;
		this.port = port;
		this.serviceLookupPrefix = serviceLookupPrefix;
		this.resolver = resolver;
		this.connectionFactories = connectionFactories;
		this.timerFactory = timer;
		this.timeoutMilliseconds = 0;
	}

	protected void finalize() throws Throwable {
		try {
			if (currentConnector != null) {
				onConnectFinishedConnection.disconnect();
				currentConnector.stop();
				currentConnector = null;
			}
		}
		finally {
			super.finalize();
		}
	}

	public void setTimeoutMilliseconds(int milliseconds) {
		timeoutMilliseconds = milliseconds;
	}

	public void start() {
		logger_.fine("Starting queued connector for " + hostname + "\n");

		connectionFactoryQueue = new Vector<ConnectionFactory>(connectionFactories);
		tryNextConnectionFactory();
	}

	public void stop() {
		if (currentConnector != null) {
			onConnectFinishedConnection.disconnect();
			currentConnector.stop();
			currentConnector = null;
		}
		finish((Connection)null, (com.isode.stroke.base.Error)null);
	}

	public final Signal2<Connection, com.isode.stroke.base.Error> onConnectFinished = new Signal2<Connection, com.isode.stroke.base.Error>();

	private void finish(Connection connection, com.isode.stroke.base.Error error) {
		onConnectFinished.emit(connection, error);
	}

	private void tryNextConnectionFactory() {
		assert(currentConnector == null);
		if (connectionFactoryQueue.isEmpty()) {
			logger_.fine("No more connection factories\n");
			finish((Connection)null, lastError);
		}
		else {
			ConnectionFactory connectionFactory = connectionFactoryQueue.firstElement();
			logger_.fine("Trying next connection factory: " + connectionFactory + "\n");
			connectionFactoryQueue.remove(connectionFactoryQueue.firstElement());
			currentConnector = Connector.create(hostname, port, serviceLookupPrefix, resolver, connectionFactory, timerFactory);
			currentConnector.setTimeoutMilliseconds(timeoutMilliseconds);
			onConnectFinishedConnection = currentConnector.onConnectFinished.connect(new Slot2<Connection, com.isode.stroke.base.Error>() {
				@Override
				public void call(Connection connection, com.isode.stroke.base.Error error) {
					handleConnectorFinished(connection, error);
				}
			});
			currentConnector.start();
		}
	}

	private void handleConnectorFinished(Connection connection, com.isode.stroke.base.Error error) {
		logger_.fine("Connector finished\n");
		onConnectFinishedConnection.disconnect();
		lastError = error;
		currentConnector = null;
		if (connection != null) {
			finish(connection, error);
		}
		else {
			tryNextConnectionFactory();
		}
	}
}