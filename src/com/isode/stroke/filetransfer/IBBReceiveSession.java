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

import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.SetResponder;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IBB;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal1;
import java.util.logging.Logger;

public class IBBReceiveSession {

	class IBBResponder extends SetResponder<IBB> {

		public IBBResponder(IBBReceiveSession session, IQRouter router) {
			super(new IBB(), router);
			this.session = session;
			this.sequenceNumber = 0;
			this.receivedSize = 0;
			setFinal(false);
		}

		public boolean handleSetRequest(final JID from, final JID to, final String id, IBB ibb) {
			if (from.equals(session.from) && ibb.getStreamID().equals(session.id)) {
				if (IBB.Action.Data.equals(ibb.getAction())) {
					if (sequenceNumber == ibb.getSequenceNumber()) {
						session.bytestream.write(ibb.getData());
						receivedSize += ibb.getData().getSize();
						sequenceNumber++;
						sendResponse(from, id, null);
						if (receivedSize >= session.size) {
							if (receivedSize > session.size) {
								logger_.warning("Received more data than expected");
							}
							session.finish(null);
						}
					}
					else {
						logger_.warning("Received data out of order");
						sendError(from, id, ErrorPayload.Condition.NotAcceptable, ErrorPayload.Type.Cancel);
						session.finish(new FileTransferError(FileTransferError.Type.ClosedError));
					}
				}
				else if (IBB.Action.Open.equals(ibb.getAction())) {
					logger_.fine("IBB open received");
					sendResponse(from, id, null);
				}
				else if (IBB.Action.Close.equals(ibb.getAction())) {
					logger_.fine("IBB close received");
					sendResponse(from, id, null);
					session.finish(new FileTransferError(FileTransferError.Type.ClosedError));
				}
				return true;
			}
			logger_.fine("wrong from/sessionID: " + from + " == " + session.from + " / " + ibb.getStreamID() + " == " + session.id);
			return false;
		}

		private IBBReceiveSession session;
		private int sequenceNumber;
		private long receivedSize;
		private Logger logger_ = Logger.getLogger(this.getClass().getName());
	};

	private String id = "";
	private JID from = new JID();
	private JID to = new JID();
	private long size;
	private WriteBytestream bytestream;
	private IQRouter router;
	private IBBResponder responder;
	private boolean active;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public IBBReceiveSession(final String id, final JID from, final JID to, long size, WriteBytestream bytestream, IQRouter router) {
		this.id = id; 
		this.from = from; 
		this.to = to;
		this.size = size; 
		this.bytestream = bytestream;
		this.router = router;
		this.active = false;
		assert(!id.isEmpty());
		assert(from.isValid());
		responder = new IBBResponder(this, router);
	}
	
	public void start() {
		logger_.fine("receive session started");
		active = true;
		responder.start();
	}

	public void stop() {
		logger_.fine("receive session stopped");
		responder.stop();
		if (active) {
			if (router.isAvailable()) {
				IBBRequest.create(to, from, IBB.createIBBClose(id), router).send();
			}
			finish(null);
		}
	}

	public JID getSender() {
		return from;
	}

	public JID getReceiver() {
		return to;
	}

	public final Signal1<FileTransferError> onFinished = new Signal1<FileTransferError>();

	private void finish(FileTransferError error) {
		active = false;
		onFinished.emit(error);
	}
}