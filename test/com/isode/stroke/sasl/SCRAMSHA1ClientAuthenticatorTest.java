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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kev
 */
public class SCRAMSHA1ClientAuthenticatorTest {

    @Test
    public void testGetInitialResponse() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH");
        testling.setCredentials("user", "pass", "");

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("n,,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_UsernameHasSpecialChars() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH");
        testling.setCredentials(",us=,er=", "pass", "");

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("n,,n==2Cus=3D=2Cer=3D,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithAuthorizationID() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH");
        testling.setCredentials("user", "pass", "auth");

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("n,a=auth,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithAuthorizationIDWithSpecialChars() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH");
        testling.setCredentials("user", "pass", "a=u,th");

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("n,a=a=3Du=2Cth,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithoutChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", false);
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setCredentials("user", "pass", "");

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("y,,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetInitialResponse_WithChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefghABCDEFGH", true);
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setCredentials("user", "pass", "");

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("p=tls-unique,,n=user,r=abcdefghABCDEFGH"), response);
    }

    @Test
    public void testGetFinalResponse() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");
        assertTrue(testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096")));

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("c=biws,r=abcdefghABCDEFGH,p=CZbjGDpIteIJwQNBgO0P8pKkMGY="), response);
    }

    @Test
    public void testGetFinalResponse_WithoutChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", false);
        testling.setCredentials("user", "pass", "");
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("c=eSws,r=abcdefghABCDEFGH,p=JNpsiFEcxZvNZ1+FFBBqrYvYxMk="), response);
    }

    @Test
    public void testGetFinalResponse_WithChannelBindingWithTLSChannelBindingData() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh", true);
        testling.setCredentials("user", "pass", "");
        testling.setTLSChannelBindingData(new ByteArray("xyza"));
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        ByteArray response = testling.getResponse();

        assertEquals(new ByteArray("c=cD10bHMtdW5pcXVlLCx4eXph,r=abcdefghABCDEFGH,p=i6Rghite81P1ype8XxaVAa5l7v0="), response);
    }

    @Test
    public void testSetFinalChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        boolean result = testling.setChallenge(new ByteArray("v=Dd+Q20knZs9jeeK0pi1Mx1Se+yo="));

        assertTrue(result);
    }

    @Test
    public void testSetChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        assertTrue(result);
    }

    @Test
    public void testSetChallenge_InvalidClientNonce() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefgiABCDEFGH,s=MTIzNDU2NzgK,i=4096"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_OnlyClientNonce() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefgh,s=MTIzNDU2NzgK,i=4096"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_InvalidIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=bla"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_MissingIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_ZeroIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=0"));

        assertTrue(!result);
    }

    @Test
    public void testSetChallenge_NegativeIterations() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");

        boolean result = testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=-1"));

        assertTrue(!result);
    }

    @Test
    public void testSetFinalChallenge_InvalidChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));
        boolean result = testling.setChallenge(new ByteArray("v=e26kI69ICb6zosapLLxrER/631A="));

        assertTrue(!result);
    }

    @Test
    public void testGetResponseAfterFinalChallenge() {
        SCRAMSHA1ClientAuthenticator testling = new SCRAMSHA1ClientAuthenticator("abcdefgh");
        testling.setCredentials("user", "pass", "");
        testling.setChallenge(new ByteArray("r=abcdefghABCDEFGH,s=MTIzNDU2NzgK,i=4096"));
        testling.setChallenge(new ByteArray("v=Dd+Q20knZs9jeeK0pi1Mx1Se+yo="));

        assertTrue(testling.getResponse() == null);
    }
}
