/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
package com.isode.stroke.queries.responders;

import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GetResponder;
import com.isode.stroke.queries.IQRouter;

public class SoftwareVersionResponder extends GetResponder<SoftwareVersion> {

    public SoftwareVersionResponder(IQRouter router) {
        super(new SoftwareVersion(), router);
    }

    public void setVersion(final String client, final String version) {
        setVersion(client, version, "");
    }

    public void setVersion(final String client, final String version, final String os) {
        this.client = client;
        this.version = version;
        this.os = os;
    }

    @Override
    public boolean handleGetRequest(final JID from, final JID to, final String id, SoftwareVersion payload) {
        sendResponse(from, id, new SoftwareVersion(client, version, os));
        return true;
    }
    private String client = "";
    private String version = "";
    private String os = "";
}