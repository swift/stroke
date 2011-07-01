/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.queries;

import com.isode.stroke.elements.IQ.Type;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.jid.JID;

public class GetRosterRequest extends GenericRequest<RosterPayload> {
    public GetRosterRequest(JID target, IQRouter iqRouter) {
        super(Type.Get, target, new RosterPayload(), iqRouter);
    }

    public GetRosterRequest(IQRouter iqRouter) {
        super(Type.Get, new JID(), new RosterPayload(), iqRouter);
    }
}
