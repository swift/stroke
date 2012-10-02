/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
package com.isode.stroke.queries;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;

public abstract class GetResponder<T extends Payload> extends Responder<T> {

    public GetResponder(T t, IQRouter router) {
        super(t, router);
    }

    @Override
    protected boolean handleSetRequest(final JID from, final JID to, final String id, final T payload) {
        return false;
    }
};
