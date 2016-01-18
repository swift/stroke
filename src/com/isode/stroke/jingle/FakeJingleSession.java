/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2013-2015 Isode Limited.
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
import com.isode.stroke.elements.Payload;
import com.isode.stroke.jingle.JingleContentID;
import com.isode.stroke.jingle.JingleSession;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.SimpleIDGenerator;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot3;
import java.util.Vector;

public class FakeJingleSession extends JingleSession {

	public interface Command {

	}

	public class InitiateCall implements Command {
		public InitiateCall(JingleContentID contentId, JingleDescription desc, JingleTransportPayload payL) {
			this.id = contentId;
			this.description = desc;
			this.payload = payL;
		}
		public JingleContentID id;
		public JingleDescription description;
		public JingleTransportPayload payload;
	};

	public class  TerminateCall implements Command {
		public TerminateCall(JinglePayload.Reason.Type r) {
			this.reason = r;
		}
		public JinglePayload.Reason.Type reason;
	};

	public class InfoCall implements Command {
		public InfoCall(Payload info){
			this.payload = info;
		}
		public Payload payload;
	};

	public class AcceptCall implements Command {
		public AcceptCall(JingleContentID contentId, JingleDescription desc, JingleTransportPayload payL) {
			this.id = contentId;
			this.description = desc;
			this.payload = payL;
		}
		public JingleContentID id;
		public JingleDescription description;
		public JingleTransportPayload payload;
	};

	public class InfoTransportCall implements Command {
		public InfoTransportCall(JingleContentID contentId, JingleTransportPayload payL) {
			this.id = contentId;
			this.payload = payL;
		}
		public JingleContentID id;
		public JingleTransportPayload payload;
	};

	public class AcceptTransportCall implements Command {
		public AcceptTransportCall(JingleContentID contentId, JingleTransportPayload payL) {
			this.id = contentId;
			this.payload = payL;
		}
		public JingleContentID id;
		public JingleTransportPayload payload;
	};

	public class RejectTransportCall implements Command {
		public RejectTransportCall(JingleContentID contentId, JingleTransportPayload payL) {
			this.id = contentId;
			this.payload = payL;
		}
		public JingleContentID id;
		public JingleTransportPayload payload;
	};
	
	public class ReplaceTransportCall implements Command {
		public ReplaceTransportCall(JingleContentID contentId, JingleTransportPayload payL) {
			this.id = contentId;
			this.payload = payL;
		}
		public JingleContentID id;
		public JingleTransportPayload payload;
	};

	public Vector<Command> calledCommands = new Vector<Command>();
	public SimpleIDGenerator idGenerator = new SimpleIDGenerator();

	public FakeJingleSession(final JID initiator, final String id) {
		super(initiator, id);
	}

	public void sendInitiate(final JingleContentID id, JingleDescription desc, JingleTransportPayload payload) {
		calledCommands.add(new InitiateCall(id, desc, payload));
	}

	public void sendTerminate(JinglePayload.Reason.Type reason) {
		calledCommands.add(new TerminateCall(reason));
	}

	public void sendInfo(Payload payload) {
		calledCommands.add(new InfoCall(payload));
	}

	public void sendAccept(final JingleContentID id, JingleDescription desc) {
		sendAccept(id, desc, null);
	}

	public void sendAccept(final JingleContentID id, JingleDescription desc, JingleTransportPayload payload) {
		calledCommands.add(new AcceptCall(id, desc, payload));
	}

	public String sendTransportInfo(final JingleContentID id, JingleTransportPayload payload) {
		calledCommands.add(new InfoTransportCall(id, payload));
		return idGenerator.generateID();
	}

	public void sendTransportAccept(final JingleContentID id, JingleTransportPayload payload) {
		calledCommands.add(new AcceptTransportCall(id, payload));
	}

	public void sendTransportReject(final JingleContentID id, JingleTransportPayload payload) {
		calledCommands.add(new RejectTransportCall(id, payload));
	}

	public void sendTransportReplace(final JingleContentID id, JingleTransportPayload payload) {
		calledCommands.add(new ReplaceTransportCall(id, payload));
	}

	public void handleSessionTerminateReceived(final JinglePayload.Reason reason) {
		notifyListeners(new ListenableCallback<JingleSessionListener>() {

            @Override
            public void call(JingleSessionListener listener) {
                listener.handleSessionTerminateReceived(reason);
            }
            
        });
	}

	public void handleSessionAcceptReceived(final JingleContentID id,final JingleDescription desc,
	        final JingleTransportPayload transport) {
		notifyListeners(new ListenableCallback<JingleSessionListener>() {

            @Override
            public void call(JingleSessionListener listener) {
                listener.handleSessionAcceptReceived(id, desc, transport);
            }
            
        });
	}

	public void handleSessionInfoReceived(JinglePayload payload) {

	}

	public void handleTransportReplaceReceived(final JingleContentID id,final JingleTransportPayload payload) {
	    notifyListeners(new ListenableCallback<JingleSessionListener>() {

            @Override
            public void call(JingleSessionListener listener) {
                listener.handleTransportReplaceReceived(id, payload);
            }
            
        });
	}

	public void handleTransportAcceptReceived(final JingleContentID id,final JingleTransportPayload payload) {
	    notifyListeners(new ListenableCallback<JingleSessionListener>() {

            @Override
            public void call(JingleSessionListener listener) {
                listener.handleTransportAcceptReceived(id, payload);
            }
            
        });
	}

	public void handleTransportInfoReceived(final JingleContentID id,final JingleTransportPayload payload) {
	    notifyListeners(new ListenableCallback<JingleSessionListener>() {

            @Override
            public void call(JingleSessionListener listener) {
                listener.handleTransportInfoReceived(id, payload);;
            }
            
        });
	}

	public void handleTransportRejectReceived(final JingleContentID id, JingleTransportPayload payload) {

	}

	public void handleTransportInfoAcknowledged(final String id) {

	}
}