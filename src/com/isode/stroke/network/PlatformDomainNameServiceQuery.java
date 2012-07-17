/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class PlatformDomainNameServiceQuery extends DomainNameServiceQuery implements EventOwner {

    private class QueryThread extends Thread {

        @Override
        public void run() {
            final Collection<Result> results = new ArrayList<Result>();
            Hashtable env = new Hashtable();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns:");
            try {
                DirContext ctx = new InitialDirContext(env);
                Attributes attrs = ctx.getAttributes(service, new String[]{"SRV"});
                Attribute attribute = attrs.get("SRV");
                for (int i = 0; attribute != null && i < attribute.size(); i++) {
                    /* SRV results are going to be returned in the space-separated format
                     * Priority Weight Port Target
                     * (See RFC2782)
                     */
                    String[] srvParts = ((String) attribute.get(i)).split(" ");
                    String host = srvParts[3];
                    if (host.endsWith(".")) {
                        host = host.substring(0, host.length() - 1);
                    }
                    Result result = new Result(host, Integer.parseInt(srvParts[2]), Integer.parseInt(srvParts[0]), Integer.parseInt(srvParts[1]));
                    results.add(result);
                }
            } catch (NamingException ex) {
                /* Turns out that you get the exception just for not finding a result, so we want to fall through to A lookups and ignore.*/
            }

            eventLoop.postEvent(new Callback() {
                public void run() {
                    onResult.emit(results);
                }
            });

        }
    }

    public PlatformDomainNameServiceQuery(String service, EventLoop eventLoop) {
        this.service = service;
        this.eventLoop = eventLoop;
    }

    @Override
    public void run() {
        QueryThread thread = new QueryThread();
        thread.setDaemon(true);
        thread.start();
    }
    private final String service;
    private final EventLoop eventLoop;
}
