/*  Copyright (c) 2013, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written licence agreement from Isode Limited,
 *  or a written licence from an organisation licensed by Isode Limited Limited
 *  to grant such a licence.
 *
 */
 
package com.isode.stroke.tls;

import java.security.cert.X509Certificate;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.tls.java.CAPIConstants;

/**
 * CAPICertificate objects refer to certificate/key pairs that are held by
 * CAPI. A CAPICertificate itself doesn't have any key information inside
 * it. It doesn't make sense to use these on platforms other than Windows.
 */
public class CAPICertificate extends CertificateWithKey {

   
    private X509Certificate x509Certificate = null;
    private String keyStoreName = null;
    
    @Override
    public boolean isNull() {
        return (x509Certificate == null);
    }

    /**
     * Construct a new object. Note that the constructor does not perform any 
     * checking that the specified certificate exists or is usable. Such a 
     * check will take place if/when the certificate and key are needed (for
     * example, to establish a TLS connection), and it will be at this stage 
     * that any prompts may appear to insert a smartcard or enter a PIN etc..
     *  
     * 
     * @param x509Certificate an X509Certificate corresponding to a certificate
     * that is available in certificate object which has been read from
     * CAPI. Must not be null.
     * 
     * @param keyStoreName the name of the Windows keystore containing this
     * certificate. This may be null, in which case a search will be made of
     * all the stores named in {@link CAPIConstants#knownSunMSCAPIKeyStores}
     * and the first match used.
     */
    public CAPICertificate(X509Certificate x509Certificate, String keyStoreName) {
        NotNull.exceptIfNull(x509Certificate,"x509Certificate"); 
        this.x509Certificate = x509Certificate;
        this.keyStoreName = keyStoreName;
    }
    
    @Override
    public String toString() {
        return "CAPICertificate in " + 
                (keyStoreName == null ? "unspecified keystore" : keyStoreName) +
                 " for " + x509Certificate.getSubjectDN();
    }
    
    /**
     * Return the X509Certificate associated with this object
     * @return the X509Certificate, which will never be null.
     */
    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
    
    /**
     * Return the name of the KeyStore associated with this object, if any.
     * @return the KeyStore name, which may be null
     */
    public String getKeyStoreName() {
        return keyStoreName;
    }

}
