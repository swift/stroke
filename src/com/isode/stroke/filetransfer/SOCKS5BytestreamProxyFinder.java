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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.isode.stroke.disco.DiscoServiceWalker;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot2;

/*
 * This class is designed to find possible SOCKS5 bytestream proxies which are used for peer-to-peer data transfers in
 * restrictive environments.
 */
public class SOCKS5BytestreamProxyFinder {
    
	private JID service;
	private IQRouter iqRouter;
	private DiscoServiceWalker serviceWalker;
	private final List<S5BProxyRequest> proxyHosts = new ArrayList<S5BProxyRequest>();
	private final Set<GenericRequest<S5BProxyRequest>> pendingRequests = new HashSet<GenericRequest<S5BProxyRequest>>();
	
	private SignalConnection onServiceFoundConnection;
	private SignalConnection onWalkCompleteConnection;
	private final Map<GenericRequest<S5BProxyRequest>,SignalConnection> requestOnResponseConnections
	    = new HashMap<GenericRequest<S5BProxyRequest>,SignalConnection>();
	

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
		onWalkCompleteConnection = serviceWalker.onWalkComplete.connect(new Slot() {
            
            @Override
            public void call() {
                handleWalkEnded();
            }
            
        });
		serviceWalker.beginWalk();
	}

	public void stop() {
		for (SignalConnection onResponseConnection : requestOnResponseConnections.values()) {
		    onResponseConnection.disconnect();
		}
		requestOnResponseConnections.clear();
	    serviceWalker.endWalk();
		onServiceFoundConnection.disconnect();
		onWalkCompleteConnection.disconnect();
		serviceWalker = null;
	}
	
	public final Signal1<List<S5BProxyRequest>> onProxiesFound = new Signal1<List<S5BProxyRequest>>();

	private void sendBytestreamQuery(final JID jid) {
		S5BProxyRequest proxyRequest = new S5BProxyRequest();
		final GenericRequest<S5BProxyRequest> requester = new GenericRequest<S5BProxyRequest>(IQ.Type.Get, jid, proxyRequest, iqRouter);
		SignalConnection requestOnResponseConnection = requester.onResponse.connect(new Slot2<S5BProxyRequest, ErrorPayload>() {
			@Override
			public void call(S5BProxyRequest s, ErrorPayload e) {
				handleProxyResponse(requester,s,e);
			}
		});
		pendingRequests.add(requester);
		requestOnResponseConnections.put(requester, requestOnResponseConnection);
		requester.send();
	}

	private void handleServiceFound(final JID jid, DiscoInfo discoInfo) {
		if (discoInfo.hasFeature(DiscoInfo.Bytestream)) {
			sendBytestreamQuery(jid);
		}
	}
	
	private void handleWalkEnded() {
	    if (pendingRequests.isEmpty()) {
	        onProxiesFound.emit(proxyHosts);
	    }
	}
	
	private void handleProxyResponse(GenericRequest<S5BProxyRequest> requester,S5BProxyRequest request, ErrorPayload error) {
		SignalConnection requestOnResponseConnection = requestOnResponseConnections.remove(request);
		if (requestOnResponseConnection != null) {
		    requestOnResponseConnection.disconnect();
		}
		pendingRequests.remove(requester);
	    if (error != null) {
			logger_.fine("ERROR\n");
		} else {
			if (request != null) {
			    logger_.fine("add request\n");
				proxyHosts.add(request);
			} 
		}
	    if (pendingRequests.isEmpty() && !serviceWalker.isActive()) {
	        onProxiesFound.emit(proxyHosts);
	    }
	}
}