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

import com.isode.stroke.elements.WhiteboardPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.SetResponder;
import com.isode.stroke.elements.ErrorPayload;

public class WhiteboardResponder extends SetResponder<WhiteboardPayload> {
    
    private final WhiteboardSessionManager sessionManager_;
    private final IQRouter router_;
    
    public WhiteboardResponder(WhiteboardSessionManager sessionManager,IQRouter router) {
        super(new WhiteboardPayload(),router);
        sessionManager_ = sessionManager;
        router_ = router;
    }

    @Override
    protected boolean handleSetRequest(JID from, JID to, String id,
            WhiteboardPayload payload) {
        if (payload.getType() == WhiteboardPayload.Type.SessionRequest) {
            if (sessionManager_.getSession(from) != null) {
                sendError(from, id, ErrorPayload.Condition.Conflict, ErrorPayload.Type.Cancel);
            } 
            else {
                sendResponse(from, id, null);
                IncomingWhiteboardSession session = new IncomingWhiteboardSession(from, router_);
                sessionManager_.handleIncomingSession(session);
            }
        } 
        else {
            sendResponse(from, id, null);
            WhiteboardSession session = sessionManager_.getSession(from);
            if (session != null) {
                session.handleIncomingAction(payload);
            }
        }
        return true;
    }
    
}
