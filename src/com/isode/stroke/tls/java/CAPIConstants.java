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
/**
 * Defines various constant values used in the CAPI implementation
 */
public class CAPIConstants {
    /**
     * The name of the Sun MSCAPI provider
     */
    final public static String sunMSCAPIProvider = "SunMSCAPI";
    
    /**
     * The list of KeyStores available in the SunMSCAPI provider
     * as per Oracle's 
     * <a href=http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html>
     * JCA documentation</a>.
     * The list is in order of preference
     * I can't see a reliable programmatic way of asking the provider what
     * keystores it supports.
     * 
     */
    final public static String[] knownSunMSCAPIKeyStores = new String[] 
            {"Windows-MY", "Windows-ROOT"};


}
