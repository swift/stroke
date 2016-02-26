package com.isode.stroke.whiteboard;

import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class IncomingWhiteboardSession extends WhiteboardSession {

    private final WhiteboardClient client = new WhiteboardClient();
    
    public IncomingWhiteboardSession(JID jid, IQRouter router) {
        super(jid, router);
    }
    
    public void accept() {
        WhiteboardPayload payload = new WhiteboardPayload(WhiteboardPayload.Type.SessionAccept);
        GenericRequest<WhiteboardPayload> request = 
                new GenericRequest<WhiteboardPayload>(IQ.Type.Set, toJID_, payload, router_);
        request.send();
        onRequestAccepted.emit(toJID_);
    }

    @Override
    public void sendOperation(WhiteboardOperation operation) {
        operation.setID(idGenerator_.generateID());
        operation.setParentID(lastOpID_);
        lastOpID_ = operation.getID();

        WhiteboardOperation result = client.handleLocalOperationReceived(operation);

        if (result != null) {
            WhiteboardPayload payload = new WhiteboardPayload();
            payload.setOperation(result);
            sendPayload(payload);
        }
    }

    @Override
    protected void handleIncomingOperation(WhiteboardOperation operation) {
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(operation);
        if (pairResult.client != null) {
            if (pairResult.client.getPos() != -1) {
                onOperationReceived.emit(pairResult.client);
            }
            lastOpID_ = pairResult.client.getID();
        }

        if (pairResult.server != null) {
            WhiteboardPayload payload = new WhiteboardPayload();
            payload.setOperation(pairResult.server);
            sendPayload(payload);
        }
    }
    
    

}
