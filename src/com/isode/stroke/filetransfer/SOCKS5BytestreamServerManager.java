/*
 * Copyright (c) 2012-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.network.ConnectionServer;
import com.isode.stroke.network.ConnectionServerFactory;
import com.isode.stroke.network.NATTraverser;
import com.isode.stroke.network.NetworkInterface;
import com.isode.stroke.network.NATPortMapping;
import com.isode.stroke.network.NetworkEnvironment;
import com.isode.stroke.network.NATTraversalGetPublicIPRequest;
import com.isode.stroke.network.NATTraversalForwardPortRequest;
import com.isode.stroke.network.NATTraversalRemovePortForwardingRequest;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import java.util.Vector;
import java.util.logging.Logger;

public class SOCKS5BytestreamServerManager {

	public static final int LISTEN_PORTS_BEGIN = 10000;
	public static final int LISTEN_PORTS_END = 11000;

	private SOCKS5BytestreamRegistry bytestreamRegistry;
	private ConnectionServerFactory connectionServerFactory;
	private NetworkEnvironment networkEnvironment;
	private NATTraverser natTraverser;
	private enum State { Start, Initializing, Initialized };
	private State state; 
	private SOCKS5BytestreamServer server;
	private ConnectionServer connectionServer;
	private int connectionServerPort;

	private NATTraversalGetPublicIPRequest getPublicIPRequest;
	private NATTraversalForwardPortRequest forwardPortRequest;
	private NATTraversalRemovePortForwardingRequest unforwardPortRequest;
	private HostAddress publicAddress;
	private NATPortMapping portMapping;
	private boolean attemptedPortMapping_;

	private SOCKS5BytestreamServerResourceUser s5bServerResourceUser_;
	private SOCKS5BytestreamServerPortForwardingUser s5bServerPortForwardingUser_;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public SOCKS5BytestreamServerManager(SOCKS5BytestreamRegistry bytestreamRegistry, ConnectionServerFactory connectionServerFactory, NetworkEnvironment networkEnvironment, NATTraverser natTraverser) {
		this.bytestreamRegistry = bytestreamRegistry;
		this.connectionServerFactory = connectionServerFactory;
		this.networkEnvironment = networkEnvironment;
		this.natTraverser = natTraverser;
		this.state = State.Start;
		this.server = null;
		this.attemptedPortMapping_ = false;
	}

	/**
	* User should call delete to free the resources.
	*/
	public void delete() {
		//SWIFT_LOG_ASSERT(!connectionServer, warning) << std::endl;
		//SWIFT_LOG_ASSERT(!getPublicIPRequest, warning) << std::endl;
		//SWIFT_LOG_ASSERT(!forwardPortRequest, warning) << std::endl;
		//SWIFT_LOG_ASSERT(state == Start, warning) << std::endl;
		if (portMapping != null && unforwardPortRequest == null) {
			//SWIFT_LOG(warning) << "Port forwarding still alive. Trying to remove it now." << std::endl;
			unforwardPortRequest = natTraverser.createRemovePortForwardingRequest(portMapping.getLocalPort(), portMapping.getPublicPort());
			unforwardPortRequest.start();
		}
	}

	protected void finalize() throws Throwable {
		try {
			delete();
		}
		finally {
			super.finalize();
		}
	}

	public SOCKS5BytestreamServerResourceUser aquireResourceUser() {
		SOCKS5BytestreamServerResourceUser resourceUser = null;
		if (s5bServerResourceUser_ == null) {
			resourceUser = new SOCKS5BytestreamServerResourceUser(this);
			s5bServerResourceUser_ = resourceUser;
		}
		else {
			resourceUser = s5bServerResourceUser_;
		}
		return resourceUser;
	}

	public SOCKS5BytestreamServerPortForwardingUser aquirePortForwardingUser() {
		SOCKS5BytestreamServerPortForwardingUser portForwardingUser = null;
		if (s5bServerPortForwardingUser_ == null) {
			portForwardingUser = new SOCKS5BytestreamServerPortForwardingUser(this);
			s5bServerPortForwardingUser_ = portForwardingUser;
		}
		else {
			portForwardingUser = s5bServerPortForwardingUser_;
		}
		return portForwardingUser;
	}

	public void stop() {
		if (getPublicIPRequest != null) {
			getPublicIPRequest.stop();
			getPublicIPRequest = null;
		}
		if (forwardPortRequest != null) {
			forwardPortRequest.stop();
			forwardPortRequest = null;
		}
		if (unforwardPortRequest != null) {
		    unforwardPortRequest.stop();
		    unforwardPortRequest = null;
		}
		if (server != null) {
			server.stop();
			server = null;
		}
		if (connectionServer != null) {
			connectionServer.stop();
			connectionServer = null;
		}

		state = State.Start;
	}
			
	public Vector<HostAddressPort> getHostAddressPorts() {
		Vector<HostAddressPort> result = new Vector<HostAddressPort>();
		if (connectionServer != null) {
			Vector<NetworkInterface> networkInterfaces = networkEnvironment.getNetworkInterfaces();
			for (final NetworkInterface networkInterface : networkInterfaces) {
				for (final HostAddress address : networkInterface.getAddresses()) {
					result.add(new HostAddressPort(address, connectionServerPort));
				}
			}
		}
		return result;
	}

	public Vector<HostAddressPort> getAssistedHostAddressPorts() {
		Vector<HostAddressPort> result = new Vector<HostAddressPort>();
		if (publicAddress != null && portMapping != null) {
			result.add(new HostAddressPort(publicAddress, portMapping.getPublicPort()));
		}
		return result;
	}

	public SOCKS5BytestreamServer getServer()  {
		return server;
	}

	boolean isInitialized() {
		return State.Initialized.equals(state);
	}

	void initialize() {
		if (State.Start.equals(state)) {
			state = State.Initializing;

			// Find a port to listen on
			assert(connectionServer == null);
			int port;
			for (port = LISTEN_PORTS_BEGIN; port < LISTEN_PORTS_END; ++port) {
				logger_.fine("Trying to start server on port " + port + "\n");
				connectionServer = connectionServerFactory.createConnectionServer(new HostAddress("::"), port);
				ConnectionServer.Error error = connectionServer.tryStart();
				if (error == null) {
					break;
				}
				else if (!ConnectionServer.Error.Conflict.equals(error)) {
					logger_.fine("Error starting server\n");
					onInitialized.emit(false);
					return;
				}
				connectionServer = null;
			}
			if (connectionServer == null) {
				logger_.fine("Unable to find an open port\n");
				onInitialized.emit(false);
				return;
			}
			logger_.fine("Server started succesfully\n");
			connectionServerPort = port;

			// Start bytestream server. Should actually happen before the connectionserver is started
			// but that doesn't really matter here.
			assert(server == null);
			server = new SOCKS5BytestreamServer(connectionServer, bytestreamRegistry);
			server.start();
			checkInitializeFinished();
		}
	}

	boolean isPortForwardingReady() {
		return attemptedPortMapping_ && getPublicIPRequest == null && forwardPortRequest == null;
	}

	void setupPortForwarding() {
		assert(server != null);
		attemptedPortMapping_ = true;

		// Retrieve public addresses
		assert(getPublicIPRequest == null);
		publicAddress = null;
		if ((natTraverser.createGetPublicIPRequest() != null)) {
			getPublicIPRequest = natTraverser.createGetPublicIPRequest();
			getPublicIPRequest.onResult.connect(new Slot1<HostAddress>() {
				@Override
				public void call(HostAddress a) {
					handleGetPublicIPResult(a);
				}
			});
			getPublicIPRequest.start();
		}

		// Forward ports
		int port = server.getAddressPort().getPort();
		assert(forwardPortRequest == null);
		portMapping = null;
		if ((natTraverser.createForwardPortRequest(port, port) != null)) {
			forwardPortRequest = natTraverser.createForwardPortRequest(port, port);
			forwardPortRequest.onResult.connect(new Slot1<NATPortMapping>() {
				@Override
				public void call(NATPortMapping n) {
					handleForwardPortResult(n);
				}
			});
			forwardPortRequest.start();
		}
	}

	void removePortForwarding() {
		// remove port forwards
		if (portMapping != null) {
			unforwardPortRequest = natTraverser.createRemovePortForwardingRequest(portMapping.getLocalPort(), portMapping.getPublicPort());
			unforwardPortRequest.onResult.connect(new Slot1<Boolean>() {
				@Override
				public void call(Boolean b) {
					handleUnforwardPortResult(b);
				}
			});
			unforwardPortRequest.start();
		}
	}

	void checkInitializeFinished() {
		assert(State.Initializing.equals(state));
		state = State.Initialized;
		onInitialized.emit(true);
	}

	void handleGetPublicIPResult(HostAddress address) {
		if (address != null) {
			logger_.fine("Public IP discovered as " + address.toString() + ".\n");
		} 
		else {
			logger_.fine("No public IP discoverable.\n");
		}

		publicAddress = address;

		getPublicIPRequest.stop();
		getPublicIPRequest = null;
	}

	void handleForwardPortResult(NATPortMapping mapping) {
		if (mapping != null) {
			logger_.fine("Mapping port was successful.\n");
		} 
		else {
			logger_.fine("Mapping port has failed.\n");
		}

		portMapping = mapping;
		onPortForwardingSetup.emit(mapping != null);

		forwardPortRequest.stop();
		forwardPortRequest = null;
	}

	void handleUnforwardPortResult(Boolean result) {
		if (result != null && result) {
			portMapping = null;
		}
		else {
			logger_.warning("Failed to remove port forwarding.\n");
		}
		attemptedPortMapping_ = false;
		unforwardPortRequest = null;
	}

	Signal1<Boolean/* success */> onInitialized = new Signal1<Boolean>();
	Signal1<Boolean/* success */> onPortForwardingSetup = new Signal1<Boolean>();
}