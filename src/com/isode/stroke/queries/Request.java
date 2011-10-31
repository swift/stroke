/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.queries;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.IQ.Type;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;

/**
 * Base class for IQ requests.
 */
public abstract class Request implements IQHandler {
    protected final Type type_;
    protected final IQRouter router_;
    protected final JID receiver_;
    private boolean sent_;
    private Payload payload_;
    private String id_;

    public Request(IQ.Type type, JID receiver, IQRouter router) {
        this(type, receiver, null, router);
    }

    public Request(IQ.Type type, JID receiver, Payload payload, IQRouter router) {
        type_ = type;
        router_ = router;
        receiver_ = receiver;
        payload_ = payload;
        sent_ = false;
    }

    public void send() {
        assert payload_ != null;
	assert !sent_;
	sent_ = true;

	IQ iq = new IQ(type_);
	iq.setTo(receiver_);
	iq.addPayload(payload_);
	id_ = router_.getNewIQID();
	iq.setID(id_);

	router_.addHandler(this);

	router_.sendIQ(iq);
    }

    protected void setPayload(Payload payload) {
        payload_ = payload;
    }

    protected Payload getPayload() {
        return payload_;
    }

    protected abstract void handleResponse(Payload payload, ErrorPayload error);

    public boolean handleIQ(IQ iq) {
        boolean handled = false;
        if (sent_ && iq.getID().equals(id_)) {
            if (iq.getType().equals(IQ.Type.Result)) {
                handleResponse(iq.getPayload(payload_), null);
            } else {
                ErrorPayload errorPayload = iq.getPayload(new ErrorPayload());
                if (errorPayload != null) {
                    handleResponse(null, errorPayload);
                } else {
                    handleResponse(null, new ErrorPayload(ErrorPayload.Condition.UndefinedCondition));
                }
            }
            router_.removeHandler(this);
            handled = true;
        }
        return handled;
    }

}