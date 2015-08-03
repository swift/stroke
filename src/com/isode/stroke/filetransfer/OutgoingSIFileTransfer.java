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

import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.StreamInitiation;
import com.isode.stroke.elements.Bytestreams;

public class OutgoingSIFileTransfer implements OutgoingFileTransfer {

	private long fileSizeInBytes = 0; //FileTransferVariables
	private String filename = ""; //FileTransferVariables

	/**
	* FileTransferMethod.
	*/
	@Override
	public String getFileName() {
		return filename;
	}

	/**
	* FileTransferMethod.
	*/
	@Override
	public long getFileSizeInBytes() {
		return fileSizeInBytes;
	}

	/**
	* FileTransferMethod.
	*/
	@Override
	public void setFileInfo(final String name, long size) {
		this.filename = name;
		this.fileSizeInBytes = size;
	}

	private String id = "";
	private JID from;
	private JID to;
	private String name = "";
	private long size;
	private String description = "";
	private ReadBytestream bytestream;
	private IQRouter iqRouter;
	private SOCKS5BytestreamServer socksServer;
	private IBBSendSession ibbSession;

	public OutgoingSIFileTransfer(final String id, final JID from, final JID to, final String name, long size, final String description, ReadBytestream bytestream, IQRouter iqRouter, SOCKS5BytestreamServer socksServer) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.name = name;
		this.size = size;
		this.description = description;
		this.bytestream = bytestream;
		this.iqRouter = iqRouter;
		this.socksServer = socksServer;
		this.ibbSession = ibbSession;
	}

	/**
	* OutgoingFileTransferMethod.
	*/
	@Override
	public void start() {
		/*
		StreamInitiation::ref streamInitiation(new StreamInitiation());
		streamInitiation.setID(id);
		streamInitiation.setFileInfo(StreamInitiationFileInfo(name, description, size));
		//streamInitiation.addProvidedMethod("http://jabber.org/protocol/bytestreams");
		streamInitiation.addProvidedMethod("http://jabber.org/protocol/ibb");
		StreamInitiationRequest::ref request = StreamInitiationRequest::create(to, streamInitiation, iqRouter);
		request.onResponse.connect(boost::bind(&OutgoingSIFileTransfer::handleStreamInitiationRequestResponse, this, _1, _2));
		request.send();
		*/
	}

	public void stop() {
	}

	public final Signal1<FileTransferError> onFinished = new Signal1<FileTransferError>();

	private void handleStreamInitiationRequestResponse(StreamInitiation stream, ErrorPayload error) {
		/*
		if (error) {
			finish(FileTransferError());
		}
		else {
			if (response->getRequestedMethod() == "http://jabber.org/protocol/bytestreams") {
				socksServer->addReadBytestream(id, from, to, bytestream);
				Bytestreams::ref bytestreams(new Bytestreams());
				bytestreams->setStreamID(id);
				HostAddressPort addressPort = socksServer->getAddressPort();
				bytestreams->addStreamHost(Bytestreams::StreamHost(addressPort.getAddress().toString(), from, addressPort.getPort()));
				BytestreamsRequest::ref request = BytestreamsRequest::create(to, bytestreams, iqRouter);
				request->onResponse.connect(boost::bind(&OutgoingSIFileTransfer::handleBytestreamsRequestResponse, this, _1, _2));
				request->send();
			}
			else if (response->getRequestedMethod() == "http://jabber.org/protocol/ibb") {
				ibbSession = boost::make_shared<IBBSendSession>(id, from, to, bytestream, iqRouter);
				ibbSession->onFinished.connect(boost::bind(&OutgoingSIFileTransfer::handleIBBSessionFinished, this, _1));
				ibbSession->start();
			}
		}
		*/
	}

	private void handleBytestreamsRequestResponse(Bytestreams stream, ErrorPayload error) {
		/*
		if (error) {
			finish(FileTransferError());
		}
		*/
		//socksServer->onTransferFinished.connect();
	}

	private void finish(FileTransferError error) {
		/*
		if (ibbSession) {
			ibbSession->onFinished.disconnect(boost::bind(&OutgoingSIFileTransfer::handleIBBSessionFinished, this, _1));
			ibbSession.reset();
		}
		socksServer->removeReadBytestream(id, from, to);
		onFinished(error);
		*/
	}

	private void handleIBBSessionFinished(FileTransferError error) {
		//finish(error);
	}

	public void cancel() {
		
	}
}