/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

/**
 *
 */
public class ClientOptions {

    enum UseTLS {

        NeverUseTLS,
        UseTLSWhenAvailable
    };

    public ClientOptions() {
        useStreamCompression = true;
        useTLS = UseTLS.UseTLSWhenAvailable;
        useStreamResumption = false;
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
