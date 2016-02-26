/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.tls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.tls.java.JavaCertificate;

public class JavaCertificateFactory implements CertificateFactory {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    private final java.security.cert.CertificateFactory jvmCertFactory;

    public JavaCertificateFactory() {
        java.security.cert.CertificateFactory temp = null;
        try {
            temp = java.security.cert.CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            logger.log(Level.WARNING,"Unable to generate X509 certificate factory",e);
            temp = null;
        }
        jvmCertFactory = temp;
    }
    
    @Override
    public Certificate createCertificateFromDER(ByteArray der) {
        if (jvmCertFactory == null) {
            return null;
        }
        InputStream derInputStream = new ByteArrayInputStream(der.getData());
        try {
            X509Certificate x509Cert = (X509Certificate)jvmCertFactory.generateCertificate(derInputStream);
            return new JavaCertificate(x509Cert);
        } catch (CertificateException e) {
            logger.log(Level.WARNING,"Unable to generate certificate from byte array "+der,e);
            return null;
        } catch (ClassCastException e) {
            // Should not get here as factory should return an x509 certificate
            logger.log(Level.WARNING,"Unable to generate X509 certificate",e);
            return null;
        }
    }

}
