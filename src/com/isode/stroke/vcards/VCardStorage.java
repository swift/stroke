/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.vcards;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.stringcodecs.Hexify;

public abstract class VCardStorage {
	private CryptoProvider crypto;

	public abstract VCard getVCard(JID jid);
	public abstract void setVCard(JID jid, VCard vcard);

	public VCardStorage(CryptoProvider crypto) {
		this.crypto = crypto;
	}

	public void delete() {};

	public String getPhotoHash(final JID jid) {
		VCard vCard = getVCard(jid);
		if (vCard != null && vCard.getPhoto().getSize() != 0) {
			return Hexify.hexify(crypto.getSHA1Hash(vCard.getPhoto()));
		}
		else {
			return "";
		}
	}
}
