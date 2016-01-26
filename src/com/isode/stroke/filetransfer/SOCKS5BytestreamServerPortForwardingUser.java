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

public class SOCKS5BytestreamServerPortForwardingUser {

	private SOCKS5BytestreamServerManager s5bServerManager_;
	private SignalConnection onPortForwardingSetupConnection_;

	public SOCKS5BytestreamServerPortForwardingUser(SOCKS5BytestreamServerManager s5bServerManager) {
		this.s5bServerManager_ = s5bServerManager;
		// the server should be initialized, so we know what port to setup a forward for
		assert(s5bServerManager.isInitialized());
		if (s5bServerManager_.isPortForwardingReady()) {
			onSetup.emit(!s5bServerManager_.getAssistedHostAddressPorts().isEmpty());
		}
		else {
			onPortForwardingSetupConnection_ = s5bServerManager_.onPortForwardingSetup.connect(new Slot1<Boolean>() {
				@Override
				public void call(Boolean s) {
					handleServerManagerPortForwardingSetup(s);
				}
			});
			s5bServerManager_.setupPortForwarding();
		}
	}

	/**
	* User should call delete to free the resources.
	*/
	public void delete() {
		if (s5bServerManager_.isPortForwardingReady()) {
			s5bServerManager_.removePortForwarding();
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

	public boolean isForwardingSetup() {
		return s5bServerManager_.isPortForwardingReady();
	}

	public Signal1<Boolean /* success */> onSetup = new Signal1<Boolean>();

	private void handleServerManagerPortForwardingSetup(boolean successful) {
		onSetup.emit(successful);
	}
}