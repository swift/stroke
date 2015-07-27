/*
 * Copyright (c) 2011-2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;


import com.isode.stroke.tls.TLSOptions;
import com.isode.stroke.base.URL;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.HTTPTrafficFilter;

/**
 * Options for a client connection
 */
public class ClientOptions {
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
     * Forget the password once it's used.
     * This makes the Client useless after the first login attempt.
     *
     * FIXME: This is a temporary workaround.
     *
     * Default: false
     */
    public boolean forgetPassword;

    /**
     * Use XEP-0198 acks in the stream when available.
     * Default: true
     */
    public boolean useAcks;

    /**
     * Use Single Sign On.
     * Default: false
     */
    public boolean singleSignOn;

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

    /**
     * The type of proxy to use for connecting to the XMPP
     * server.
     */
    public ProxyType proxyType;

    /**
     * Override the system-configured proxy hostname.
     */
    public String manualProxyHostname;

    /**
     * Override the system-configured proxy port.
     */
    public int manualProxyPort;

    /**
     * If non-empty, use BOSH instead of direct TCP, with the given URL.
     * Default: empty (no BOSH)
     */
    public URL boshURL = new URL();

    /**
     * If non-empty, BOSH connections will try to connect over this HTTP CONNECT
     * proxy instead of directly.
     * Default: empty (no proxy)
     */
    public URL boshHTTPConnectProxyURL = new URL();

    /**
     * If this and matching Password are non-empty, BOSH connections over
     * HTTP CONNECT proxies will use these credentials for proxy access.
     * Default: empty (no authentication needed by the proxy)
     */
    public SafeByteArray boshHTTPConnectProxyAuthID;
    public SafeByteArray boshHTTPConnectProxyAuthPassword;

    /**
     * This can be initialized with a custom HTTPTrafficFilter, which allows HTTP CONNECT
     * proxy initialization to be customized.
     */
    public HTTPTrafficFilter httpTrafficFilter;

    /**
     * Options passed to the TLS stack
     */
    public TLSOptions tlsOptions = new TLSOptions();

    public enum UseTLS {
        NeverUseTLS,
        UseTLSWhenAvailable,
        RequireTLS
    }

    public enum ProxyType {
        NoProxy,
        SystemConfiguredProxy,
        SOCKS5Proxy,
        HTTPConnectProxy
    };

    public ClientOptions() {
        useStreamCompression = true;
        useTLS = UseTLS.UseTLSWhenAvailable;
        allowPLAINWithoutTLS = false;
        useStreamResumption = false;
        forgetPassword = false;
        useAcks = true;
        singleSignOn = false;
        manualHostname = "";
        manualPort = -1;
        proxyType = ProxyType.SystemConfiguredProxy;
        manualProxyHostname = "";
        manualProxyPort = -1;
        boshHTTPConnectProxyAuthID = new SafeByteArray(""); 
        boshHTTPConnectProxyAuthPassword = new SafeByteArray("");        
    }

    @Override
    public String toString() {
        return
            "useStreamCompression:" + useStreamCompression +
            "; useStreamResumption:" + useStreamResumption +
            "; " + useTLS;
    }
}
