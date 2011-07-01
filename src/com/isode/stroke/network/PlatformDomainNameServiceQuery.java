/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.network;

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

    public PlatformDomainNameServiceQuery(String service, EventLoop eventLoop) {
        this.service = service;
        this.eventLoop = eventLoop;
    }

    @Override
    public void run() {
        //TODO: Make async
        Collection<Result> results = new ArrayList<Result>();
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        try {
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(this.service, new String[]{"SRV"});
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

        onResult.emit(results);
    }


//    void PlatformDomainNameServiceQuery::doRun() {
//	std::vector<DomainNameServiceQuery::Result> records;
//
//#if defined(SWIFTEN_PLATFORM_WINDOWS)
//	DNS_RECORD* responses;
//	// FIXME: This conversion doesn't work if unicode is deffed above
//	if (DnsQuery(service.getUTF8Data(), DNS_TYPE_SRV, DNS_QUERY_STANDARD, NULL, &responses, NULL) != ERROR_SUCCESS) {
//		emitError();
//		return;
//	}
//
//	DNS_RECORD* currentEntry = responses;
//	while (currentEntry) {
//		if (currentEntry->wType == DNS_TYPE_SRV) {
//			DomainNameServiceQuery::Result record;
//			record.priority = currentEntry->Data.SRV.wPriority;
//			record.weight = currentEntry->Data.SRV.wWeight;
//			record.port = currentEntry->Data.SRV.wPort;
//
//			// The pNameTarget is actually a PCWSTR, so I would have expected this
//			// conversion to not work at all, but it does.
//			// Actually, it doesn't. Fix this and remove explicit cast
//			// Remove unicode undef above as well
//			record.hostname = String((const char*) currentEntry->Data.SRV.pNameTarget);
//			records.push_back(record);
//		}
//		currentEntry = currentEntry->pNext;
//	}
//	DnsRecordListFree(responses, DnsFreeRecordList);
//
//#else
//	// Make sure we reinitialize the domain list every time
//	res_init();
//
//	//std::cout << "SRV: Querying " << service << std::endl;
//	ByteArray response;
//	response.resize(NS_PACKETSZ);
//	int responseLength = res_query(const_cast<char*>(service.getUTF8Data()), ns_c_in, ns_t_srv, reinterpret_cast<u_char*>(response.getData()), response.getSize());
//	if (responseLength == -1) {
//		emitError();
//		return;
//	}
//
//	// Parse header
//	HEADER* header = reinterpret_cast<HEADER*>(response.getData());
//	unsigned char* messageStart = reinterpret_cast<unsigned char*>(response.getData());
//	unsigned char* messageEnd = messageStart + responseLength;
//	unsigned char* currentEntry = messageStart + NS_HFIXEDSZ;
//
//	// Skip over the queries
//	int queriesCount = ntohs(header->qdcount);
//	while (queriesCount > 0) {
//		int entryLength = dn_skipname(currentEntry, messageEnd);
//		if (entryLength < 0) {
//			emitError();
//			return;
//		}
//		currentEntry += entryLength + NS_QFIXEDSZ;
//		queriesCount--;
//	}
//
//	// Process the SRV answers
//	int answersCount = ntohs(header->ancount);
//	while (answersCount > 0) {
//		DomainNameServiceQuery::Result record;
//
//		int entryLength = dn_skipname(currentEntry, messageEnd);
//		currentEntry += entryLength;
//		currentEntry += NS_RRFIXEDSZ;
//
//		// Priority
//		if (currentEntry + 2 >= messageEnd) {
//			emitError();
//			return;
//		}
//		record.priority = ns_get16(currentEntry);
//		currentEntry += 2;
//
//		// Weight
//		if (currentEntry + 2 >= messageEnd) {
//			emitError();
//			return;
//		}
//		record.weight = ns_get16(currentEntry);
//		currentEntry += 2;
//
//		// Port
//		if (currentEntry + 2 >= messageEnd) {
//			emitError();
//			return;
//		}
//		record.port = ns_get16(currentEntry);
//		currentEntry += 2;
//
//		// Hostname
//		if (currentEntry >= messageEnd) {
//			emitError();
//			return;
//		}
//		ByteArray entry;
//		entry.resize(NS_MAXDNAME);
//		entryLength = dn_expand(messageStart, messageEnd, currentEntry, entry.getData(), entry.getSize());
//		if (entryLength < 0) {
//			emitError();
//			return;
//		}
//		record.hostname = String(entry.getData());
//		records.push_back(record);
//		currentEntry += entryLength;
//		answersCount--;
//	}
//#endif
//
//	safeToJoin = true;
//	std::sort(records.begin(), records.end(), ResultPriorityComparator());
//	//std::cout << "Sending out " << records.size() << " SRV results " << std::endl;
//	eventLoop->postEvent(boost::bind(boost::ref(onResult), records));
//}


    private final String service;
    private final EventLoop eventLoop;
}
