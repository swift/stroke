/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.stringcodecs.SHA1;
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

    public String getSHA1Fingerprint() {
        ByteArray hash = SHA1.getHash(toDER());
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
