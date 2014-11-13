/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.vcards;

import java.util.HashSet;
import java.util.Set;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Slot2;

public class VCardManager {
    private final JID ownJID;
    private final IQRouter iqRouter;
    private final VCardStorage storage;
    private final Set<JID> requestedVCards = new HashSet<JID>();

    /**
     * The JID will always be bare.
     */
    public final Signal2<JID, VCard> onVCardChanged = new Signal2<JID, VCard>();

    /**
     * Emitted when our own vcard changes.
     *
     * onVCardChanged will also be emitted.
     */
    public final Signal1<VCard> onOwnVCardChanged = new Signal1<VCard>();

    public VCardManager(final JID ownJID, IQRouter iqRouter, VCardStorage vcardStorage) {
        this.ownJID = ownJID;
        this.iqRouter = iqRouter;
        this.storage = vcardStorage;
    }

    void delete() {
    }

    public VCard getVCard(final JID jid) {
        return storage.getVCard(jid);
    }

    public VCard getVCardAndRequestWhenNeeded(final JID jid) {
        VCard vcard = storage.getVCard(jid);
        if (vcard == null) {
            requestVCard(jid);
        }
        return vcard;
    }

    void requestVCard(final JID requestedJID) {
        final JID jid = requestedJID.compare(ownJID, JID.CompareType.WithoutResource) == 0 ? new JID() : requestedJID;
        if (requestedVCards.contains(jid)) {
            return;
        }
        GetVCardRequest request = GetVCardRequest.create(jid, iqRouter);
        request.onResponse.connect(new Slot2<VCard, ErrorPayload>() {
                @Override
                public void call(VCard p1, ErrorPayload p2) {
                    handleVCardReceived(jid, p1, p2);
                }
            });
        request.send();
        requestedVCards.add(jid);
    }

    public void requestOwnVCard() {
        requestVCard(new JID());
    }


    void handleVCardReceived(final JID actualJID, VCard vcard, ErrorPayload error) {
        if (error != null || vcard == null) {
            vcard = new VCard();
        }
        requestedVCards.remove(actualJID);
        JID jid = actualJID.isValid() ? actualJID : ownJID.toBare();
        setVCard(jid, vcard);
    }

    SetVCardRequest createSetVCardRequest(final VCard vcard) {
        SetVCardRequest request = SetVCardRequest.create(vcard, iqRouter);
        request.onResponse.connect(new Slot2<VCard, ErrorPayload>() {
                @Override
                public void call(VCard p1, ErrorPayload p2) {
                    handleSetVCardResponse(vcard, p2);
                }
            });
        return request;
    }

    void handleSetVCardResponse(VCard vcard, ErrorPayload error) {
        if (error == null) {
            setVCard(ownJID.toBare(), vcard);
        }
    }

    void setVCard(final JID jid, VCard vcard) {
        storage.setVCard(jid, vcard);
        onVCardChanged.emit(jid, vcard);
        if (jid.compare(ownJID, JID.CompareType.WithoutResource) == 0) {
            onOwnVCardChanged.emit(vcard);
        }
    }

//	String getPhotoHash(final JID jid) {
//		return storage.getPhotoHash(jid);
//	}
}
