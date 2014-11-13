/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class GetDiscoInfoRequest extends GenericRequest<DiscoInfo> {

    public static GetDiscoInfoRequest create(final JID jid, IQRouter router) {
        return new GetDiscoInfoRequest(jid, router);
    }

    public static GetDiscoInfoRequest create(final JID jid, final String node, IQRouter router) {
        return new GetDiscoInfoRequest(jid, node, router);
    }

    private GetDiscoInfoRequest(final JID jid, IQRouter router) {
        super(IQ.Type.Get, jid, new DiscoInfo(), router);
    }

    private GetDiscoInfoRequest(final JID jid, final String node, IQRouter router) {
        this(jid, router);
        getPayloadGeneric().setNode(node);
    }

}
