/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 - 2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.Timer;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.stringcodecs.Hexify;
import java.util.logging.Logger;

/**
 * A session which has been connected to a SOCKS5 server (requester).
 *
 */
public class SOCKS5BytestreamClientSession extends SOCKS5AbstractBytestreamSession {

	public enum State {
		Initial(0),
		Hello(1),
		Authenticating(2),
		Ready(3),
		Writing(4),
		Reading(5),
		Finished(6);
		private State(int x) {
			description = x;
		}
		public final int description;
	};

	private Connection connection;
	private HostAddressPort addressPort;
	private String destination; // hexify(SHA1(sessionID + requester + target))

	private State state;

	private ByteArray unprocessedData;
	private ByteArray authenticateAddress;

	private int chunkSize;
	private WriteBytestream writeBytestream;
	private ReadBytestream readBytestream;

	private Timer weFailedTimeout;

	private SignalConnection connectFinishedConnection;
	private SignalConnection dataWrittenConnection;
	private SignalConnection dataReadConnection;
	private SignalConnection disconnectedConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public SOCKS5BytestreamClientSession(Connection connection, final HostAddressPort addressPort, final String destination, TimerFactory timerFactory) {
		this.connection = connection;
		this.addressPort = addressPort;
		this.destination = destination;
		this.state = State.Initial;
		this.chunkSize = 131072;
		weFailedTimeout = timerFactory.createTimer(3000);
		weFailedTimeout.onTick.connect(new Slot() {
			@Override
			public void call() {
				handleWeFailedTimeout();
			}
		});
	}

	public void start() {
		assert(state == State.Initial);
		logger_.fine("Trying to connect via TCP to " + addressPort.toString() + ".\n");
		weFailedTimeout.start();
		connectFinishedConnection = connection.onConnectFinished.connect(new Slot1<Boolean>() {
			@Override
			public void call(Boolean b) {
				handleConnectFinished(b);
			}
		});
		connection.connect(addressPort);
	}

	public void stop() {
		logger_.fine("\n");
		if (state.description < State.Ready.description) {
			weFailedTimeout.stop();
		}
		if (state == State.Finished) {
			return;
		}
		closeConnection();
		readBytestream = null;
		state = State.Finished;		
	}

	public void startReceiving(WriteBytestream writeStream) {
		if (state == State.Ready) {
			state = State.Reading;
			writeBytestream = writeStream;
			writeBytestream.write(unprocessedData);
			//onBytesReceived(unprocessedData.size());
			unprocessedData.clear();
		} else {
			logger_.fine("Session isn't ready for transfer yet!\n");
		}
	}

	public void startSending(ReadBytestream readStream) {
		if (state == State.Ready) {
			state = State.Writing;
			readBytestream = readStream;
			dataWrittenConnection = connection.onDataWritten.connect(new Slot() {
				@Override
				public void call() {
					sendData();
				}
			});
			sendData();
		} else {
			logger_.fine("Session isn't ready for transfer yet!\n");
		}
	}

	public HostAddressPort getAddressPort() {
		return addressPort;
	}

	public Signal1<Boolean /*error*/> onSessionReady = new Signal1<Boolean>();

	private void process() {
		logger_.fine("unprocessedData.size(): " + unprocessedData.getSize() + "\n");
		ByteArray bndAddress = new ByteArray();
		switch(state) {
			case Initial:
				hello();
				break;
			case Hello:
				if (unprocessedData.getSize() > 1) {
					char version = (char)unprocessedData.getData()[0];
					char authMethod = (char)unprocessedData.getData()[1];
					if (version != 5 || authMethod != 0) {
						// signal failure to upper level
						finish(true);
						return;
					}
					unprocessedData.clear();
					authenticate();
				}
				break;
			case Authenticating:
				if (unprocessedData.getSize() < 5) {
					// need more data to start progressing
					break;
				}
				if (unprocessedData.getData()[0] != (byte)0x05) {
					// wrong version
					// disconnect & signal failure
					finish(true);
					break;
				}
				if (unprocessedData.getData()[1] != (byte)0x00) {
					// no success
					// disconnect & signal failure
					finish(true);
					break;
				}
				if (unprocessedData.getData()[3] != (byte)0x03) {
					// we expect x'03' = DOMAINNAME here
					// disconnect & signal failure
					finish(true);
					break;
				}
				if (((int)unprocessedData.getData()[4]) + 1 > unprocessedData.getSize() + 5) {
					// complete domainname and port not available yet
					break;
				}
				//-----bndAddress = new ByteArray(&vecptr(unprocessedData)[5], unprocessedData[4]);
				if (unprocessedData.getData()[unprocessedData.getData()[4] + 5] != 0 && new ByteArray(destination).equals(bndAddress)) {
					// we expect a 0 as port
					// disconnect and fail
					finish(true);
				}
				unprocessedData.clear();
				state = State.Ready;
				logger_.fine("session ready\n");
				// issue ready signal so the bytestream can be used for reading or writing
				weFailedTimeout.stop();
				onSessionReady.emit(false);
				break;
			case Ready:
				logger_.fine("Received further data in Ready state.\n");
				break;
			case Reading:
			case Writing:
			case Finished:
				logger_.fine("Unexpected receive of data. Current state: " + state + "\n");
				logger_.fine("Data: " + Hexify.hexify(unprocessedData) + "\n");
				unprocessedData.clear();
				//assert(false);
		}
	}

