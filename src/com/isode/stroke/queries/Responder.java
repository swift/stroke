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

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;

/**
 * A class for handling incoming IQ Get and Set requests of a specific payload
 * type.
 *
 * Concrete subclasses of this class need to implement handleGetRequest() and
 * handleSetRequest() to implement the behavior of the responder.
 *
 * \tparam PAYLOAD_TYPE The type of payload this Responder handles. Only IQ
 * requests containing this payload type will be passed to handleGetRequest()
 * and handleSetRequest()
 */
public abstract class Responder<PAYLOAD_TYPE extends Payload> implements IQHandler {

    public Responder(final PAYLOAD_TYPE payloadType, IQRouter router) {
        payloadType_ = payloadType;
        router_ = router;
        isFinalResonder_ = true;
    }

    /**
     * Starts the responder.
     *
     * After the responder has started, it will start receiving and responding
     * to requests.
     *
     * \see stop()
     */
    public void start() {
        router_.addHandler(this);
    }

    /**
     * Stops the responder.
     *
     * When the responder is stopped, it will no longer receive incoming
     * requests.
     *
     * \see start()
     */
    public void stop() {
        router_.removeHandler(this);
    }

    /**
     * Handle an incoming IQ-Get request containing a payload of class
     * PAYLOAD_TYPE.
     *
     * This method is implemented in the concrete subclasses.
     */
    protected abstract boolean handleGetRequest(final JID from, final JID to, final String id, PAYLOAD_TYPE payload);

    /**
     * Handle an incoming IQ-Set request containing a payload of class
     * PAYLOAD_TYPE.
     *
     * This method is implemented in the concrete subclasses.
     */
    protected abstract boolean handleSetRequest(final JID from, final JID to, final String id, PAYLOAD_TYPE payload);

    /**
     * Convenience function for sending an IQ response.
     */
    protected void sendResponse(final JID to, final String id, PAYLOAD_TYPE payload) {
        router_.sendIQ(IQ.createResult(to, id, payload));
    }

    /**
     * Convenience function for sending an IQ response, with a specific from
     * address.
     */
    protected void sendResponse(final JID to, final JID from, final String id, PAYLOAD_TYPE payload) {
        router_.sendIQ(IQ.createResult(to, from, id, payload));
    }

    /**
     * Convenience function for responding with an error.
     */
    protected void sendError(final JID to, final String id, ErrorPayload.Condition condition, ErrorPayload.Type type, Payload payload) {
        router_.sendIQ(IQ.createError(to, id, condition, type, payload));
    }

    protected void sendError(final JID to, final String id, ErrorPayload.Condition condition, ErrorPayload.Type type) {
        sendError(to, id, condition, type, null);
    }

    /**
     * Convenience function for responding with an error from a specific from
     * address.
     */
    protected void sendError(final JID to, final JID from, final String id, ErrorPayload.Condition condition, ErrorPayload.Type type, Payload payload) {
        router_.sendIQ(IQ.createError(to, from, id, condition, type, payload));
    }

    protected void sendError(final JID to, final JID from, final String id, ErrorPayload.Condition condition, ErrorPayload.Type type) {
        sendError(to, from, id, condition, type, null);
    }

    protected IQRouter getIQRouter() {
        return router_;
    }

    protected void setFinal(boolean isFinal) {
        isFinalResonder_ = isFinal;
    }

    @Override
    public boolean handleIQ(IQ iq) {
        if (IQ.Type.Set.equals(iq.getType()) || IQ.Type.Get.equals(iq.getType())) {
            PAYLOAD_TYPE payload = iq.getPayload(payloadType_);
            if (payload != null) {
                boolean result;
                if (IQ.Type.Set.equals(
                        iq.getType())) {
                    result = handleSetRequest(iq.getFrom(), iq.getTo(), iq.getID(), payload);
                } else {
                    result = handleGetRequest(iq.getFrom(), iq.getTo(), iq.getID(), payload);
                }
                if (!result) {
                    if(isFinalResonder_) {
                        router_.sendIQ(IQ.createError(iq.getFrom(), iq.getID(), ErrorPayload.Condition.NotAllowed, ErrorPayload.Type.Cancel));
                    } else {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    private IQRouter router_;
    private PAYLOAD_TYPE payloadType_;
    private boolean isFinalResonder_;
};
