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

    public PlatformDomainNameResolver(EventLoop eventLoop) {
        this.eventLoop_ = eventLoop;
    }

    @Override
    public DomainNameServiceQuery createServiceQuery(String name) {
        return new PlatformDomainNameServiceQuery(getNormalized(name), eventLoop_);
    }

    @Override
    public DomainNameAddressQuery createAddressQuery(String name) {
        return new AddressQuery(getNormalized(name), eventLoop_);
    }
    private final EventLoop eventLoop_;
}
