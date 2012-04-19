/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon
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
import com.isode.stroke.signals.Signal2;

/**
 * Class representing a request to get a private storage
 *
 * @param <T> type extending {@link Payload}
 */
public class GetPrivateStorageRequest <T extends Payload> extends Request {    

    /**
     * Create the request
     * @param <T> Payload Type
     * @param payload object of type payload
     * @param router IQ router
     * @return request to get Private storage
     */
    public static<T extends Payload> GetPrivateStorageRequest<T> create(T payload,IQRouter router) {
        return new GetPrivateStorageRequest<T>(payload,router);
    }

    private GetPrivateStorageRequest(T payload,IQRouter router) {
        super(IQ.Type.Get,new JID(),new PrivateStorage(payload),router);
    }

    @Override
    public void handleResponse(Payload payload, ErrorPayload error) {
        PrivateStorage storage = null;
        if(payload instanceof PrivateStorage) {
            storage = (PrivateStorage)payload;
        }
        if (storage != null) {
            onResponse.emit((T) storage.getPayload(), error);
        }
        else {
            onResponse.emit(null, error);
        }
    }

    /**
     * Siganl to be notified on receiving the response for the request sent 
     */
    public Signal2<T, ErrorPayload> onResponse = new Signal2<T, ErrorPayload>();
}
