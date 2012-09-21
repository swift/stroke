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
        UseTLSWhenAvailable
    }

    public ClientOptions() {
        useStreamCompression = true;
        useTLS = UseTLS.UseTLSWhenAvailable;
        useStreamResumption = false;
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
     * Use XEP-196 stream resumption when available.
     *
     * Default: false
     */
    public boolean useStreamResumption;
    
}
