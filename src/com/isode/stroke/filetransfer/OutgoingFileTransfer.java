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

/**
 * Because of the lack of multiple inheritance in Java, this has to be done
 * slightly differently from Swiften. What happens is that the methods in Swiften
 * are provided abstract here. Any class implementing this interface directly/indirectly (through other interface) has to implement these methods.
 * OutgoingJingleFileTransfer and OutgoingSIFileTransfer implements this interface and will also need to implement methods from FileTransfer (which is an interface).
 */
public interface OutgoingFileTransfer extends FileTransfer {

	public void start();
}