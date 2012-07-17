/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

public class PlatformDomainNameResolver extends DomainNameResolver {

    private class AddressQuery extends DomainNameAddressQuery implements EventOwner {

        private class AddressQueryThread extends Thread {
            @Override
            public void run() {
                final Collection<HostAddress> results = new ArrayList<HostAddress>();
                try {
                    results.add(new HostAddress(InetAddress.getByName(hostname)));
                } catch (UnknownHostException ex) {
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
        this.eventLoop = eventLoop;
    }

    @Override
    public DomainNameServiceQuery createServiceQuery(String name) {
        return new PlatformDomainNameServiceQuery(getNormalized(name), eventLoop);
    }

    @Override
    public DomainNameAddressQuery createAddressQuery(String name) {
        return new AddressQuery(getNormalized(name), eventLoop);
    }
    private final EventLoop eventLoop;
}
