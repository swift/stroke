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
import java.util.Date;
import java.util.TimeZone;

public class VCardMemoryStorage extends VCardStorage {
    public VCardMemoryStorage(CryptoProvider crypto) {
        super(crypto);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private Map<JID, VCard> vcards = new HashMap<JID, VCard>();
    private Map<JID, Date> vcardWriteTimes = new HashMap<JID, Date>();

    @Override
    public VCard getVCard(JID jid) {
        return vcards.get(jid);
    }

    /**
    * @param JID jid.
    * @return Date, May be Null.
    */
    @Override    
    public Date getVCardWriteTime(final JID jid) {
        return vcardWriteTimes.get(jid);
    }

    @Override
    public void setVCard(JID jid, VCard vcard) {
        vcards.put(jid, vcard);
        vcardWriteTimes.put(jid, new Date());
    }

}
