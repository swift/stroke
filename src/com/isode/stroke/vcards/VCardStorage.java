/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.vcards;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.stringcodecs.Hexify;
import java.util.Date;

public abstract class VCardStorage {
	private CryptoProvider crypto;

	public abstract VCard getVCard(JID jid);
	public abstract Date getVCardWriteTime(JID jid);
	public abstract void setVCard(JID jid, VCard vcard);

	public VCardStorage(CryptoProvider crypto) {
		this.crypto = crypto;
	}

	public void delete() {};

	public String getPhotoHash(final JID jid) {
		final VCard vCard = getVCard(jid);
		if (vCard != null) {
			final ByteArray photo = vCard.getPhoto();
			if (photo != null) {
				return Hexify.hexify(crypto.getSHA1Hash(photo));
			}
		}
		return "";
	}
}
