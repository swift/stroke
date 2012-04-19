/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.queries.requests;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.PrivateStorage;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.Request;
import com.isode.stroke.signals.Signal1;

/**
 * Class representing a request to set a private storage
 *
 * @param <T> type extending {@link Payload}
 */
public class SetPrivateStorageRequest<T extends Payload> extends Request {

    /**
     * Create the request
     * @param <T> Payload Type
     * @param payload object of type payload
     * @param router IQ router
     * @return request to set Private storage
     */
    public static<T extends Payload> SetPrivateStorageRequest<T> create(T payload, IQRouter router) {
        return new SetPrivateStorageRequest<T>(payload, router);
    }

    private SetPrivateStorageRequest(T payload, IQRouter router)  {
        super(IQ.Type.Set, new JID(), new PrivateStorage(payload), router);
    }

    @Override
    public void handleResponse(Payload p, ErrorPayload error) {
        onResponse.emit(error);
    }

    public Signal1<ErrorPayload> onResponse = new Signal1<ErrorPayload>();
}
