/*
 * Copyright (c) 2011-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jingle;

import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot3;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;

public class JingleSessionImpl extends JingleSession {

	private IQRouter iqRouter;
	private JID peerJID;
	private Map<GenericRequest<JinglePayload> , SignalConnection> pendingRequests = new HashMap<GenericRequest<JinglePayload> , SignalConnection>();
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JingleSessionImpl(final JID initiator, final JID peerJID, final String id, IQRouter router) {
		super(initiator, id);
		this.iqRouter = router;
		this.peerJID = peerJID;
		logger_.fine("initiator: " + initiator + ", peerJID: " + peerJID + "\n");
	}

	public void sendInitiate(final JingleContentID id, JingleDescription description, JingleTransportPayload transport) {
		JinglePayload payload = new JinglePayload(JinglePayload.Action.SessionInitiate, getID());
		payload.setInitiator(getInitiator());
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(id.getCreator());
		content.setName(id.getName());
		content.addDescription(description);
		content.addTransport(transport);
		payload.addPayload(content);

		sendSetRequest(payload);
	}

	public void sendTerminate(JinglePayload.Reason.Type reason) {
		JinglePayload payload = new JinglePayload(JinglePayload.Action.SessionTerminate, getID());
		payload.setReason(new JinglePayload.Reason(reason));
		payload.setInitiator(getInitiator());
		sendSetRequest(payload);
	}

	public void sendInfo(Payload info) {
		JinglePayload payload = new JinglePayload(JinglePayload.Action.SessionInfo, getID());
		payload.addPayload(info);

		sendSetRequest(payload);
	}

	public void sendAccept(final JingleContentID id, JingleDescription description) {
		sendAccept(id, description, null);
	}

	public void sendAccept(final JingleContentID id, JingleDescription description, JingleTransportPayload transPayload) {
		JinglePayload payload = createPayload();
		
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(id.getCreator());
		content.setName(id.getName());
		content.addTransport(transPayload);
		content.addDescription(description);
		payload.setAction(JinglePayload.Action.SessionAccept);
		payload.addPayload(content);
		
		// put into IQ:set and send it away
		sendSetRequest(payload);
	}

	public String sendTransportInfo(final JingleContentID id, JingleTransportPayload transPayload) {
		JinglePayload payload = createPayload();

		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(id.getCreator());
		content.setName(id.getName());
		content.addTransport(transPayload);
		payload.setAction(JinglePayload.Action.TransportInfo);
		payload.addPayload(content);

		return sendSetRequest(payload);
	}

	public void sendTransportAccept(final JingleContentID id, JingleTransportPayload transPayload) {
		JinglePayload payload = createPayload();

		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(id.getCreator());
		content.setName(id.getName());
		content.addTransport(transPayload);
		payload.setAction(JinglePayload.Action.TransportAccept);
		payload.addPayload(content);

		// put into IQ:set and send it away
		sendSetRequest(payload);
	}

	public void sendTransportReject(final JingleContentID id, JingleTransportPayload transPayload) {
		JinglePayload payload = createPayload();

		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(id.getCreator());
		content.setName(id.getName());
		content.addTransport(transPayload);
		payload.setAction(JinglePayload.Action.TransportReject);
		payload.addPayload(content);

		sendSetRequest(payload);		
	}

	public void sendTransportReplace(final JingleContentID id, JingleTransportPayload transPayload) {
		JinglePayload payload = createPayload();

		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(id.getCreator());
		content.setName(id.getName());
		content.addTransport(transPayload);
		payload.setAction(JinglePayload.Action.TransportReplace);
		payload.addContent(content);

		sendSetRequest(payload);
	}

	void handleIncomingAction(JinglePayload action) {
		if (JinglePayload.Action.SessionTerminate.equals(action.getAction())) {
			/*notifyListeners(new Slot1<JinglePayload.Reason>() {
				@Override
				public void call(JinglePayload.Reason reason) {
					handleSessionTerminateReceived(reason);
				}
			}, action.getReason());*/
			return;
		}
		if (JinglePayload.Action.SessionInfo.equals(action.getAction())) {
			/*notifyListeners(new Slot1<JinglePayload>() {
				@Override
				public void call(JinglePayload p) {
					handleSessionInfoReceived(p);
				}
			}, action);*/
			return;
		}

		JingleContentPayload content = action.getPayload(new JingleContentPayload());
		if (content == null) {
			logger_.fine("no content payload!\n");
			return;
		}
		JingleContentID contentID = new JingleContentID(content.getName(), content.getCreator());
		JingleDescription description = content.getDescriptions().isEmpty() ? null : content.getDescriptions().get(0);
		JingleTransportPayload transport = content.getTransports().isEmpty() ? null : content.getTransports().get(0);
		switch(action.getAction()) {
			case SessionAccept:
				/*notifyListeners(new Slot3<JingleContentID, JingleDescription, JingleTransportPayload>() {
					@Override
					public void call(JingleContentID id, JingleDescription des, JingleTransportPayload tr) {
						handleSessionAcceptReceived(id, des, tr);
					}
				}, contentID, description, transport);*/
				return;
			case TransportAccept:
				/*notifyListeners(new Slot2<JingleContentID, JingleTransportPayload>() {
					@Override
					public void call(JingleContentID id, JingleTransportPayload tr) {
						handleTransportAcceptReceived(id, tr);
					}
				}, contentID, transport);*/
				return;
			case TransportInfo:
				/*notifyListeners(new Slot2<JingleContentID, JingleTransportPayload>() {
					@Override
					public void call(JingleContentID id, JingleTransportPayload tr) {
						handleTransportInfoReceived(id, tr);
					}
				}, contentID, transport);*/
				return;
			case TransportReject:
				/*notifyListeners(new Slot2<JingleContentID, JingleTransportPayload>() {
					@Override
					public void call(JingleContentID id, JingleTransportPayload tr) {
						handleTransportRejectReceived(id, tr);
					}
				}, contentID, transport);*/
				return;
			case TransportReplace:
				/*notifyListeners(new Slot2<JingleContentID, JingleTransportPayload>() {
					@Override
					public void call(JingleContentID id, JingleTransportPayload tr) {
						handleTransportReplaceReceived(id, tr);
					}
				}, contentID, transport);*/
				return;
			// following unused Jingle actions
			case ContentAccept:
			case ContentAdd:
			case ContentModify:
			case ContentReject:
			case ContentRemove:
			case DescriptionInfo:
			case SecurityInfo:

			// handled elsewhere
			case SessionInitiate:
			case SessionInfo:
			case SessionTerminate:

			case UnknownAction:
				return;
		}
		assert(false);
	}
			
	private String sendSetRequest(JinglePayload payload) {
		final GenericRequest<JinglePayload> request = new GenericRequest<JinglePayload>(IQ.Type.Set, peerJID, payload, iqRouter);
		pendingRequests.put(request, request.onResponse.connect(new Slot2<JinglePayload, ErrorPayload>() {
			@Override
			public void call(JinglePayload p, ErrorPayload e) {
				handleRequestResponse(request);
			}
		}));
		return request.send();
	}

	private JinglePayload createPayload() {
		JinglePayload payload = new JinglePayload();
		payload.setSessionID(getID());
		payload.setInitiator(getInitiator());
		return payload;
	}

	private void handleRequestResponse(GenericRequest<JinglePayload> request) {
		assert (pendingRequests.containsKey(request));
		if (JinglePayload.Action.TransportInfo.equals(request.getPayloadGeneric().getAction())) {
			/*notifyListeners(new Slot1<String>() {
				@Override
				public void call(String s) {
					handleTransportInfoAcknowledged(s);
				}
			}, request.getID());*/
		}
		pendingRequests.get(request).disconnect();
		pendingRequests.remove(request);		
	}
}