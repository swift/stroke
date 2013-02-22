/*  Copyright (c) 2013, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written licence agreement from Isode Limited,
 *  or a written licence from an organisation licensed by Isode Limited Limited
 *  to grant such a licence.
 *
 */
 
package com.isode.stroke.tls.java;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

import com.isode.stroke.base.NotNull;

/**
 * This class is used to provide a way of overriding the behaviour of a KeyManager
 * returned from SunMSCAPI.
 * <p>Specifically, this implementation allows callers to specify what should
 * be returned by {@link #chooseEngineClientAlias(String[], Principal[], SSLEngine)
 */
public class CAPIKeyManager extends X509ExtendedKeyManager {

    X509ExtendedKeyManager parentKeyManager = null;
    String engineClientAlias = null;
    
    /**
     * Create a new object. 
     * @param parent the actual X509ExtendedKeyManager to which work will
     * be delegated unless overridden by caller-specified values. Must
     * not be null.
     */
    public CAPIKeyManager(X509ExtendedKeyManager parent) {
        NotNull.exceptIfNull(parent,"parent"); 
        this.parentKeyManager = parent;
    }
    
    /**
     * Set the value which should be returned by
     * {@link #chooseEngineClientAlias(String[], Principal[], SSLEngine)}.
     * 
     * <p>The default behaviour of the SunMSCAPI KeyManager is to pick what it
     * thinks is the most suitable client certificate for the session. 
     * However, this may not be the same as the certificate which was specified
     * by the client. This method allows callers to override the default 
     * behaviour and force a specific certificate to be used.
     * 
     * @param engineClientAlias the alias of an entry in the KeyStore. This
     * may be null, in which case when
     * {@link #chooseEngineClientAlias(String[], Principal[], SSLEngine) is
     * called, it will return whatever value the original KeyManager returns.
     */
    public void setEngineClientAlias(String engineClientAlias) {
        this.engineClientAlias = engineClientAlias;
    }
    
    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return parentKeyManager.getServerAliases(keyType, issuers);
    }
    
    @Override
    public PrivateKey getPrivateKey(String alias) {
        return parentKeyManager.getPrivateKey(alias);
    }
    
    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return parentKeyManager.getClientAliases(keyType, issuers);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return parentKeyManager.getCertificateChain(alias);
    }
    
    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers,
            Socket socket) {
        return parentKeyManager.chooseServerAlias(keyType, issuers, socket);

    }
    
    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers,
            Socket socket) {
        return parentKeyManager.chooseClientAlias(keyType, issuers, socket);
    }
    
    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        if (engineClientAlias != null) {
            return engineClientAlias;
        }
        return parentKeyManager.chooseEngineClientAlias(keyType, issuers, engine);
    }
    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        return parentKeyManager.chooseEngineServerAlias(keyType, issuers, engine);

    }        

}
