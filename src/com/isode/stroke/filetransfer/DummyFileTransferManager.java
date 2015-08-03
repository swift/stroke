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

import com.isode.stroke.signals.Signal1;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.S5BProxyRequest;
import java.util.Date;

public class DummyFileTransferManager extends FileTransferManager {

	public DummyFileTransferManager() {
		super();
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to, 
			final String filepath, 
			final String description, 
			ReadBytestream bytestream,
			final FileTransferOptions op) {
		return null;
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to,
			final String filename, 
			final String description, 
			final long sizeInBytes, 
			final Date lastModified, 
			ReadBytestream bytestream,
			final FileTransferOptions op) {
		return null;
	}

	public void addS5BProxy(S5BProxyRequest p) {
	}
}