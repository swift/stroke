/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.DomainNameServiceQuery;
import com.isode.stroke.network.DomainNameAddressQuery;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.eventloop.Event;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class StaticDomainNameResolver extends DomainNameResolver {

	private EventLoop eventLoop;
	private boolean isResponsive;
	private Map<String, Vector<HostAddress> > addresses = new HashMap<String, Vector<HostAddress> >();
	private Vector<Pair> services = new Vector<Pair>();
	private EventOwner owner;

	class ServiceQuery extends DomainNameServiceQuery {

		public ServiceQuery(final String service, StaticDomainNameResolver resolver, EventLoop eventLoop, EventOwner owner) {
			this.eventLoop = eventLoop;
			this.service = service;
			this.resolver = resolver;
			this.owner = owner;
		}

		public void run() {
			if (!resolver.getIsResponsive()) {
				return;
			}
			final Vector<DomainNameServiceQuery.Result> results = new Vector<DomainNameServiceQuery.Result>();
			for(StaticDomainNameResolver.Pair i : resolver.getServices()) {
				if(i.node.equals(service)) {
					results.add(i.queryResult);
				}
			}
			eventLoop.postEvent(new Event.Callback() {
				@Override				
				public void run() {
					emitOnResult(results);
				}
			}, owner);
		}

		public void emitOnResult(Vector<DomainNameServiceQuery.Result> results) {
			onResult.emit(results);
		}

		public EventLoop eventLoop;
		public String service = "";
		public StaticDomainNameResolver resolver;
		public EventOwner owner;
	};

	class AddressQuery extends DomainNameAddressQuery {

		public AddressQuery(final String host, StaticDomainNameResolver resolver, EventLoop eventLoop, EventOwner owner) {
			this.eventLoop = eventLoop;
			this.host = host;
			this.resolver = resolver;
			this.owner = owner;
		}

		public void run() {
			if (!resolver.getIsResponsive()) {
				return;
			}
			if (resolver.getAddresses().containsKey(host)) {
				eventLoop.postEvent(new Event.Callback() {
					@Override
					public void run() {
						emitOnResult(resolver.getAddresses().get(host), null);
					}
				});
			}
			else {
				eventLoop.postEvent(new Event.Callback() {
					@Override					
					public void run() {
						emitOnResult(new Vector<HostAddress>(), new DomainNameResolveError());
					}
				});
			}
		}

		public void emitOnResult(Vector<HostAddress> results, DomainNameResolveError error) {
			onResult.emit(results, error);
		}

		public EventLoop eventLoop;
		public String host = "";
		public StaticDomainNameResolver resolver;
		public EventOwner owner;
	};

	private class Pair {
		public String node;
		public DomainNameServiceQuery.Result queryResult;

		public Pair(String j, DomainNameServiceQuery.Result n) {node = j; queryResult = n;}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pair)) return false;
			Pair o1 = (Pair) o;
			return queryResult.equals(o1.queryResult) && node.equals(o1.node);
		}
	}

	class StaticDomainNameResolverEventOwner implements EventOwner {

	};

	public StaticDomainNameResolver(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
		isResponsive = true;
		owner = new StaticDomainNameResolverEventOwner();
	}

	public void addAddress(final String domain, final HostAddress address) {
		Vector<HostAddress> vec = new Vector<HostAddress>();
		vec.add(address);
		if(!(addresses.containsKey(domain))) {
			addresses.put(domain, vec);
		}
		else {
			addresses.get(domain).add(address);
		}
	}

	public void addService(final String service, final DomainNameServiceQuery.Result result) {
		services.add(new Pair(service, result));
	}

	public void addXMPPClientService(final String domain, final HostAddressPort address) {
		int hostid = 0;
		String hostname = "host-" + Integer.toString(hostid);
		hostid++;

		addService("_xmpp-client._tcp." + domain, new ServiceQuery.Result(hostname, address.getPort(), 0, 0));
		addAddress(hostname, address.getAddress());
	}

	public void addXMPPClientService(final String domain, final String hostname, int port) {
		addService("_xmpp-client._tcp." + domain, new ServiceQuery.Result(hostname, port, 0, 0));
	}

	public Map<String, Vector<HostAddress> > getAddresses() {
		return addresses;
	}

	public Vector<Pair> getServices() {
		return services;
	}

	public boolean getIsResponsive() {
		return isResponsive;
	}

	public void setIsResponsive(boolean b) {
		isResponsive = b;
	}

	public DomainNameServiceQuery createServiceQuery(final String serviceLookupPrefix, final String domain) {
		return new ServiceQuery(serviceLookupPrefix + domain, this, eventLoop, owner);
	}

	public DomainNameAddressQuery createAddressQuery(final String name) {
		return new AddressQuery(name, this, eventLoop, owner);
	}
}