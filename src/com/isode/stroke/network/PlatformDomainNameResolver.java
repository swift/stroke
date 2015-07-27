/*
 * Copyright (c) 2010-2013, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.ICUConverter;

public class PlatformDomainNameResolver extends DomainNameResolver {

    private class AddressQuery extends DomainNameAddressQuery implements EventOwner {

        private class AddressQueryThread extends Thread {
            @Override
            public void run() {
                final Collection<HostAddress> results = new ArrayList<HostAddress>();
                try {
                    for (InetAddress result : InetAddress.getAllByName(hostname)) {
                        results.add(new HostAddress(result));
                    }
                } catch (UnknownHostException ex) {
                    /* results remains empty */
                }
                eventLoop.postEvent(new Callback() {
                    public void run() {
                        onResult.emit(results, results.isEmpty() ? new DomainNameResolveError() : null);
                    }
                });
            }
        }

        AddressQuery(String host, EventLoop eventLoop) {
            hostname = host;
            this.eventLoop = eventLoop;
        }

        public void run() {
            AddressQueryThread thread = new AddressQueryThread();
            thread.setDaemon(true);
            thread.start();
        }
        final String hostname;
        final EventLoop eventLoop;
    }

    public PlatformDomainNameResolver(IDNConverter idnConverter, EventLoop eventLoop) {
        this.eventLoop_ = eventLoop;
        this.idnConverter = idnConverter;
    }

    @Override
    public DomainNameServiceQuery createServiceQuery(String serviceLookupPrefix, String name) {
        String encodedDomain = idnConverter.getIDNAEncoded(name);
        String result = "";
        if (encodedDomain != null) {
            result = serviceLookupPrefix + encodedDomain;
        }        
        return new PlatformDomainNameServiceQuery(result, eventLoop_);
    }

    @Override
    public DomainNameAddressQuery createAddressQuery(String name) {
        return new AddressQuery(idnConverter.getIDNAEncoded(name), eventLoop_);
    }
    private final EventLoop eventLoop_;
    private IDNConverter idnConverter;
}
