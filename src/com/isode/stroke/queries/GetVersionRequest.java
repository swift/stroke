/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.queries;

import com.isode.stroke.elements.IQ.Type;
import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.jid.JID;

public class GetVersionRequest extends GenericRequest<SoftwareVersion> {
    public GetVersionRequest(JID target, IQRouter iqRouter) {
        super(Type.Get, target, new SoftwareVersion(), iqRouter);
    }

    public GetVersionRequest(IQRouter iqRouter) {
        super(Type.Get, new JID(), new SoftwareVersion(), iqRouter);
    }
}
