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
import com.isode.stroke.elements.Version;
import com.isode.stroke.jid.JID;

public class GetVersionRequest extends GenericRequest<Version> {
    public GetVersionRequest(JID target, IQRouter iqRouter) {
        super(Type.Get, target, new Version(), iqRouter);
    }

    public GetVersionRequest(IQRouter iqRouter) {
        super(Type.Get, new JID(), new Version(), iqRouter);
    }
}
