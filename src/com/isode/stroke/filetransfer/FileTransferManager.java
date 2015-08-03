/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2013 Isode Limited.
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
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.DiscoInfo;
import java.util.Date;

public abstract class FileTransferManager {

	public abstract OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to, 
			final String filepath, 
			final String description, 
			ReadBytestream bytestream,
			final FileTransferOptions op);

	public abstract OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to,
			final String filename, 
			final String description, 
			final long sizeInBytes, 
			final Date lastModified, 
			ReadBytestream bytestream,
			final FileTransferOptions op);
			
	public static boolean isSupportedBy(final DiscoInfo info) {
		if (info != null) {
			return info.hasFeature(DiscoInfo.JingleFeature)
					&& info.hasFeature(DiscoInfo.JingleFTFeature)
					&& (info.hasFeature(DiscoInfo.JingleTransportsIBBFeature) || info.hasFeature(DiscoInfo.JingleTransportsS5BFeature));
		}
		return false;
	}

	public final Signal1<IncomingFileTransfer> onIncomingFileTransfer = new Signal1<IncomingFileTransfer>();
}