/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.roster;

import com.isode.stroke.elements.IQ.Type;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class GetRosterRequest extends GenericRequest<RosterPayload> {
    public GetRosterRequest(JID target, IQRouter iqRouter) {
        super(Type.Get, target, new RosterPayload(), iqRouter);
    }

    public GetRosterRequest(IQRouter iqRouter) {
        super(Type.Get, new JID(), new RosterPayload(), iqRouter);
    }
    
    public static GetRosterRequest create(IQRouter router) {
		return new GetRosterRequest(router);
	}

    public static GetRosterRequest create(IQRouter router, String version) {
    	GetRosterRequest request = new GetRosterRequest(router);
    	request.getPayloadGeneric().setVersion(version);
    	return request;
    }
}
