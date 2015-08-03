/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
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

import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.disco.DiscoServiceWalker;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import java.util.logging.Logger;

/*
 * This class is designed to find possible SOCKS5 bytestream proxies which are used for peer-to-peer data transfers in
 * restrictive environments.
 */
public class SOCKS5BytestreamProxyFinder {

	private JID service;
	private IQRouter iqRouter;
	private DiscoServiceWalker serviceWalker;
	private Vector<GenericRequest<S5BProxyRequest> > requests = new Vector<GenericRequest<S5BProxyRequest>>();
	private SignalConnection onServiceFoundConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public SOCKS5BytestreamProxyFinder(final JID service, IQRouter iqRouter) {
		this.service = service;
		this.iqRouter = iqRouter;
	}

	public void start() {
		serviceWalker = new DiscoServiceWalker(service, iqRouter);
		onServiceFoundConnection = serviceWalker.onServiceFound.connect(new Slot2<JID, DiscoInfo>() {
			@Override
			public void call(JID j, DiscoInfo d) {
				handleServiceFound(j, d);
			}
		});
		serviceWalker.beginWalk();
	}

	public void stop() {
		serviceWalker.endWalk();
		onServiceFoundConnection.disconnect();
		serviceWalker = null;
	}

	public final Signal1<S5BProxyRequest> onProxyFound = new Signal1<S5BProxyRequest>();

	private void sendBytestreamQuery(final JID jid) {
		S5BProxyRequest proxyRequest = new S5BProxyRequest();
		GenericRequest<S5BProxyRequest> request = new GenericRequest<S5BProxyRequest>(IQ.Type.Get, jid, proxyRequest, iqRouter);
		request.onResponse.connect(new Slot2<S5BProxyRequest, ErrorPayload>() {
			@Override
			public void call(S5BProxyRequest s, ErrorPayload e) {
				handleProxyResponse(s, e);
			}
		});
		request.send();
	}

	private void handleServiceFound(final JID jid, DiscoInfo discoInfo) {
		if (discoInfo.hasFeature(DiscoInfo.Bytestream)) {
			sendBytestreamQuery(jid);
		}
	}
	private void handleProxyResponse(S5BProxyRequest request, ErrorPayload error) {
		if (error != null) {
			logger_.fine("ERROR\n");
		} else {
			if (request != null) {
				onProxyFound.emit(request);
			} else {
				//assert(false);
			}
		}
	}
}