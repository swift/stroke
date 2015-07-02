/*
 * Copyright (c) 2010-2014, Isode Limited, London, England.
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
import java.util.logging.Logger;

/**
 * Base class for IQ requests.
 */
public abstract class Request implements IQHandler {
    protected final Type type_;
    protected final IQRouter router_;
    protected final JID receiver_;
	protected final JID sender_;    
    private boolean sent_;
    private Payload payload_;
    private String id_;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructs a request of a certain type to a specific receiver.
	 */
    public Request(IQ.Type type, JID receiver, IQRouter router) {
        this(type, null, receiver, null, router);
    }

	/**
	 * Constructs a request of a certain type to a specific receiver, and attaches the given
	 * payload.
	 */
    public Request(IQ.Type type, JID receiver, Payload payload, IQRouter router) {
        this(type, null, receiver, payload, router);
    }

	/**
	 * Constructs a request of a certain type to a specific receiver from a specific sender.
	 */
    public Request(IQ.Type type, JID sender, JID receiver, IQRouter router) {
    	this(type, sender, receiver, null, router);
    }

	/**
	 * Constructs a request of a certain type to a specific receiver from a specific sender, and attaches the given
	 * payload.
	 */
    public Request(IQ.Type type, JID sender, JID receiver, Payload payload, IQRouter router) {
        type_ = type;
        router_ = router;
        receiver_ = receiver;
        payload_ = payload;
        sender_ = sender;
        sent_ = false;
    }

    public String send() {
        assert payload_ != null;
		assert !sent_;
		sent_ = true;

		IQ iq = new IQ(type_);
		iq.setTo(receiver_);
		iq.setFrom(sender_);
		iq.addPayload(payload_);
		id_ = router_.getNewIQID();
		iq.setID(id_);

		router_.addHandler(this);

		router_.sendIQ(iq);
    	return id_;
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
        if (iq.getType() == IQ.Type.Result || iq.getType() == IQ.Type.Error) {
	        if (sent_ && iq.getID().equals(id_)) {
		    	if (isCorrectSender(iq.getFrom())) {
			
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
			}
		}
        return handled;
    }

    private boolean isCorrectSender(final JID jid) {
		if (isAccountJID(receiver_)) {
	    	return isAccountJID(jid);
		}
		return (jid.compare(receiver_, JID.CompareType.WithResource) == 0);
    }

    private boolean isAccountJID(final JID jid) {
		// If the router's JID is not set, we don't check anything
		if (!router_.getJID().isValid()) {
		    return true;
		}

		return jid.isValid() ?
		    router_.getJID().compare(jid, JID.CompareType.WithoutResource) == 0 : true;
    }
	       
	public JID getReceiver() {
		return receiver_;
	}

	/**
	 * Returns the ID of this request.
	 * This will only be set after send() is called.
	 */
	public String getID() {
		return id_;
	}
}