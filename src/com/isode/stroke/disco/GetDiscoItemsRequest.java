/*
 * Copyright (c) 2010-2015, Isode Limited.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class GetDiscoItemsRequest extends GenericRequest<DiscoItems> {

    public static GetDiscoItemsRequest create(final JID jid, IQRouter router) {
        return new GetDiscoItemsRequest(jid, router);
    }

    public static GetDiscoItemsRequest create(final JID jid, final String node, IQRouter router) {
        return new GetDiscoItemsRequest(jid, node, router);
    }

    private GetDiscoItemsRequest(final JID jid, IQRouter router) {
        super(IQ.Type.Get, jid, new DiscoItems(), router);
    }

    private GetDiscoItemsRequest(final JID jid, final String node, IQRouter router) {
        this(jid, router);
        getPayloadGeneric().setNode(node);
    }

}
