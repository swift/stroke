/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.queries;

import java.util.Vector;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.jid.JID;
import java.util.Collections;

/**
 * This class is responsible for routing all IQ stanzas to the handlers. It's
 * needed because, unlike Message and Presence, there are strict rules about
 * replying to IQ stanzas, replies need to be tracked, handled by the
 * responsible bit of code, replied to, etc. and when it's an outgoing IQ, it
 * needs to be tracked such that when the reply comes in, the callback is
 * called.
 */
public class IQRouter {

    private Vector<IQHandler> handlers_ = new Vector<IQHandler>();
    private IQChannel channel_;
    private JID jid_ = new JID();
    private JID from_ = new JID();
    private Vector<IQHandler> queuedRemoves_ = new Vector<IQHandler>();
    private boolean queueRemoves_;

    public IQRouter(IQChannel channel) {
        channel_ = channel;
        queueRemoves_ = false;
        channel_.onIQReceived.connect(new Slot1<IQ>() {

            public void call(IQ p1) {
                handleIQ(p1);
            }
        });
    }

    public void addHandler(IQHandler handler) {
        synchronized (handlers_) {
            handlers_.add(handler);
        }
    }

    public void removeHandler(IQHandler handler) {
        if (queueRemoves_) {
            queuedRemoves_.add(handler);
        }
        else {
            synchronized (handlers_) {
                handlers_.remove(handler);
            }
        }
    }

    public void sendIQ(IQ iq) {
        if (from_.isValid() && !iq.getFrom().isValid()) {
            iq.setFrom(from_);
        }
        channel_.sendIQ(iq);
    }

    public String getNewIQID() {
        return channel_.getNewIQID();
    }

    public boolean isAvailable() {
        return channel_.isAvailable();
    }

	/**
	 * Checks whether the given jid is the account JID (i.e. it is either
	 * the bare JID, or it is the empty JID).
	 * Can be used to check whether a stanza is sent by the server on behalf
	 * of the user's account.
	 */
    public boolean isAccountJID(final JID jid) {
		return jid.isValid() ? jid_.toBare().compare(jid, JID.CompareType.WithResource) == 0 : true;
	}

	private void handleIQ(IQ iq) {
        queueRemoves_ = true;
        boolean handled = false;
        Vector<IQHandler> i = new Vector<IQHandler>();
        i.addAll(handlers_);
        Collections.reverse(i);
        synchronized (handlers_) {
            for (IQHandler handler : i) {
                handled |= handler.handleIQ(iq);
                if (handled) {
                    break;
                }
            }
        }
        if (!handled && (iq.getType().equals(IQ.Type.Get) || iq.getType().equals(IQ.Type.Set))) {
            sendIQ(IQ.createError(iq.getFrom(), iq.getID(), ErrorPayload.Condition.FeatureNotImplemented, ErrorPayload.Type.Cancel));
        }

        processPendingRemoves();
        queueRemoves_ = false;
    }

    public void processPendingRemoves() {
        synchronized(handlers_) {
            for(IQHandler handler : queuedRemoves_) {
                handlers_.remove(handler);
            }
        }
        queuedRemoves_.clear();
    }

    /**
     * Sets the JID of this IQ router.
     *
     * This JID is used by requests to check whether incoming results
     * are addressed correctly
     * @param jid the JID
     */
    public void setJID(final JID jid) {
	jid_ = jid;
    }

    public JID getJID() {
	return jid_;
    }

    /**
     * Sets the 'from' JID for all outgoing IQ stanzas.
     *
     * By default, IQRouter does not add a from to IQ stanzas, since
     * this is automatically added by the server. This overrides this
     * default behavior, which is necessary for e.g. components.
     */
    public void setFrom(final JID from) {
        from_ = from;
    }
}
