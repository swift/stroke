/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.network.ConnectionServer;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.Connection;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class SOCKS5BytestreamServer {

	private ConnectionServer connectionServer;
	private SOCKS5BytestreamRegistry registry;
	private Vector<SOCKS5BytestreamServerSession> sessions = new Vector<SOCKS5BytestreamServerSession>();
	private SignalConnection onNewConnectionConn;
	
	/**
	 * Map between {@link SOCKS5BytestreamServerSession} and the {@link SignalConnection} 
	 * to that session's {@link SOCKS5BytestreamServerSession#onFinished} signal.
	 */
	private Map<SOCKS5BytestreamServerSession, SignalConnection> sessionOnFinishedConnectionMap =
	        new HashMap<SOCKS5BytestreamServerSession, SignalConnection>();

	public SOCKS5BytestreamServer(ConnectionServer connectionServer, SOCKS5BytestreamRegistry registry) {
		this.connectionServer = connectionServer;
		this.registry = registry;
	}

	public HostAddressPort getAddressPort() {
		return connectionServer.getAddressPort();
	}

	public void start() {
		onNewConnectionConn = connectionServer.onNewConnection.connect(new Slot1<Connection>() {
			@Override
			public void call(Connection c) {
				handleNewConnection(c);
			}
		});
	}

	public void stop() {
		onNewConnectionConn.disconnect();
		for (SOCKS5BytestreamServerSession session : sessions) {
		    SignalConnection onFinishedConnection = sessionOnFinishedConnectionMap.remove(session);
		    if (onFinishedConnection != null) {
	            onFinishedConnection.disconnect();
	        }
			session.stop();
		}
		sessions.clear();
	}

	public Vector<SOCKS5BytestreamServerSession> getSessions(final String streamID) {
		Vector<SOCKS5BytestreamServerSession> result = new Vector<SOCKS5BytestreamServerSession>();
		for (SOCKS5BytestreamServerSession session : sessions) {
			if (session.getStreamID().equals(streamID)) {
				result.add(session);
			}
		}
		return result;
	}

	private void handleNewConnection(Connection connection) {
		final SOCKS5BytestreamServerSession session = new SOCKS5BytestreamServerSession(connection, registry);
		SignalConnection onFinishedConnection = session.onFinished.connect(new Slot1<FileTransferError>() {
			@Override
			public void call(FileTransferError e) {
				handleSessionFinished(session);
			}
		});
		sessions.add(session);
		sessionOnFinishedConnectionMap.put(session, onFinishedConnection);

		session.start();
	}

	private void handleSessionFinished(SOCKS5BytestreamServerSession session) {
		while(sessions.remove(session)) {
		    // Loop will run till session no longer is sessions
		}
		SignalConnection onFinishedConnection = sessionOnFinishedConnectionMap.remove(session);
		if (onFinishedConnection != null) {
		    onFinishedConnection.disconnect();
		}
	}
}