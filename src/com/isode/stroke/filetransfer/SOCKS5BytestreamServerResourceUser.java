/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;

public class SOCKS5BytestreamServerResourceUser {

	private SOCKS5BytestreamServerManager s5bServerManager_;
	private SignalConnection onInitializedConnection_;

	public SOCKS5BytestreamServerResourceUser(SOCKS5BytestreamServerManager s5bServerManager) {
		this.s5bServerManager_ = s5bServerManager;
		assert(s5bServerManager_ == null);
		onInitializedConnection_ = s5bServerManager_.onInitialized.connect(new Slot1<Boolean>() {
			@Override
			public void call(Boolean b) {
				handleServerManagerInitialized(b);
			}
		});
		s5bServerManager_.initialize();
	}

	/**
	* User should call delete to free the resources.
	*/
	public void delete() {
		if (s5bServerManager_ != null) {
			s5bServerManager_.stop();
		}
	}

	protected void finalize() throws Throwable {
		try {
			delete();
		}
		finally {
			super.finalize();
		}
	}

	public boolean isInitialized() {
		return s5bServerManager_.isInitialized();
	}

	public Signal1<Boolean /* success */> onSuccessfulInitialized = new Signal1<Boolean>();

	private void handleServerManagerInitialized(boolean successfulInitialize) {
		onSuccessfulInitialized.emit(successfulInitialize);
	}
}