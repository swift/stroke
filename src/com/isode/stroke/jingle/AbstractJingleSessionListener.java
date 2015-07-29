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

package com.isode.stroke.jingle;

import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleTransportPayload;
import java.util.logging.Logger;

public class AbstractJingleSessionListener implements JingleSessionListener {

	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public void handleSessionAcceptReceived(final JingleContentID id, JingleDescription des, JingleTransportPayload tr) {
		logger_.warning("Unimplemented\n");
	}

	public void handleSessionInfoReceived(JinglePayload payload) {
		logger_.warning("Unimplemented\n");
	}

	public void handleSessionTerminateReceived(JinglePayload.Reason reason) {
		logger_.warning("Unimplemented\n");
	}

	public void handleTransportAcceptReceived(final JingleContentID id, JingleTransportPayload tr) {
		logger_.warning("Unimplemented\n");
	}

	public void handleTransportInfoReceived(final JingleContentID id, JingleTransportPayload tr) {
		logger_.warning("Unimplemented\n");
	}

	public void handleTransportRejectReceived(final JingleContentID id, JingleTransportPayload tr) {
		logger_.warning("Unimplemented\n");
	}

	public void handleTransportReplaceReceived(final JingleContentID id, JingleTransportPayload tr) {
		logger_.warning("Unimplemented\n");
	}

	public void handleTransportInfoAcknowledged(final String id) {

	}
}