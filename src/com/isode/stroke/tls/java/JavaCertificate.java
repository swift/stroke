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

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.tls.Certificate;

/**
 * This class wraps a java.security.cert.X509Certificate to fulfil
 * the requirements of the com.isode.stroke.tls.Certificate class.
 */
public class JavaCertificate extends Certificate {
    private X509Certificate x509Certificate;

    // There's no ASN.1 help for this in standard Java SDK so for the
    // moment we'll hard-code in the values
    /**
     * ASN.1 encoded representation of OID "1.3.6.1.5.5.7.8.5"
     */
    protected static final byte[] ENCODED_ID_ON_XMPPADD_OID = 
        new byte[] { 0x06, 0x08, 0x21, 0x06, 0x01, 0x05, 0x05, 0x07, 0x08, 0x05 };
    
    /**
     * ASN.1 encoded representation of OID "1.3.6.1.5.5.7.8.7"
     */
    protected static final byte[] ENCODED_ID_ON_DNSSRV_OID = 
        new byte[] { 0x06, 0x08, 0x21, 0x06, 0x01, 0x05, 0x05, 0x07, 0x08, 0x07 };
    
    
    private static enum GeneralNameType {
        OTHERNAME(0),
        RFC822NAME(1),
        DNSNAME(2),
        X400ADDRESS(3),
        DIRECTORYNAME(4),
        EDIPARTYNAME(5),
        UNIFORMRESOURCEIDENTIFIER(6),
        IPADDRESS(7),
        REGISTEREDID(8);
        
        private int val;
        private GeneralNameType(int v) {
            this.val = v;
        }
        static GeneralNameType getValue(int x) {
            for (GeneralNameType g:values()) {
                if (g.val == x) {
                    return g;
                }
            }
            return null;
        }
    }
    
    private void processSubjectAlternativeNames()  {

        Collection<List<?>> sans = null;
        
        try {
            // Process subject alternative names. This returns a sequence
            // of general names
            sans = x509Certificate.getSubjectAlternativeNames();
        }
        catch (CertificateParsingException e) {
            // Leave all the subjectAltNames unparsed 
            return;
        }
        
        if (sans == null) {
            // No subjectAltNames
            return;
        }

        for (List<?> san : sans) {
            // Each general name element contains an Integer representing the
            // name type, and either a String or byte array containing the
            // value
            Integer type = (Integer)san.get(0);
            GeneralNameType nameType = GeneralNameType.getValue(type.intValue());
            switch (nameType) {
            case DNSNAME: // String
                dnsNames_.add((String)san.get(1));
                break;
            case OTHERNAME: // DER
                byte[] encoded = (byte[])san.get(1);
                // TODO: what you get here is something like
                // LBER_SEQUENCE, length = 31 :
                //    tag : 0x06 (LBER_OID), length = 8
                //    value = 1.3.6.1.5.5.7.8.5 (i.e. ID_ON_XMPPADDR_OID)
                //    bytes = [00]=2B  [01]=06  [02]=01  [03]=05  [04]=05  [05]=07  [06]=08  [07]=05  
                //            
                //    CONTEXT[0], length = 19 :
                //     CONTEXT[0], length = 17 :
                //      tag : 0x0C UNIVERSAL[12] primitive, length = 15
                //      value = "funky.isode.net"
                //      bytes = [00]=66  [01]=75  [02]=6E  [03]=6B  [04]=79  [05]=2E  [06]=69  [07]=73  
                //              [08]=6F  [09]=64  [0A]=65  [0B]=2E  [0C]=6E  [0D]=65  [0E]=74  
                //
                // And a corresponding thing for a DNSSRV SAN.
                // However, there's no general ASN.1 decoder in the standard
                // java library, so we will have to implement our own. For
                // now, we ignore these values.

                break;
            case DIRECTORYNAME: // String
            case IPADDRESS: // String
            case REGISTEREDID: // String representation of an OID
            case RFC822NAME: // String
            case UNIFORMRESOURCEIDENTIFIER: // String
            case EDIPARTYNAME: // DER
            case X400ADDRESS: // DER
            default:
                // Other types of subjectalt names are ignored
                break;
            }

        }

    }
    
    /**
     * Construct a new JavaCertificate by parsing an X509Certificate 
     * 
     * @param x509Cert an X509Certificate, which must not be null
     */
    public JavaCertificate(X509Certificate x509Cert)
    {
        if (x509Cert == null) {
            throw new NullPointerException("x509Cert must not be null");
        }
        x509Certificate = x509Cert;

        dnsNames_ = new ArrayList<String>();
        srvNames_ = new ArrayList<String>();
        xmppNames_ = new ArrayList<String>();
        
        processSubjectAlternativeNames();
        
         
    }
    
