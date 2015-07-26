/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.sasl;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.ibm.icu.text.StringPrepParseException;
import com.isode.stroke.stringcodecs.Base64;
import com.isode.stroke.stringcodecs.PBKDF2;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.crypto.CryptoProvider;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Map;

public class SCRAMSHA1ClientAuthenticator extends ClientAuthenticator {

    static String escape(String s) {
        String result = "";
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ',') {
                result += "=2C";
            } else if (s.charAt(i) == '=') {
                result += "=3D";
            } else {
                result += s.charAt(i);
            }
        }
        return result;
    }

    public SCRAMSHA1ClientAuthenticator(String nonce, boolean useChannelBinding, IDNConverter idnConverter, CryptoProvider crypto) {
        super(useChannelBinding ? "SCRAM-SHA-1-PLUS" : "SCRAM-SHA-1");
        step = Step.Initial;
        clientnonce = nonce;
        this.useChannelBinding = useChannelBinding;
        this.idnConverter = idnConverter;
        this.crypto = crypto;
    }

    public void setTLSChannelBindingData(ByteArray channelBindingData) {
        tlsChannelBindingData = channelBindingData;
    }

    public SafeByteArray getResponse() {
        if (step.equals(Step.Initial)) {
            return new SafeByteArray(getGS2Header().append(getInitialBareClientMessage()));
        } else if (step.equals(Step.Proof)) {
            ByteArray clientKey = crypto.getHMACSHA1(saltedPassword, new ByteArray("Client Key"));
            ByteArray storedKey = crypto.getSHA1Hash(clientKey);
            ByteArray clientSignature = crypto.getHMACSHA1(new SafeByteArray(storedKey), authMessage);
            ByteArray clientProof = clientKey;
            byte[] clientProofData = clientProof.getData();
            for (int i = 0; i < clientProofData.length; ++i) {
                clientProofData[i] ^= clientSignature.getData()[i];
            }
            clientProof = new ByteArray(clientProofData);
            ByteArray result = getFinalMessageWithoutProof().append(",p=").append(Base64.encode(clientProof));
            return new SafeByteArray(result);
        } else {
            return null;
        }
    }

    public boolean setChallenge(ByteArray challenge) {
        if (step.equals(Step.Initial)) {
            if (challenge == null) {
                return false;
            }
            initialServerMessage = challenge;

            Map<Character, String> keys = parseMap(initialServerMessage.toString());

            // Extract the salt
            ByteArray salt = Base64.decode(keys.get('s'));

            // Extract the server nonce
            String clientServerNonce = keys.get('r');
            if (clientServerNonce.length() <= clientnonce.length()) {
                return false;
            }
            String receivedClientNonce = clientServerNonce.substring(0, clientnonce.length());
            if (!receivedClientNonce.equals(clientnonce)) {
                return false;
            }
            serverNonce = new ByteArray(clientServerNonce.substring(clientnonce.length()));


            // Extract the number of iterations
            int iterations = 0;
            try {
                iterations = Integer.parseInt(keys.get('i'));
            } catch (NumberFormatException e) {
                return false;
            }
            if (iterations <= 0) {
                return false;
            }

            //Not Sure, why this here.
            ByteArray channelBindData = new ByteArray();
            if (useChannelBinding && tlsChannelBindingData != null) {
                channelBindData = tlsChannelBindingData;
            }

            // Compute all the values needed for the server signature
            try {
                saltedPassword = PBKDF2.encode(idnConverter.getStringPrepared(getPassword(), IDNConverter.StringPrepProfile.SASLPrep), salt, iterations, crypto);         
            } catch (StringPrepParseException e) {

            }
            authMessage = getInitialBareClientMessage().append(",").append(initialServerMessage).append(",").append(getFinalMessageWithoutProof());
            ByteArray serverKey = crypto.getHMACSHA1(saltedPassword, new ByteArray("Server Key"));
            serverSignature = crypto.getHMACSHA1(serverKey, authMessage);

            step = Step.Proof;
            return true;
        } else if (step.equals(step.Proof)) {
            ByteArray result = new ByteArray("v=").append(new ByteArray(Base64.encode(serverSignature)));
            step = Step.Final;
            return challenge != null && challenge.equals(result);
        } else {
            return true;
        }
    }

    private Map<Character, String> parseMap(String s) {
        HashMap<Character, String> result = new HashMap<Character, String>();
        if (s.length() > 0) {
            char key = '~'; /* initialise so it'll compile */
            String value = "";
            int i = 0;
            boolean expectKey = true;
            while (i < s.length()) {
                if (expectKey) {
                    key = s.charAt(i);
                    expectKey = false;
                    i++;
                } else if (s.charAt(i) == ',') {
                    result.put(key, value);
                    value = "";
                    expectKey = true;
                } else {
                    value += s.charAt(i);
                }
                i++;
            }
            result.put(key, value);
        }
        return result;
    }

    private ByteArray getInitialBareClientMessage() {
        String authenticationID = "";
        try {
            authenticationID = idnConverter.getStringPrepared(getAuthenticationID(), IDNConverter.StringPrepProfile.SASLPrep);
        } catch (StringPrepParseException e) {

        }
        return new ByteArray("n=" + escape(authenticationID) + ",r=" + clientnonce);
    }

    private ByteArray getGS2Header() {

        ByteArray channelBindingHeader = new ByteArray("n");
	if (tlsChannelBindingData != null) {
		if (useChannelBinding) {
			channelBindingHeader = new ByteArray("p=tls-unique");
		}
		else {
			channelBindingHeader = new ByteArray("y");
		}
	}
	return new ByteArray().append(channelBindingHeader).append(",").append(getAuthorizationID().isEmpty() ? new ByteArray() : new ByteArray("a=" + escape(getAuthorizationID()))).append(",");
    }

    private ByteArray getFinalMessageWithoutProof() {
        ByteArray channelBindData = new ByteArray();
	if (useChannelBinding && tlsChannelBindingData != null) {
		channelBindData = tlsChannelBindingData;
	}
	return new ByteArray("c=" + Base64.encode(new ByteArray(getGS2Header()).append(channelBindData)) + ",r=" + clientnonce).append(serverNonce);
    }

    private enum Step {

        Initial,
        Proof,
        Final
    };
    private Step step;
    private String clientnonce = "";
    private ByteArray initialServerMessage = new ByteArray();
    private ByteArray serverNonce = new ByteArray();
    private ByteArray authMessage = new ByteArray();
    private ByteArray saltedPassword = new ByteArray();
    private ByteArray serverSignature = new ByteArray();
    private boolean useChannelBinding;
    private ByteArray tlsChannelBindingData;
    private IDNConverter idnConverter;
    private CryptoProvider crypto;    
}
