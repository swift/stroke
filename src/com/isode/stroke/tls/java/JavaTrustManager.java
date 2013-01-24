/*  Copyright (c) 2012, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written licence agreement from Isode Limited,
 *  or a written licence from an organisation licensed by Isode Limited Limited
 *  to grant such a licence.
 *
 */
 
package com.isode.stroke.tls.java;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * A concrete X509TrustManager implementation which provides a trust manager 
 * based on the default java "pkcs12" keystore.
 */
public class JavaTrustManager implements X509TrustManager {
    
    /**
     * Construct a new object
     * @param jsseContext reference to JSSEContext; must not be null.
     * 
     * @throws SSLException if it was not possible to initialise the
     * TrustManager or KeyStore
     */
    JavaTrustManager(JSSEContext jsseContext) throws SSLException {

        if (jsseContext == null) {
            throw new NullPointerException("JSSEContext may not be null");
        }
        this.jsseContext = jsseContext;

        try {
            // create a "default" JSSE X509TrustManager.

            KeyStore ks = KeyStore.getInstance("PKCS12");
            /*

        // This is how you could load trust anchors
        ks.load(new FileInputStream("trustedCerts"),
            "passphrase".toCharArray());
             */
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            TrustManager tms [] = tmf.getTrustManagers();

            /*
             * Iterate over the returned trustmanagers, look
             * for an instance of X509TrustManager.  If found,
             * use that as our "default" trust manager.
             */
            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    pkixTrustManager = (X509TrustManager) tms[i];                                      
                    return;
                }
            }
            /*
             * Find some other way to initialize, or else we have to fail the
             * constructor.
             */
            throw new SSLException("Couldn't initialize");
        }
        catch (KeyStoreException e) {
            throw new SSLException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SSLException(e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // It's not expected that a Stroke application will ever be in the
        // position of checking client certificates.  Just delegate to
        // default trust manager
        pkixTrustManager.checkClientTrusted(chain, authType);

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        CertificateException certificateException = null; 


        // TODO: 
        // Note that we don't call the superclass method here yet, because
        // it will fail with like this until the TrustManagerFactory has
        // been initialised with a suitable list of trust anchors
        // java.lang.RuntimeException: Unexpected error: 
        // java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty
        
        /*
        try {
            pkixTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            certificateException = e;
        }
        catch (Exception e) {
            emitError(e,"checkServerTrusted failed");
        }
        */
        
        // TODO: The only type of verification done is the certificate validity.
        // Need to make "checkServerTrusted" do certificate verification properly
        // and pass in an appropriate CertificateException
        if (chain != null && chain.length > 0) {
            try {
                chain[0].checkValidity();
            }
            catch (CertificateException e) {
                certificateException = e;
            }
        }

        jsseContext.setPeerCertificateInfo(chain, certificateException);
        
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * The default PKIX X509TrustManager, to which decisions can be
     * delegated when we don't make them ourselves.
     */
    X509TrustManager pkixTrustManager;
    
    /**
     * The object who wants to know what server certificates appear 
     */
    JSSEContext jsseContext;
}
