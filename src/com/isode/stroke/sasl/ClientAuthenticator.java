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

public abstract class ClientAuthenticator {

    public ClientAuthenticator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCredentials(String authcid, String password) {
        setCredentials(authcid, password, "");
    }

    public void setCredentials(String authcid, String password, String authzid) {
        this.authcid = authcid;
        this.password = password;
        this.authzid = authzid;
    }

    public abstract ByteArray getResponse();

    public abstract boolean setChallenge(ByteArray challenge);

    public String getAuthenticationID() {
        return authcid;
    }

    public String getAuthorizationID() {
        return authzid;
    }

    public String getPassword() {
        return password;
    }
    private String name;
    private String authcid;
    private String password;
    private String authzid;
}
