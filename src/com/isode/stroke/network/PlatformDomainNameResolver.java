/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;


public class PlatformDomainNameResolver extends DomainNameResolver {

    private class AddressQuery extends DomainNameAddressQuery implements EventOwner {
	AddressQuery(String host, EventLoop eventLoop) {
            hostname = host;
            this.eventLoop = eventLoop;
            //FIXME: port asyncDNS
//            thread = null;
//            safeToJoin = false;
        }

	public void run() {
            //FIXME: port asyncDNS
            Collection<HostAddress> results = new ArrayList<HostAddress>();
            try {
                results.add(new HostAddress(InetAddress.getByName(hostname)));
            } catch (UnknownHostException ex) {

            }
            onResult.emit(results, results.isEmpty() ? new DomainNameResolveError() : null);

//            safeToJoin = false;
//            thread = new boost::thread(boost::bind(&AddressQuery::doRun, shared_from_this()));
	}
// FIXME: Port async DNS.
//		void doRun() {
//			//std::cout << "PlatformDomainNameResolver::doRun()" << std::endl;
//			boost::asio::ip::tcp::resolver resolver(ioService);
//			boost::asio::ip::tcp::resolver::query query(hostname.getUTF8String(), "5222");
//			try {
//				//std::cout << "PlatformDomainNameResolver::doRun(): Resolving" << std::endl;
//				boost::asio::ip::tcp::resolver::iterator endpointIterator = resolver.resolve(query);
//				//std::cout << "PlatformDomainNameResolver::doRun(): Resolved" << std::endl;
//				if (endpointIterator == boost::asio::ip::tcp::resolver::iterator()) {
//					//std::cout << "PlatformDomainNameResolver::doRun(): Error 1" << std::endl;
//					emitError();
//				}
//				else {
//					std::vector<HostAddress> results;
//					for ( ; endpointIterator != boost::asio::ip::tcp::resolver::iterator(); ++endpointIterator) {
//						boost::asio::ip::address address = (*endpointIterator).endpoint().address();
//						results.push_back(address.is_v4() ? HostAddress(&address.to_v4().to_bytes()[0], 4) : HostAddress(&address.to_v6().to_bytes()[0], 16));
//					}
//
//					//std::cout << "PlatformDomainNameResolver::doRun(): Success" << std::endl;
//					eventLoop->postEvent(
//							boost::bind(boost::ref(onResult), results, boost::optional<DomainNameResolveError>()),
//							shared_from_this());
//				}
//			}
//			catch (...) {
//				//std::cout << "PlatformDomainNameResolver::doRun(): Error 2" << std::endl;
//				emitError();
//			}
//			safeToJoin = true;
//		}
//
//		void emitError() {
//			eventLoop->postEvent(boost::bind(boost::ref(onResult), std::vector<HostAddress>(), boost::optional<DomainNameResolveError>(DomainNameResolveError())), shared_from_this());
//		}
//
//		boost::asio::io_service ioService;
		String hostname;
		EventLoop eventLoop;
//		boost::thread* thread;
//		bool safeToJoin;
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
