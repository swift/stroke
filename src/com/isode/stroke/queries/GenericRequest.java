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

    /**
     * Create a request suitable for client use.
     * @param type Iq type - Get or Set.
     * @param receiver JID to send request to.
     * @param payload Payload to send in stanza.
     * @param router IQRouter instance for current connection.
     */
    public GenericRequest(IQ.Type type, JID receiver, Payload payload, IQRouter router) {
        super(type, receiver, payload, router);
    }

    /**
     * Create a request suitable for component or server use. As a client, use the other constructor instead.
     * @param type Iq type - Get or Set.
     * @param sender JID to use in "from" of stanza.
     * @param receiver JID to send request to.
     * @param payload Payload to send in stanza.
     * @param router IQRouter instance for current connection.
     */
    public GenericRequest(IQ.Type type, final JID sender, final JID receiver, Payload payload, IQRouter router) {
        super(type, sender, receiver, payload, router);
    }

    @Override
    public void handleResponse(Payload payload, ErrorPayload error) {
        T genericPayload = null;
        try {
            genericPayload = (T)payload;
        } catch (Exception ex) {
            /* This isn't legal XMPP, so treat as NULL.*/
        }
        onResponse.emit(genericPayload, error);
    }

    public T getPayloadGeneric() {
        return (T)getPayload();
    }

}
