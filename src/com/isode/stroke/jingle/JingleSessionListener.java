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

public interface JingleSessionListener {

	public void handleSessionAcceptReceived(final JingleContentID id, JingleDescription des, JingleTransportPayload tr);
	public void handleSessionInfoReceived(JinglePayload payload);
	public void handleSessionTerminateReceived(JinglePayload.Reason reason);
	public void handleTransportAcceptReceived(final JingleContentID id, JingleTransportPayload tr);
	public void handleTransportInfoReceived(final JingleContentID id, JingleTransportPayload tr);
	public void handleTransportRejectReceived(final JingleContentID id, JingleTransportPayload tr);
	public void handleTransportReplaceReceived(final JingleContentID id, JingleTransportPayload tr);
	public void handleTransportInfoAcknowledged(final String id);
}