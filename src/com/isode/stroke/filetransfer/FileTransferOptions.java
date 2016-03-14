/*
 * Copyright (c) 2013-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

public class FileTransferOptions {
    
	private boolean allowInBand_;
	private boolean allowAssisted_;
	private boolean allowProxied_;
	private boolean allowDirect_;

	public FileTransferOptions() {
		allowInBand_ = true;
		allowAssisted_ = true;
		allowProxied_ = true;
		allowDirect_ = true;
	}

	/**
	 * Copy constructor 
	 * @param other {@link FileTransferOptions} to copy
	 */
	public FileTransferOptions(FileTransferOptions other) {
	    this.allowInBand_ = other.allowInBand_;
	    this.allowAssisted_ = other.allowAssisted_;
	    this.allowProxied_ = other.allowProxied_;
	    this.allowDirect_ = other.allowDirect_;
	}

	public FileTransferOptions withInBandAllowed(boolean b) {
		allowInBand_ = b;
		return this;
	}

	public boolean isInBandAllowed() {
		return allowInBand_;
	}

	public FileTransferOptions withAssistedAllowed(boolean b) {
		allowAssisted_ = b;
		return this;
	}

	public boolean isAssistedAllowed() {
		return allowAssisted_;
	}

	public FileTransferOptions withProxiedAllowed(boolean b) {
		allowProxied_ = b;
		return this;
	}

	public boolean isProxiedAllowed() {
		return allowProxied_;
	}

	public FileTransferOptions withDirectAllowed(boolean b) {
		allowDirect_ = b;
		return this;
	}

	public boolean isDirectAllowed() {
		return allowDirect_;
	}
}