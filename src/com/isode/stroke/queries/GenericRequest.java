/*
 * Copyright (c) 2010, 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.queries;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal2;

/**
 * IQ Request for standard payload.
 */
public class GenericRequest<T extends Payload> extends Request {

    public Signal2<T, ErrorPayload> onResponse = new Signal2<T, ErrorPayload>();

    public GenericRequest(IQ.Type type, JID receiver, Payload payload, IQRouter router) {
        super(type, receiver, payload, router);
    }

    @Override
    protected void handleResponse(Payload payload, ErrorPayload error) {
        T genericPayload = null;
        try {
            genericPayload = (T)payload;
        } catch (Exception ex) {
            /* This isn't legal XMPP, so treat as NULL.*/
        }
        onResponse.emit(genericPayload, error);
    }

    protected T getPayloadGeneric() {
        return (T)getPayload();
    }

}
