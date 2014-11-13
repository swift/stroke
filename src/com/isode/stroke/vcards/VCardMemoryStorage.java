/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.vcards;

import java.util.HashMap;
import java.util.Map;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;

public class VCardMemoryStorage extends VCardStorage {
    public VCardMemoryStorage(CryptoProvider crypto) {
        super(crypto);
    }

    private Map<JID, VCard> vcards = new HashMap<JID, VCard>();

    @Override
    public VCard getVCard(JID jid) {
        return vcards.get(jid);
    }

    @Override
    public void setVCard(JID jid, VCard vcard) {
        vcards.put(jid, vcard);
    }

}
