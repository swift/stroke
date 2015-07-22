/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.sasl;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.ICUConverter;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;

/**
 *
 * @author Kev
 */
public class SCRAMSHA1ClientAuthenticatorTest {

    private IDNConverter idnConverter;
    private CryptoProvider crypto;

    @Before
    public void setUp() {
        idnConverter = new ICUConverter();
        crypto = new JavaCryptoProvider();
    }

    @Test
    public void testGetInitialResponse() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("n,,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_UsernameHasSpecialChars() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", false, idnConverter, crypto);
        testling.setCredentials(",us=,er=", new SafeByteArray("pass"), "");

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("n,,n==2Cus=3D=2Cer=3D,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithAuthorizationID() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "auth");

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("n,a=auth,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithAuthorizationIDWithSpecialChars() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "a=u,th");

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("n,a=a=3Du=2Cth,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithoutChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", false, idnConverter, crypto);
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("y,,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", true, idnConverter, crypto);
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("p=tls-unique,,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetFinalResponse() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");
        assertTrue(testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096")));

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("c=biws,r=abcdefghABCDEFGH,p=CZbjGDpIteIJwQNBgO0P8pKkMGY="), response);
    }

    @Test
    public void testGetFinalResponse_WithoutChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("c=eSws,r=abcdefghABCDEFGH,p=JNpsiFEcxZvNZ1+FFBBqrYvYxMk="), response);
    }

    @Test
    public void testGetFinalResponse_WithChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", true, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        SafeByteArray response = testling.getResponse();

        assertEquals(new SafeByteArray("c=cD10bHMtdW5pcXVlLCx4eXph,r=abcdefghABCDEFGH,p=i6Rghite81P1ype8XxaVAa5l7v0="), response);
    }

    @Test
    public void testSetFinalChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        boolean result = testling.setChallenge(new ByteArray("v=Dd+Q20knZs9jeeK0pi1Mx1Se+yo="));

        assertTrue(result);
    }

    @Test
    public void testSetChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        assertTrue(result);
    }

    @Test
    public void testSetChallenge_InvalidClientNonce() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefgiABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_OnlyClientNonce() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefgh,s=MTIzNDU2NzgK,i=4096"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_InvalidIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=bla"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_MissingIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_ZeroIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=0"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_NegativeIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=-1"));

        assertTrue(!result);
    }

    @Test
    public void testSetFinalChallenge_InvalidChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));
        boolean result = testling.setChallenge(new ByteArray("v=e26kI69ICb6zosapLLxrER/631A="));

        assertTrue(!result);
    }

    @Test
    public void testGetResponseAfterFinalChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false, idnConverter, crypto);
        testling.setCredentials("user", new SafeByteArray("pass"), "");
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));
        testling.setChallenge(new ByteArray("v=Dd+Q20knZs9jeeK0pi1Mx1Se+yo="));

        assertTrue(testling.getResponse() == null);
    }
}