    /**
     * Return a reference to the X509Certificate object that this 
     * JavaCertificate is wrapping.
     * 
     * @return an X509Certificate (won't be null).
     */
    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
    
    /**
     * Gets a String representation of the certificate subjectname
     * 
     * @return certificate subject name, e.g. "CN=harry,O=acme"
     */
    @Override
    public String getSubjectName() {
        return x509Certificate.getSubjectX500Principal().toString();
    }

    /**
     * Returns a list of all the commonname values from the certificate's
     * subjectDN.  For example, if the subjectDN is "CN=fred,O=acme,CN=bill"
     * then the list returned would contain "fred" and "bill"
     * 
     * @return a list containing the Strings representing common name values
     * in the server certificate's subjectDN. Will never return null, but may
     * return an empty list. 
     */
    @Override
    public List<String> getCommonNames() {
        ArrayList<String> result = new ArrayList<String>();
        
        
        // There isn't a convenient way to extract commonname values from
        // the certificate's subject DN (short of parsing the encoded value
        // ourselves).  So instead, we get a String version, ensuring that
        // any CN values have a prefix we can recognize (we could probably
        // rely on "CN" but this allows us to have a more distinctive value)
        
        X500Principal p = x509Certificate.getSubjectX500Principal();
        Map<String, String> cnMap = new HashMap<String, String>();
        
        // Request that the returned String will use our label for any values
        // with the commonName OID 
        cnMap.put(cnOID, cnLabel); 
        String s = p.getName("RFC2253",cnMap);
        
        String cnPrefix = cnLabel + "=";
        
        int x = s.indexOf(cnPrefix);
        if (x == -1) {
            return result; // No CN values to add
        }
        
        // Crude attempt to split, noting that this may result in values
        // that contain an escaped comma being chopped between more than one
        // element, so we need to go through this subsequently and handle that..
        String[] split=s.split(",");

        boolean inQuote = false;
        boolean escape = false;
        
        int e = 0;
        String field = "";

        while (e < split.length) {
            String element = split[e];
            int quoteCount = 0;
            for (int i=0; i<element.length(); i++) {
                char c = element.charAt(i);
                if (c == '"') {
                    quoteCount++;
                }
            }
            escape = (element.endsWith("\\"));
            
            inQuote = ((quoteCount % 2) == 1);
            if (!inQuote && !escape) {
                // We got to the end of a field
                field += element;
                if (field.startsWith(cnPrefix)) {
                    result.add(field.substring(cnPrefix.length()));
                }
                field = "";
            }
            else {
                // the split has consumed a comma that was part of a quoted
                // String.  
                field = field + element + ",";
            }                  
            e++;        
        }
        return result;
    }

    /**
     * Returns a list of all the SRV values held in "OTHER" type subjectAltName
     * fields in the server's certificate.  
     * 
     * @return a list containing the Strings representing SRV subjectAltName
     * values from the server certificate. Will never return null, but may
     * return an empty list. 
     */
    @Override
    public List<String> getSRVNames() {
        // TODO: At the moment it will always return
        // an empty list -see processSubjectAlternativeNames()
        return srvNames_;
    }

    /**
     * Returns a list of all the DNS subjectAltName values from the server's
     * certificate.  
     * 
     * @return a list containing the Strings representing DNS subjectAltName
     * values from the server certificate. Will never return null, but may
     * return an empty list. 
     */
    @Override
    public List<String> getDNSNames() {       
        return dnsNames_;
    }

    /**
     * Returns a list of all the XMPP values held in "OTHER" type subjectAltName
     * fields in the server's certificate.  
     * 
     * @return a list containing the Strings representing XMPP subjectAltName
     * values from the server certificate. Will never return null, but may
     * return an empty list. 
     */
    @Override
    public List<String> getXMPPAddresses() {
        // TODO: At the moment it will always return
        // an empty list -see processSubjectAlternativeNames()
        return xmppNames_;
    }

    /**
     * Return the encoded representation of the certificate
     * 
     * @return the DER encoding of the certificate.  Will return null if
     * the certificate is not valid.
     */
    @Override
    public ByteArray toDER() {
        // TODO Auto-generated method stub
        try {
            byte[] r = x509Certificate.getEncoded();            
            return new ByteArray(r);
        }
        catch (CertificateEncodingException e) {
            return null;
        }
    }
    
    private List<String> dnsNames_ = null;
    private List<String> srvNames_ = null;
    private List<String> xmppNames_ = null;
    
    /**
     * OID for commonName
     */
    private final static String cnOID = "2.5.4.3";
    /**
     * String to be used to identify commonName values in a DN.
     */
    private final static String cnLabel = "COMMONNAME";

}
