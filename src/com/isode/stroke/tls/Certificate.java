/*
 * Copyright (c) 2011-2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.stringcodecs.Hexify;
import java.util.List;

public abstract class Certificate {

    /**
     * Returns the textual representation of the full Subject
     * name.
     */
    public abstract String getSubjectName();

    public abstract List<String> getCommonNames();

    public abstract List<String> getSRVNames();

    public abstract List<String> getDNSNames();

    public abstract List<String> getXMPPAddresses();

    public abstract ByteArray toDER();

    public static String getSHA1Fingerprint(Certificate certificate, CryptoProvider crypto) {
        ByteArray hash = crypto.getSHA1Hash(certificate.toDER());
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < hash.getSize(); ++i) {
            if (i > 0) {
                s.append(":");
            }
            s.append(Hexify.hexify(hash.getData()[i]));
        }
        return s.toString();
    }
    protected String ID_ON_XMPPADDR_OID = "1.3.6.1.5.5.7.8.5";
    protected String ID_ON_DNSSRV_OID = "1.3.6.1.5.5.7.8.7";
}