	private void hello() {
		// Version 5, 1 auth method, No authentication
		final SafeByteArray hello = new SafeByteArray(new byte[]{0x05, 0x01, 0x00});
		connection.write(hello);
		state = State.Hello;
	}

	private void authenticate() {
		logger_.fine("\n");
		SafeByteArray header = new SafeByteArray(new byte[]{0x05, 0x01, 0x00, 0x03});
		SafeByteArray message = header;
		String destinationlength = Integer.toString(destination.length());
		message.append(new SafeByteArray(destinationlength));
		authenticateAddress = new ByteArray(destination);
		message.append(authenticateAddress);
		message.append(new SafeByteArray(new byte[]{0x00, 0x00})); // 2 byte for port
		connection.write(message);
		state = State.Authenticating;		
	}

	private void handleConnectFinished(boolean error) {
		connectFinishedConnection.disconnect();
		if (error) {
			logger_.fine("Failed to connect via TCP to " + addressPort.toString() + "." + "\n");
			finish(true);
		} else {
			logger_.fine("Successfully connected via TCP" + addressPort.toString() + "." + "\n");
			disconnectedConnection = connection.onDisconnected.connect(new Slot1<Connection.Error>() {
				@Override
				public void call(Connection.Error e) {
					handleDisconnected(e);
				}
			});
			dataReadConnection = connection.onDataRead.connect(new Slot1<SafeByteArray>() {
				@Override
				public void call(SafeByteArray b) {
					handleDataRead(b);
				}
			});
			weFailedTimeout.stop();
			weFailedTimeout.start();
			process();
		}
	}

	private void handleDataRead(SafeByteArray data) {
		logger_.fine("state: " + state + " data.size() = " + data.getSize() + "\n");
		if (state != State.Reading) {
			unprocessedData.append(data);
			process();
		}
		else {
		    writeBytestream.write(data);
			//onBytesReceived(data.size());
		}
	}

	private void handleDisconnected(final Connection.Error error) {
		logger_.fine((error != null ? (error == Connection.Error.ReadError ? "Read Error" : "Write Error") : "No Error") + "\n");
		if (error != null) {
			finish(true);
		}
	}

	private void handleWeFailedTimeout() {
		logger_.fine("Failed due to timeout!\n");
		finish(true);
	}

	private void finish(boolean error) {
		logger_.fine("\n");
		if (state.description < State.Ready.description) {
			weFailedTimeout.stop(); }
		closeConnection();
		readBytestream = null;
		if (State.Initial.equals(state) || State.Hello.equals(state) || State.Authenticating.equals(state)) {
			onSessionReady.emit(true);
		}
		else {
			state = State.Finished;
			if (error) {
				onFinished.emit(new FileTransferError(FileTransferError.Type.ReadError));
			} else {
				onFinished.emit(null);
			}
		}
	}

	private void sendData() {
		if (!readBytestream.isFinished()) {
			//try {
				ByteArray dataToSend = readBytestream.read((int)(chunkSize));
				connection.write(new SafeByteArray(dataToSend));
				onBytesSent.emit(dataToSend.getSize());
			//}
			//catch (BytestreamException e) {
			//	finish(true);
			//}
		}
		else {
			finish(false);
		}
	}

	private void closeConnection() {
		connectFinishedConnection.disconnect();
		dataWrittenConnection.disconnect();
		dataReadConnection.disconnect();
		disconnectedConnection.disconnect();
		connection.disconnect();
	}
}