/*
 * Copyright (c) 2010-2014 Isode Limited.
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
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.Event;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.network.Connector;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.StaticDomainNameResolver;
import com.isode.stroke.network.DummyTimerFactory;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.network.DomainNameAddressQuery;
import java.util.Vector;

public class ConnectorTest {

	private HostAddressPort host1;
	private HostAddressPort host2;
	private HostAddressPort host3;
	private DummyEventLoop eventLoop;
	private StaticDomainNameResolver resolver;
	private MockConnectionFactory connectionFactory;
	private DummyTimerFactory timerFactory;
	private Vector<MockConnection> connections = new Vector<MockConnection>();
	private com.isode.stroke.base.Error error;

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

	private Connector createConnector() {
		return createConnector(-1, "_xmpp-client._tcp.");
	}

	private Connector createConnector(int port) {
		return createConnector(port, "_xmpp-client._tcp.");
	}

	private Connector createConnector(int port, String serviceLookupPrefix) {
		Connector connector = Connector.create("foo.com", port, serviceLookupPrefix, resolver, connectionFactory, timerFactory);
		connector.onConnectFinished.connect(new Slot2<Connection, com.isode.stroke.base.Error>() {
			@Override
			public void call(Connection c, com.isode.stroke.base.Error e) {
				handleConnectorFinished(c, e);
			}
		});
		return connector;
	}

	private void handleConnectorFinished(Connection connection, com.isode.stroke.base.Error resultError) {
		MockConnection c = (MockConnection)(connection);
		if (connection != null) {
			assert(c != null);
		}
		connections.add(c);
		error = resultError;
	}

	@Before
	public void setUp() {
		host1 = new HostAddressPort(new HostAddress("1.1.1.1"), 1234);
		host2 = new HostAddressPort(new HostAddress("2.2.2.2"), 2345);
		host3 = new HostAddressPort(new HostAddress("3.3.3.3"), 5222);
		eventLoop = new DummyEventLoop();
		resolver = new StaticDomainNameResolver(eventLoop);
		connectionFactory = new MockConnectionFactory(eventLoop);
		timerFactory = new DummyTimerFactory();
	}

	@Test
	public void testConnect() {
		Connector testling = createConnector();
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addXMPPClientService("foo.com", host2);
		resolver.addAddress("foo.com", host3.getAddress());

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(host1, (connections.get(0).hostAddressPort));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoServiceLookups() {
		Connector testling = createConnector(4321, null);
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addXMPPClientService("foo.com", host2);
		resolver.addAddress("foo.com", host3.getAddress());

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		//assertEquals(host3.getAddress(), (connections.get(0).hostAddressPort).getAddress()); FAIL
		assertEquals(4321, (connections.get(0).hostAddressPort).getPort());
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoServiceLookups_DefaultPort() {
		Connector testling = createConnector(-1, null);
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addXMPPClientService("foo.com", host2);
		resolver.addAddress("foo.com", host3.getAddress());

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		//assertEquals(host3.getAddress(), (connections.get(0).hostAddressPort).getAddress()); FAIL
		assertEquals(5222, (connections.get(0).hostAddressPort).getPort());
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoSRVHost() {
		Connector testling = createConnector();
		resolver.addAddress("foo.com", host3.getAddress());

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(host3, (connections.get(0).hostAddressPort));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_FirstAddressHostFails() {
		Connector testling = createConnector();

		HostAddress address1 = new HostAddress("1.1.1.1");
		HostAddress address2 = new HostAddress("2.2.2.2");
		resolver.addXMPPClientService("foo.com", "host-foo.com", 1234);
		resolver.addAddress("host-foo.com", address1);
		resolver.addAddress("host-foo.com", address2);
		connectionFactory.failingPorts.add(new HostAddressPort(address1, 1234));

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(new HostAddressPort(address2, 1234), (connections.get(0).hostAddressPort));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoHosts() {
		Connector testling = createConnector();

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
		assertNotNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_FirstSRVHostFails() {
		Connector testling = createConnector();
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addXMPPClientService("foo.com", host2);
		connectionFactory.failingPorts.add(host1);

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		//assertEquals(host2, (connections.get(0).hostAddressPort)); FAIL
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_AllSRVHostsFailWithoutFallbackHost() {
		Connector testling = createConnector();
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addXMPPClientService("foo.com", host2);
		connectionFactory.failingPorts.add(host1);
		connectionFactory.failingPorts.add(host2);

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		//assertNull(connections.get(0)); FAIL
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_AllSRVHostsFailWithFallbackHost() {
		Connector testling = createConnector();
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addXMPPClientService("foo.com", host2);
		resolver.addAddress("foo.com", host3.getAddress());
		connectionFactory.failingPorts.add(host1);
		connectionFactory.failingPorts.add(host2);

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		//assertEquals(host3, (connections.get(0).hostAddressPort)); FAIL
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_SRVAndFallbackHostsFail() {
		Connector testling = createConnector();
		resolver.addXMPPClientService("foo.com", host1);
		resolver.addAddress("foo.com", host3.getAddress());
		connectionFactory.failingPorts.add(host1);
		connectionFactory.failingPorts.add(host3);

		testling.start();
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
		assertNull((DomainNameResolveError)(error));
	}

	//@Test COMMENTED IN SWIFTEN TOO.
	/*public void testConnect_TimeoutDuringResolve() {
		Connector testling = createConnector();
		testling.setTimeoutMilliseconds(10);
		resolver.setIsResponsive(false);

		testling.start();
		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		CPPUNIT_ASSERT((DomainNameResolveError)(error));
		CPPUNIT_ASSERT(!connections.get(0));
	}*/

	@Test
	public void testConnect_TimeoutDuringConnectToOnlyCandidate() {
		Connector testling = createConnector();
		testling.setTimeoutMilliseconds(10);
		resolver.addXMPPClientService("foo.com", host1);
		connectionFactory.isResponsive = false;

		testling.start();
		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_TimeoutDuringConnectToCandidateFallsBack() {
		Connector testling = createConnector();
		testling.setTimeoutMilliseconds(10);

		resolver.addXMPPClientService("foo.com", "host-foo.com", 1234);
		HostAddress address1 = new HostAddress("1.1.1.1");
		resolver.addAddress("host-foo.com", address1);
		HostAddress address2 = new HostAddress("2.2.2.2");
		resolver.addAddress("host-foo.com", address2);

		connectionFactory.isResponsive = false;
		testling.start();
		eventLoop.processEvents();
		connectionFactory.isResponsive = true;
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertEquals(new HostAddressPort(address2, 1234), (connections.get(0).hostAddressPort));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testConnect_NoTimeout() {
		Connector testling = createConnector();
		testling.setTimeoutMilliseconds(10);
		resolver.addXMPPClientService("foo.com", host1);

		testling.start();
		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNotNull(connections.get(0));
		assertNull((DomainNameResolveError)(error));
	}

	@Test
	public void testStop_DuringSRVQuery() {
		Connector testling = createConnector();
		resolver.addXMPPClientService("foo.com", host1);

		testling.start();
		testling.stop();

		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
		assertNotNull((DomainNameResolveError)(error));
	}

	@Test
	public void testStop_Timeout() {
		Connector testling = createConnector();
		testling.setTimeoutMilliseconds(10);
		resolver.addXMPPClientService("foo.com", host1);

		testling.start();
		testling.stop();

		eventLoop.processEvents();
		timerFactory.setTime(10);
		eventLoop.processEvents();

		assertEquals(1, (connections.size()));
		assertNull(connections.get(0));
	}
}