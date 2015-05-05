/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import java.util.HashMap;
import java.util.Map;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GetResponder;
import com.isode.stroke.queries.IQRouter;

public class DiscoInfoResponder extends GetResponder<DiscoInfo> {
    private DiscoInfo info_ = new DiscoInfo();
    private Map<String, DiscoInfo> nodeInfo_ = new HashMap<String, DiscoInfo>();

    public DiscoInfoResponder(IQRouter router) {
        super(new DiscoInfo(), router);
    }

    void clearDiscoInfo() {
        info_ = new DiscoInfo();
        nodeInfo_.clear();
    }

    void setDiscoInfo(final DiscoInfo info) {
        info_ = info;
    }

    void setDiscoInfo(final String node, final DiscoInfo info) {
        DiscoInfo newInfo = info;
        newInfo.setNode(node);
        nodeInfo_.put(node, newInfo);
    }

    protected boolean handleGetRequest(final JID from, final JID j, final String id, DiscoInfo info) {
        if (info.getNode().isEmpty()) {
            sendResponse(from, id, info_);
        }
        else {
            DiscoInfo i = nodeInfo_.get(info.getNode());
            if (i != null) {
                sendResponse(from, id, i);
            }
            else {
                sendError(from, id, ErrorPayload.Condition.ItemNotFound, ErrorPayload.Type.Cancel);
            }
        }
        return true;
    }
}
