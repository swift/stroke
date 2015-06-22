/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.base.NotNull;

public class JingleFileTransferHash extends Payload {

	private JingleFileTransferFileInfo fileInfo_ = new JingleFileTransferFileInfo();

	/**
	* Default Constructor.
	*/
	public JingleFileTransferHash() {

	}

	/**
	* @param fileInfo, NotNull.
	*/
	public void setFileInfo(JingleFileTransferFileInfo fileInfo) {
		NotNull.exceptIfNull(fileInfo, "fileInfo");
		fileInfo_ = fileInfo;
	}

	/**
	* @return fileInfo, NotNull.
	*/
	public JingleFileTransferFileInfo getFileInfo() {
		return fileInfo_;
	}
}
