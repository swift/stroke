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

import com.isode.stroke.jid.JID;

public interface FileTransferTransporterFactory {

	public FileTransferTransporter createInitiatorTransporter(
		final JID initiator, 
		final JID responder,
		final FileTransferOptions options);
	public FileTransferTransporter createResponderTransporter(
		final JID initiator, 
		final JID responder, 
		final String s5bSessionID,
		final FileTransferOptions options);
}