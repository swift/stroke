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

import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.IBB;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.base.ByteArray;

public class IBBSendSession {

	private String id = "";
	private JID from = new JID();
	private JID to = new JID();
	private ReadBytestream bytestream;
	private IQRouter router;
	private int blockSize;
	private int sequenceNumber;
	private boolean active;
	private boolean waitingForData;
	private SignalConnection currentRequestOnResponseConnection;

    public IBBSendSession(final String id, final JID from, final JID to, ReadBytestream bytestream, IQRouter router) {
		this.id = id; 
		this.from = from; 
		this.to = to; 
		this.bytestream = bytestream;
		this.router = router; 
		this.blockSize = 4096; 
		this.sequenceNumber = 0; 
		this.active = false; 
		this.waitingForData = false;
		bytestream.onDataAvailable.connect(new Slot() {
			@Override
			public void call() {
				handleDataAvailable();
			}
		});
	}

	public void start() {
		IBBRequest request = IBBRequest.create(from, to, IBB.createIBBOpen(id, (int)(blockSize)), router);
		currentRequestOnResponseConnection = request.onResponse.connect(new Slot2<IBB, ErrorPayload>() {
			@Override
			public void call(IBB b, ErrorPayload e) {
				handleIBBResponse(b, e);
			}
		});
		active = true;
		request.send();
	}

	public void stop() {
		if (active && router.isAvailable()) {
			IBBRequest.create(from, to, IBB.createIBBClose(id), router).send();
		}
		if (currentRequestOnResponseConnection != null) {
		    currentRequestOnResponseConnection.disconnect();
		    currentRequestOnResponseConnection = null;
		}
		finish(null);
	}

	public JID getSender() {
		return from;
	}

	public JID getReceiver() {
		return to;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public final Signal1<FileTransferError> onFinished = new Signal1<FileTransferError>();
	public final Signal1<Integer> onBytesSent = new Signal1<Integer>();
    private void handleIBBResponse(IBB ibb, ErrorPayload error) {
        if (currentRequestOnResponseConnection != null) {
            currentRequestOnResponseConnection.disconnect();
            currentRequestOnResponseConnection = null;
        }
		if (error == null && active) {
			if (!bytestream.isFinished()) {
				sendMoreData();
			}
			else {
				finish(null);
			}
		}
		else {
			finish(new FileTransferError(FileTransferError.Type.PeerError));
		}		
	}
	private void finish(FileTransferError error) {
		active = false;
		onFinished.emit(error);
	}

	private void sendMoreData() {
		//try {
			ByteArray data = bytestream.read(blockSize);
			if (!data.isEmpty()) {
				waitingForData = false;
				IBBRequest request = IBBRequest.create(from, to, IBB.createIBBData(id, sequenceNumber, data), router);
				sequenceNumber++;
				request.onResponse.connect(new Slot2<IBB, ErrorPayload>() {
					@Override
					public void call(IBB b, ErrorPayload e) {
						handleIBBResponse(b, e);
					}
				});
				request.send();
				onBytesSent.emit(data.getSize());
			}
			else {
				waitingForData = true;
			}
		//}
		//catch (BytestreamException e) {
		//	finish(new FileTransferError(FileTransferError.Type.ReadError));
		//}		
	}

	private void handleDataAvailable() {
		if (waitingForData) {
			sendMoreData();
		}
	}
}