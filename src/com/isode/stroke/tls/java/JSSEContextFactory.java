/*  Copyright (c) 2012-2013, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written licence agreement from Isode Limited,
 *  or a written licence from an organisation licensed by Isode Limited Limited
 *  to grant such a licence.
 *
 */
 
package com.isode.stroke.tls.java;

import java.util.HashSet;
import java.util.Set;

import com.isode.stroke.tls.TLSContext;
import com.isode.stroke.tls.TLSOptions;
import com.isode.stroke.tls.TLSContextFactory;

/**
 * Concrete implementation of a TLSContextFactory which uses SSLEngine 
 * 
 * <p>Ciphersuite names recognised by this class correspond to the standard
 * names as described in 
 * <a href=http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#ciphersuites>
 * Oracle's "Java Cryptography Architecture Standard Algorithm Name Documentation"</a>.
 * 
 */
public class JSSEContextFactory implements TLSContextFactory {

    @Override
    public boolean canCreate() {
        return true;
    }

    @Override
    public TLSContext createTLSContext(TLSOptions tlsOptions) {
        return new JSSEContext(restrictedCipherSuites);
    }
    
    private static Set<String> restrictedCipherSuites = null;
    
    /**
     * Restrict which cipher suites are to be enabled for any TLSContexts
     * returned by this factory from now on. Any name which is
     * not recognised, or not available is ignored: this method cannot be 
     * used to enable otherwise unavailable ciphersuites.
     * 
     * @param cipherSuites a set of cipher suite names. If this parameter is
     * null, then no restriction on cipher suites applies (all suites available
     * to the implementation will be enabled). 
     * 
     */
    public static void setRestrictedCipherSuites(Set<String> cipherSuites) {
        if (cipherSuites == null) {
            restrictedCipherSuites = null;
            return;
        }
        
        restrictedCipherSuites = new HashSet<String>(cipherSuites);
    }    

}
