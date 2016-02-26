/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.whiteboard;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot2;

public class OutgoingWhiteboardSession extends WhiteboardSession {

    private final WhiteboardServer server = new WhiteboardServer();
    
    public OutgoingWhiteboardSession(JID jid, IQRouter router) {
        super(jid, router);
    }
    
    public void startSession() {
        
        WhiteboardPayload payload = new WhiteboardPayload(WhiteboardPayload.Type.SessionRequest);
        GenericRequest<WhiteboardPayload> request = 
                new GenericRequest<WhiteboardPayload>(IQ.Type.Set, toJID_, payload, router_);
        request.onResponse.connect(new Slot2<WhiteboardPayload, ErrorPayload>() {
            
            @Override
            public void call(WhiteboardPayload payload, ErrorPayload error) {
                handleRequestResponse(payload, error);
            }
            
        });
        request.send();
    }
    
    private void handleRequestResponse(WhiteboardPayload payload,ErrorPayload error) {
        if (error != null) {
            onRequestRejected.emit(toJID_);
        }
    }

    @Override
    public void sendOperation(WhiteboardOperation operation) {
        operation.setID(idGenerator_.generateID());
        operation.setParentID(lastOpID_);
        lastOpID_ = operation.getID();

        server.handleLocalOperationReceived(operation);
        WhiteboardPayload payload = new WhiteboardPayload();
        payload.setOperation(operation);
        sendPayload(payload);
    }

    @Override
    protected void handleIncomingOperation(WhiteboardOperation operation) {
        WhiteboardOperation op = server.handleClientOperationReceived(operation);
        if (op.getPos() != -1) {
            onOperationReceived.emit(op);
        }
        lastOpID_ = op.getID();

        WhiteboardPayload payload = new WhiteboardPayload();
        payload.setOperation(op);
        sendPayload(payload);
    }

}
