/*
 * Copyright (c) 2010-2013, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */

package com.isode.stroke.network;
import java.util.ArrayList;
import java.util.Collection;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.network.DomainNameServiceQuery;

public class PlatformDomainNameServiceQuery extends DomainNameServiceQuery {
    private final String service;
    private final EventLoop eventLoop;

    public PlatformDomainNameServiceQuery(final String service, final EventLoop eventLoop) {
        this.service = service;
        this.eventLoop = eventLoop;
    }

    private class QueryThread extends Thread {
        @Override
        public void run() {
            final Collection<Result> results = new ArrayList<Result>();
            Lookup request;
            try {
                request = new Lookup(service, Type.SRV);
                final Record[] records = request.run();
                if (records != null) {
                    for (final Record record : records) {
                        /* It's only anticipated that SRVRecords will be
                         * returned, but check first
                         */
                        if (record instanceof SRVRecord) {
                            final SRVRecord srv = (SRVRecord) record;
                            final Result result = new Result(srv.getTarget()
                                .toString(), srv.getPort(), srv.getPriority(),
                                srv.getWeight());
                            results.add(result);
                        }
                    }
                }
            } catch (final TextParseException e) {
                /* Lookup failed because "service" was not a valid DNS name;
                 * leave "results" empty 
                 */
            }

            eventLoop.postEvent(new Callback() {
                @Override
                public void run() {
                    onResult.emit(results);
                }
            });
        }
    }

    @Override
    public void run() {
        final QueryThread thread = new QueryThread();
        thread.setDaemon(true);
        thread.start();
    }
}
