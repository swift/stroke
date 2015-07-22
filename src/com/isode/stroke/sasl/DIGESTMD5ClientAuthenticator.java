/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.sasl;

import com.isode.stroke.sasl.ClientAuthenticator;
import com.isode.stroke.sasl.DIGESTMD5Properties;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.stringcodecs.Hexify;

public class DIGESTMD5ClientAuthenticator extends ClientAuthenticator {

	private enum Step {
		Initial,
		Response,
		Final
	};
	private Step step;
	private String host = "";
	private String cnonce = "";
	private CryptoProvider crypto;
	private DIGESTMD5Properties challenge = new DIGESTMD5Properties();

	public DIGESTMD5ClientAuthenticator(final String host, final String nonce, CryptoProvider crypto) {
		super("DIGEST-MD5");
		this.step = Step.Initial;
		this.host = host;
		this.cnonce = nonce;
		this.crypto = crypto;
	}

	public SafeByteArray getResponse() {
		if (Step.Initial.equals(step)) {
			return null;
		}
		else if (Step.Response.equals(step)) {
			String realm = "";
			if (challenge.getValue("realm") != null) {
				realm = challenge.getValue("realm");
			}
			String qop = "auth";
			String digestURI = "xmpp/" + host;
			String nc = "00000001";

			ByteArray A11 = crypto.getMD5Hash(new SafeByteArray().append(getAuthenticationID()).append(":").append(realm).append(":").append(getPassword()));
			ByteArray A12 = new ByteArray().append(":").append(challenge.getValue("nonce")).append(":").append(cnonce);
			// Compute the response value
			ByteArray A1 = A11.append(A12);
			if (!getAuthorizationID().isEmpty()) {
				A1.append(new ByteArray(":" + getAuthenticationID()));
			}
			ByteArray A2 = new ByteArray("AUTHENTICATE:" + digestURI);

			String responseValue = Hexify.hexify(crypto.getMD5Hash(new ByteArray(
				Hexify.hexify(crypto.getMD5Hash(A1)) + ":"
				+ challenge.getValue("nonce") + ":" + nc + ":" + cnonce + ":" + qop + ":"
				+ Hexify.hexify(crypto.getMD5Hash(A2)))));


			DIGESTMD5Properties response = new DIGESTMD5Properties();
			response.setValue("username", getAuthenticationID());
			if (!realm.isEmpty()) {
				response.setValue("realm", realm);
			}
			response.setValue("nonce", challenge.getValue("nonce"));
			response.setValue("cnonce", cnonce);
			response.setValue("nc", "00000001");
			response.setValue("qop", qop);
			response.setValue("digest-uri", digestURI);
			response.setValue("charset", "utf-8");
			response.setValue("response", responseValue);
			if (!getAuthorizationID().isEmpty()) {
				response.setValue("authzid", getAuthorizationID());
			}
			return new SafeByteArray(response.serialize());
		}
		else {
			return null;
		}
	}

	public boolean setChallenge(final ByteArray challengeData) {
		if (Step.Initial.equals(step)) {
			if (challengeData == null) {
				return false;
			}
			challenge = DIGESTMD5Properties.parse(challengeData);

			// Sanity checks
			if (challenge.getValue("nonce") == null) {
				return false;
			}
			if (challenge.getValue("charset") ==null || !(challenge.getValue("charset").equals("utf-8"))) {
				return false;
			}
			step = Step.Response;
			return true;
		}
		else {
			step = Step.Final;
			// TODO: Check RSPAuth
			return true;
		}
	}
}