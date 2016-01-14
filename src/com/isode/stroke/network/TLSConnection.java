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

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.tls.TLSOptions;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.tls.TLSContext;
import com.isode.stroke.tls.TLSError;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.SignalConnection;

public class TLSConnection extends Connection {

	private TLSContext context;
	private Connection connection;
	private SignalConnection onConnectFinishedConnection;
	private SignalConnection onDataReadConnection;
	private SignalConnection onDataWrittenConnection;
	private SignalConnection onDisconnectedConnection;

	public TLSConnection(Connection connection, TLSContextFactory tlsFactory, final TLSOptions tlsOptions) {
		this.connection = connection;
		context = tlsFactory.createTLSContext(tlsOptions);
		context.onDataForNetwork.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray s) {
				handleTLSDataForNetwork(s);
			}
		});
		context.onDataForApplication.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray s) {
				handleTLSDataForApplication(s);
			}
		});
		context.onConnected.connect(new Slot() {
			@Override
			public void call() {
				handleTLSConnectFinished(false);
			}
		});
		context.onError.connect(new Slot1<TLSError>() {
			@Override
			public void call(TLSError e) {
				handleTLSConnectFinished(true);
			}
		});
		connection.onConnectFinished.connect(new Slot1<Boolean>() {
			@Override
			public void call(Boolean s) {
				handleRawConnectFinished(s);
			}
		});
		connection.onDataRead.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray s) {
				handleRawDataRead(s);
			}
		});
		connection.onDataWritten.connect(new Slot() {
			@Override
			public void call() {
				handleRawDataWritten();
			}
		});
		connection.onDisconnected.connect(new Slot1<Error>() {
			@Override
			public void call(Error e) {
				handleRawDisconnected(e);
			}
		});
	}

	protected void finalize() throws Throwable {
		try {
		    if (onConnectFinished != null) {
	            onConnectFinishedConnection.disconnect();
	        }
		    if (onDataReadConnection != null) {
		        onDataReadConnection.disconnect();
		    }
		    if (onDataWrittenConnection != null) {
		        onDataWrittenConnection.disconnect();
		    }
			if (onDisconnectedConnection != null) {
			    onDisconnectedConnection.disconnect();
			}
		}
		finally {
			super.finalize();
		}
	}

	public void listen() {
		assert(false);
	}

	public void connect(final HostAddressPort address) {
		connection.connect(address);
	}

	public void disconnect() {
		connection.disconnect();
	}

	public void write(final SafeByteArray data) {
		context.handleDataFromApplication(data);
	}

	public HostAddressPort getLocalAddress() {
		return connection.getLocalAddress();
	}

	private void handleRawConnectFinished(boolean error) {
	    if (onConnectFinished != null) {
	        onConnectFinishedConnection.disconnect();
	    }
		if (error) {
			onConnectFinished.emit(true);
		}
		else {
			context.connect();
		}
	}

	private void handleRawDisconnected(final Error error) {
		onDisconnected.emit(error);
	}

	private void handleRawDataRead(SafeByteArray data) {
		context.handleDataFromNetwork(data);
	}

	private void handleRawDataWritten() {
		onDataWritten.emit();
	}

	private void handleTLSConnectFinished(boolean error) {
		onConnectFinished.emit(error);
		if (error) {
			disconnect();
		}
	}

	private void handleTLSDataForNetwork(final SafeByteArray data) {
		connection.write(data);
	}

	private void handleTLSDataForApplication(final SafeByteArray data) {
		onDataRead.emit(data);
	}
}