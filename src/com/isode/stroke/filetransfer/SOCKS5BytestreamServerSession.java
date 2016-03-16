/*
 * Copyright (c) 2010-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.network.Connection;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.StartStoppable;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;

import java.util.logging.Logger;

public class SOCKS5BytestreamServerSession  extends SOCKS5AbstractBytestreamSession implements StartStoppable  {

	private Connection connection;
	private SOCKS5BytestreamRegistry bytestreams;
	private ByteArray unprocessedData = new ByteArray();
	private State state;
	private int chunkSize;
	private String streamID = "";
	private ReadBytestream readBytestream;
	private WriteBytestream writeBytestream;
	private boolean waitingForData;

	private SignalConnection disconnectedConnection;
	private SignalConnection dataReadConnection;
	private SignalConnection dataWrittenConnection;
	private SignalConnection dataAvailableConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public enum State {
		Initial,
		WaitingForAuthentication,
		WaitingForRequest,
		ReadyForTransfer,
		ReadingData,
		WritingData,
		Finished
	};

	public SOCKS5BytestreamServerSession(Connection connection, SOCKS5BytestreamRegistry bytestreams) {
		this.connection = connection;
		this.bytestreams = bytestreams;
		this.state = State.Initial;
		this.chunkSize = 131072;
		this.waitingForData = false;
		disconnectedConnection = connection.onDisconnected.connect(new Slot1<Connection.Error>() {
			@Override
			public void call(Connection.Error e) {
				handleDisconnected(e);
			}
		});
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public void start() {
		logger_.fine("\n");
		dataReadConnection = connection.onDataRead.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray s) {
				handleDataRead(s);
			}
		});
		state = State.WaitingForAuthentication;
	}

	public void stop() {
		finish();
	}

	public void startSending(ReadBytestream stream) {
		if (!State.ReadyForTransfer.equals(state)) { logger_.fine("Not ready for transfer!\n"); return; }
		readBytestream = stream;
		state = State.WritingData;
		dataAvailableConnection = readBytestream.onDataAvailable.connect(new Slot() {
			@Override
			public void call() {
				handleDataAvailable();
			}
		});
		dataWrittenConnection = connection.onDataWritten.connect(new Slot() {
			@Override
			public void call() {
				sendData();
			}
		});
		sendData();
	}

	public void startReceiving(WriteBytestream stream) {
		if (!State.ReadyForTransfer.equals(state)) { logger_.fine("Not ready for transfer!\n"); return; }

		writeBytestream = stream;
		state = State.ReadingData;
		writeBytestream.write(unprocessedData);
		// onBytesReceived(unprocessedData.getSize());
		unprocessedData.clear();
	}

	public HostAddressPort getAddressPort() {
		return connection.getLocalAddress();
	}

	

	public String getStreamID() {
		return streamID;
	}

	private void finish() {
	    finish(null);
	}
	
	private void finish(FileTransferError error) {
		logger_.fine("state: " + state + "\n");
		if (State.Finished.equals(state)) {
			return;
		}

		disconnectedConnection.disconnect();
		dataReadConnection.disconnect();
		if (dataWrittenConnection != null) {
		    dataWrittenConnection.disconnect();
		}
		if (dataAvailableConnection != null) {
		    dataAvailableConnection.disconnect();
		}
		readBytestream = null;
		state = State.Finished;
		onFinished.emit(error);
	}

	private void process() {
		if (State.WaitingForAuthentication.equals(state)) {
			if (unprocessedData.getSize() >= 2) {
				int authCount = unprocessedData.getData()[1];
				int i = 2;
				while (i < 2 + authCount && i < unprocessedData.getSize()) {
					// Skip authentication mechanism
					++i;
				}
				if (i == 2 + authCount) {
					// Authentication message is complete
					if (i != unprocessedData.getSize()) {
						logger_.fine("Junk after authentication mechanism\n");
					}
					unprocessedData.clear();
					connection.write(new SafeByteArray(new byte[]{0x05, 0x00}));
					state = State.WaitingForRequest;
				}
			}
		}
		else if (State.WaitingForRequest.equals(state)) {
			if (unprocessedData.getSize() >= 5) {
				ByteArray requestID = new ByteArray();
				int i = 5;
				int hostnameSize = unprocessedData.getData()[4];
				while (i < 5 + hostnameSize && i < unprocessedData.getSize()) {
					requestID.append(unprocessedData.getData()[i]);
					++i;
				}
				// Skip the port: 2 byte large, one already skipped. Add one for comparison with size
				i += 2;
				if (i <= unprocessedData.getSize()) {
					if (i != unprocessedData.getSize()) {
						logger_.fine("Junk after authentication mechanism\n");
					}
					unprocessedData.clear();
					streamID = requestID.toString();
					boolean hasBytestream = bytestreams.hasBytestream(streamID);
					SafeByteArray result = new SafeByteArray((byte)0x05);
					result.append(hasBytestream ? (byte)0x0 : (byte)0x4);
					result.append(new ByteArray(new byte[]{0x00, 0x03}));
					result.append((byte)requestID.getSize());
					result.append(requestID.append(new ByteArray(new byte[]{0x00, 0x00})));
					if (!hasBytestream) {
						logger_.fine("Readstream or Wrtiestream with ID " + streamID + " not found!\n");
						connection.write(result);
						finish(new FileTransferError(FileTransferError.Type.PeerError));
					}
					else {
						logger_.fine("Found stream. Sent OK.\n");
						connection.write(result);
						state = State.ReadyForTransfer;
					}
				}
			}
		}
	}

	private void handleDataRead(SafeByteArray data) {
		if (!State.ReadingData.equals(state)) {
			unprocessedData.append(data);
			process();
		} else {
			if (!writeBytestream.write(new ByteArray(data))) {
			    finish(new FileTransferError(FileTransferError.Type.WriteError));
			}
		}
	}

	private void handleDisconnected(final Connection.Error error) {
		logger_.fine((error != null ? (error.equals(Connection.Error.ReadError) ? "Read Error" : "Write Error") : "No Error") + "\n");
		finish(error != null ? new FileTransferError(FileTransferError.Type.PeerError) : null);
	}

	private void handleDataAvailable() {
		if (waitingForData) {
			sendData();
		}
	}

	private void sendData() {
		if (!readBytestream.isFinished()) {
			//try {
				SafeByteArray dataToSend = new SafeByteArray(readBytestream.read((chunkSize)));
				if (!dataToSend.isEmpty()) {
					connection.write(dataToSend);
					onBytesSent.emit(dataToSend.getSize());
					waitingForData = false;
				}
				else {
					waitingForData = true;
				}
			//}
			//catch (BytestreamException e) {
			//	finish(true);
			//}
		}
		else {
			finish();
		}
	}
}