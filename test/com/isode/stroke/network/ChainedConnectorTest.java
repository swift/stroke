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

package com.isode.stroke.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.ChainedConnector;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.StaticDomainNameResolver;
import com.isode.stroke.network.DummyTimerFactory;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.Event;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.network.DomainNameResolveError;
import java.util.Vector;

public class ChainedConnectorTest {

	private HostAddressPort host;
	private DummyEventLoop eventLoop;
	private StaticDomainNameResolver resolver;
	private MockConnectionFactory connectionFactory1;
	private MockConnectionFactory connectionFactory2;
	private DummyTimerFactory timerFactory;
	private Vector<MockConnection> connections = new Vector<MockConnection>();
	private com.isode.stroke.base.Error error;

	private class MockConnection extends Connection {

		public MockConnection(boolean connects, int id, EventLoop eventLoop) {
			this.connects = connects;
			this.id = id;
			this.eventLoop = eventLoop;
		}

		public void listen() { assert(false); }

		public void connect(final HostAddressPort port) {
			eventLoop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					onConnectFinished.emit(!connects);
				}
			});
		}

		public HostAddressPort getLocalAddress()  { return new HostAddressPort(); }
		public void disconnect() { assert(false); }
		public void write(final SafeByteArray data) { assert(false); }

		public boolean connects;
		public int id;
		public EventLoop eventLoop;
	};

	private class MockConnectionFactory implements ConnectionFactory {
		public MockConnectionFactory(EventLoop eventLoop, int id) {
			this.eventLoop = eventLoop;
			this.connects = true;
			this.id = id;
		}

		public Connection createConnection() {
			return new MockConnection(connects, id, eventLoop);
		}

		public EventLoop eventLoop;
		public boolean connects;
		public int id;
	};

	private ChainedConnector createConnector() {
		Vector<ConnectionFactory> factories = new Vector<ConnectionFactory>();
		factories.add(connectionFactory1);
		factories.add(connectionFactory2);
		ChainedConnector connector = new ChainedConnector("foo.com", -1, "_xmpp-client._tcp.", resolver, factories, timerFactory);
		connector.onConnectFinished.connect(new Slot2<Connection, com.isode.stroke.base.Error>() {
			@Override
			public void call(Connection c, com.isode.stroke.base.Error e) {
				handleConnectorFinished(c, e);
			}
		});
		return connector;
	}

	private void handleConnectorFinished(Connection connection, com.isode.stroke.base.Error resultError) {
		error = resultError;
		MockConnection c = (MockConnection)(connection);
		if (connection != null) {
			assert(c != null);
		}
		connections.add(c);
	}

	@Before
	public void setUp() {
		error = null;
		host = new HostAddressPort(new HostAddress("1.1.1.1"), 1234);
		eventLoop = new DummyEventLoop();
		resolver = new StaticDomainNameResolver(eventLoop);
		resolver.addXMPPClientService("foo.com", host);
		connectionFactory1 = new MockConnectionFactory(eventLoop, 1);
		connectionFactory2 = new MockConnectionFactory(eventLoop, 2);
		timerFactory = new DummyTimerFactory();
	}

	@Test
	public void testConnect_FirstConnectorSucceeds() {
		ChainedConnector testling = createConnector();
		connectionFactory1.connects = true;
		connectionFactory2.connects = false;

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(1, ((MockConnection)(connections.get(0))).id);
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_SecondConnectorSucceeds() {
		ChainedConnector testling = createConnector();
		connectionFactory1.connects = false;
		connectionFactory2.connects = true;

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(2, ((MockConnection)(connections.get(0))).id);
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoConnectorSucceeds() {
		ChainedConnector testling = createConnector();
		connectionFactory1.connects = false;
		connectionFactory2.connects = false;

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoDNS() {
		/* Reset resolver so there's no record */
		resolver = null;
		resolver = new StaticDomainNameResolver(eventLoop);
		ChainedConnector testling = createConnector();
		connectionFactory1.connects = false;
		connectionFactory2.connects = false;

		testling.start();
		//testling.stop();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
		assertNotNull((DomainNameResolveError)(error));
	}

	@Test
	public void testStop() {
		ChainedConnector testling = createConnector();
		connectionFactory1.connects = true;
		connectionFactory2.connects = false;

		testling.start();
		testling.stop();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
	}
}