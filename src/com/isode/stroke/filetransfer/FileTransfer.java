/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2013-2015 Isode Limited.
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

/**
 * Because of the lack of multiple inheritance in Java, this has to be done
 * slightly differently from Swiften. What happens is that the methods in Swiften
 * are provided abstract here. Any class implementing this interface directly/indirectly (through other interface) has to implement these methods.
 * OutgoingJingleFileTransfer implements this interface indirectly through OutgoingFileTransfer.
 * IncomingJingleFileTransfer implements this interface indirectly through IncomingFileTransfer.
 * OutgoingSIFileTransfer implements this interface indirectly through OutgoingFileTransfer.
 */
public interface FileTransfer {

	public static class State {
		public enum Type {
			Initial,
			WaitingForStart,
			Negotiating,
			WaitingForAccept,
			Transferring,
			Canceled,
			Failed,
			Finished
		};

		public State(Type type) {
			this(type, "");
		}

		public State(Type type, final String message) {
			this.type = type;
			this.message = message;
		}

		public Type type;
		public String message = "";
	};

	public final Signal1<Integer /* proccessedBytes */> onProcessedBytes = new Signal1<Integer>();
	public final Signal1<State> onStateChanged = new Signal1<State>();
	public final Signal1<FileTransferError> onFinished = new Signal1<FileTransferError>();

	public void cancel();

	public String getFileName();

	public long getFileSizeInBytes();

	public void setFileInfo(final String name, long size);
}
