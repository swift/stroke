/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;

public class PlatformDomainNameAddressQuery extends DomainNameAddressQuery {

    private final String host_;
    private final EventLoop eventLoop_;
    
    public PlatformDomainNameAddressQuery(String host,EventLoop eventLoop) {
        host_ = host;
        eventLoop_ = eventLoop;
    }
    
    private class QueryRunnable implements Runnable {

        private final List<HostAddress> results_ = 
                Collections.synchronizedList(new ArrayList<HostAddress>());

        @Override
        public void run() {
            try {
                InetAddress[] inetAddresses = InetAddress.getAllByName(host_);
                for (InetAddress address : inetAddresses) {
                    HostAddress result = new HostAddress(address);
                    results_.add(result);
                }
            } catch (UnknownHostException e) {
                emitError();
            }
            emitResults();
        }

        private void emitError() {
            eventLoop_.postEvent(new Callback() {
                
                @Override
                public void run() {
                    onResult.emit(new ArrayList<HostAddress>(),new DomainNameResolveError());
                }
                
            });
        }

        private void emitResults() {
            eventLoop_.postEvent(new Callback() {
                
                @Override
                public void run() {
                    // For thread safety emit a copy of the results
                    List<HostAddress> resultCopy = new ArrayList<HostAddress>();
                    synchronized (results_) {
                        resultCopy.addAll(results_);
                    }
                    onResult.emit(results_,null);
                }
                
            });
        }
        
    }

    @Override
    public void run() {
        Thread queryThread = new Thread(new QueryRunnable());
        queryThread.setDaemon(true);
        queryThread.run();
    }

}
