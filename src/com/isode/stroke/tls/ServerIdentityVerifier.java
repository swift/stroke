/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.idn.IDNA;
import com.isode.stroke.jid.JID;
import java.util.List;

public class ServerIdentityVerifier {

    public ServerIdentityVerifier(JID jid) {
        domain = jid.getDomain();
        encodedDomain = IDNA.getEncoded(domain);
    }

    public boolean certificateVerifies(Certificate certificate) {
        boolean hasSAN = false;

        // DNS names
        List<String> dnsNames = certificate.getDNSNames();
        for (String dnsName : dnsNames) {
            if (matchesDomain(dnsName)) {
                return true;
            }
        }
        hasSAN |= !dnsNames.isEmpty();

        // SRV names
        List<String> srvNames = certificate.getSRVNames();
        for (String srvName : srvNames) {
            // Only match SRV names that begin with the service; this isn't required per
            // spec, but we're being purist about this.
            if (srvName.startsWith("_xmpp-client.") && matchesDomain(srvName.substring("_xmpp-client.".length()))) {
                return true;
            }
        }
        hasSAN |= !srvNames.isEmpty();

        // XmppAddr
        List<String> xmppAddresses = certificate.getXMPPAddresses();
        for (String xmppAddress : xmppAddresses) {
            if (matchesAddress(xmppAddress)) {
                return true;
            }
        }
        hasSAN |= !xmppAddresses.isEmpty();

        // CommonNames. Only check this if there was no SAN (according to spec).
        if (!hasSAN) {
            List<String> commonNames = certificate.getCommonNames();
            for (String commonName : commonNames) {
                if (matchesDomain(commonName)) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean matchesDomain(String s) {
        if (s.startsWith("*.")) {
            String matchString = s.substring(2);
            String matchDomain = encodedDomain;
            int dotIndex = matchDomain.indexOf('.');
            if (dotIndex >= 0) {
                matchDomain = matchDomain.substring(dotIndex + 1);
            }
            return matchString.equals(matchDomain);
        }
        else {
            return s.equals(encodedDomain);
        }
    }

    boolean matchesAddress(String s) {
        return s.equals(domain);
    }
    private String domain;
    private String encodedDomain;
}
