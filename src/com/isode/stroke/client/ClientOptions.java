/*
 * Copyright (c) 2011-2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

/**
 * Options for a client connection
 */
public class ClientOptions {

    public enum UseTLS {

        NeverUseTLS,
        UseTLSWhenAvailable,
        RequireTLS
    }

    public ClientOptions() {
        useStreamCompression = true;
        useTLS = UseTLS.UseTLSWhenAvailable;
        useStreamResumption = false;
        allowPLAINWithoutTLS = false;
        useAcks = true;
        manualHostname = "";
        manualPort = -1;
    }
    
    @Override
    public String toString() {
        return  
            "useStreamCompression:" + useStreamCompression +
            "; useStreamResumption:" + useStreamResumption +
            "; " + useTLS;
    }

    /**
     * Whether ZLib stream compression should be used when available.
     *
     * Default: true
     */
    public boolean useStreamCompression;
    /**
     * Sets whether TLS encryption should be used.
     *
     * Default: UseTLSWhenAvailable
     */
    public UseTLS useTLS;
    /**
     * Sets whether plaintext authentication is
     * allowed over non-TLS-encrypted connections.
     *
     * Default: false
     */
    public boolean allowPLAINWithoutTLS;
    /**
     * Use XEP-198 stream resumption when available.
     *
     * Default: false
     */
    public boolean useStreamResumption;
    
    /**
     * Use XEP-0198 acks in the stream when available.
     * Default: true
     */
    public boolean useAcks;

    /**
     * The hostname to connect to.
     * Leave this empty for standard XMPP connection, based on the JID domain.
     */
    public String manualHostname;

    /**
     * The port to connect to.
     * Leave this to -1 to use the port discovered by SRV lookups, and 5222 as a
     * fallback.
     */
    public int manualPort;

    
}
