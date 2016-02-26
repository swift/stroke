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

import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.WhiteboardElement;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardPayload;
import com.isode.stroke.elements.WhiteboardPayload.Type;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal1;

public abstract class WhiteboardSession {
    
    protected final JID toJID_;
    protected final IQRouter router_;
    protected String lastOpID_ = "";
    protected final IDGenerator idGenerator_ = new IDGenerator();
    
    public final Signal1<WhiteboardElement> onElementReceived = new Signal1<WhiteboardElement>();
    public final Signal1<WhiteboardOperation> onOperationReceived = new Signal1<WhiteboardOperation>();
    public final Signal1<JID> onSessionTerminated = new Signal1<JID>();
    public final Signal1<JID> onRequestAccepted = new Signal1<JID>();
    public final Signal1<JID> onRequestRejected = new Signal1<JID>();

    public WhiteboardSession(JID jid,IQRouter router) {
        toJID_ = jid;
        router_ = router;
    }
    
    public void handleIncomingAction(WhiteboardPayload payload) {
        switch(payload.getType()) {
        case Data:
            handleIncomingOperation(payload.getOperation());
            return;
        case SessionAccept:
            onRequestAccepted.emit(toJID_);
            return;
        case SessionTerminate:
            onSessionTerminated.emit(toJID_);
            return;
        case SessionRequest:
        case UnknownType:
        default:
            return;
        }
    }
    public void sendElement(WhiteboardElement element) {
        WhiteboardPayload payload = new WhiteboardPayload();
        payload.setElement(element);
        GenericRequest<WhiteboardPayload> request = 
                new GenericRequest<WhiteboardPayload>(IQ.Type.Set, toJID_, payload, router_);
        request.send();
    }
    
    public abstract void sendOperation(WhiteboardOperation operation);
    
    public void cancel() {
        if (router_.isAvailable()) {
            WhiteboardPayload payload = new WhiteboardPayload(Type.SessionTerminate);
            GenericRequest<WhiteboardPayload> request = 
                    new GenericRequest<WhiteboardPayload>(IQ.Type.Set, toJID_, payload, router_);
            request.send();
        }
        onSessionTerminated.emit(toJID_);
    }
    
    public JID getTo() {
        return toJID_;
    }
    
    protected abstract void handleIncomingOperation(WhiteboardOperation operation);
    
    protected final void sendPayload(WhiteboardPayload payload) {
        GenericRequest<WhiteboardPayload> request = new GenericRequest<WhiteboardPayload>(IQ.Type.Set, toJID_, payload, router_);
        request.send();
    }
    
}
