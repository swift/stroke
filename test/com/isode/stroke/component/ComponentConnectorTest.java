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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.Event;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.network.Connector;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.StaticDomainNameResolver;
import com.isode.stroke.network.DummyTimerFactory;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.network.DomainNameAddressQuery;
import java.util.Vector;

public class ComponentConnectorTest {

	private HostAddress host1;
	private HostAddress host2;
	private DummyEventLoop eventLoop;
	private StaticDomainNameResolver resolver;
	private MockConnectionFactory connectionFactory;
	private DummyTimerFactory timerFactory;
	private Vector<MockConnection> connections = new Vector<MockConnection>();

	private class MockConnection extends Connection {

		public MockConnection(final Vector<HostAddressPort> failingPorts, boolean isResponsive, EventLoop eventLoop) {
			this.eventLoop = eventLoop;
			this.failingPorts = failingPorts;
			this.isResponsive = isResponsive;
		}

		public void listen() { assert(false); }

		public void connect(final HostAddressPort address) {
			hostAddressPort = address;
			if(isResponsive) {
				final boolean fail = failingPorts.contains(address);
				eventLoop.postEvent(new Event.Callback() {
					@Override
					public void run() {
						onConnectFinished.emit(fail);
					}
				});
			}
		}

		public HostAddressPort getLocalAddress() { return new HostAddressPort(); }

		public void disconnect() { assert(false); }
				
		public void write(final SafeByteArray data) { assert(false); }

		public EventLoop eventLoop;
		public HostAddressPort hostAddressPort;
		public Vector<HostAddressPort> failingPorts = new Vector<HostAddressPort>();
		public boolean isResponsive;
	};

	private class MockConnectionFactory implements ConnectionFactory {
		public MockConnectionFactory(EventLoop eventLoop) {
			this.eventLoop = eventLoop;
			this.isResponsive = true;
		}

		public Connection createConnection() {
			return new MockConnection(failingPorts, isResponsive, eventLoop);
		}

		public EventLoop eventLoop;
		public boolean isResponsive;
		public Vector<HostAddressPort> failingPorts = new Vector<HostAddressPort>();
	};

	private ComponentConnector createConnector(final String hostname, int port) {
		ComponentConnector connector = ComponentConnector.create(hostname, port, resolver, connectionFactory, timerFactory);
		connector.onConnectFinished.connect(new Slot1<Connection>() {
			@Override
			public void call(Connection c) {
				handleConnectorFinished(c);
			}
		});
		return connector;
	}

	private void handleConnectorFinished(Connection connection) {
		MockConnection c = (MockConnection)(connection);
		if (connection != null) {
			assert(c != null);
		}
		connections.add(c);
	}

	@Before
	public void setUp() {
		host1 = new HostAddress("1.1.1.1");
		host2 = new HostAddress("2.2.2.2");
		eventLoop = new DummyEventLoop();
		resolver = new StaticDomainNameResolver(eventLoop);
		connectionFactory = new MockConnectionFactory(eventLoop);
		timerFactory = new DummyTimerFactory();
	}

	@Test
	public void testConnect() {
		ComponentConnector testling = createConnector("foo.com", 1234);
		resolver.addAddress("foo.com", host1);

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(new HostAddressPort(host1, 1234), (connections.get(0).hostAddressPort));
	}

	@Test
	public void testConnect_FirstAddressHostFails() {
		ComponentConnector testling = createConnector("foo.com", 1234);
		resolver.addAddress("foo.com", host1);
		resolver.addAddress("foo.com", host2);
		connectionFactory.failingPorts.add(new HostAddressPort(host1, 1234));

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(new HostAddressPort(host2, 1234), (connections.get(0).hostAddressPort));
	}

	@Test
	public void testConnect_NoHosts() {
		ComponentConnector testling = createConnector("foo.com", 1234);

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
	}

	@Test
	public void testConnect_TimeoutDuringResolve() {
		ComponentConnector testling = createConnector("foo.com", 1234);

		testling.setTimeoutMilliseconds(10);
		resolver.setIsResponsive(false);

		testling.start();
		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
	}

	@Test
	public void testConnect_TimeoutDuringConnect() {
		ComponentConnector testling = createConnector("foo.com", 1234);
		testling.setTimeoutMilliseconds(10);
		resolver.addAddress("foo.com", host1);
		connectionFactory.isResponsive = false;

		testling.start();
		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
	}

	@Test
	public void testConnect_NoTimeout() {
		ComponentConnector testling = createConnector("foo.com", 1234);
		testling.setTimeoutMilliseconds(10);
		resolver.addAddress("foo.com", host1);

		testling.start();
		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
	}

	@Test
	public void testStop_Timeout() {
		ComponentConnector testling = createConnector("foo.com", 1234);
		testling.setTimeoutMilliseconds(10);
		resolver.addAddress("foo.com", host1);

		testling.start();
		testling.stop();

		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
	}
}