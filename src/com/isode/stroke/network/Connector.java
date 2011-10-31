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

import com.isode.stroke.network.DomainNameServiceQuery.Result;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import java.util.ArrayList;
import java.util.Collection;

public class Connector {

    public static Connector create(String hostname, DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory) {
        return new Connector(hostname, resolver, connectionFactory, timerFactory);
    }

    public void setTimeoutMilliseconds(int milliseconds) {
        timeoutMilliseconds = milliseconds;
    }

    public void start() {
        assert currentConnection == null;
        assert serviceQuery == null;
        assert timer == null;
        queriedAllServices = false;
        serviceQuery = resolver.createServiceQuery("_xmpp-client._tcp." + hostname);
        serviceQuery.onResult.connect(new Slot1<Collection<DomainNameServiceQuery.Result>>() {
            public void call(Collection<Result> p1) {
                handleServiceQueryResult(p1);
            }
        });
        if (timeoutMilliseconds > 0) {
            timer = timerFactory.createTimer(timeoutMilliseconds);
            timer.onTick.connect(new Slot() {
                public void call() {
                    handleTimeout();
                }
            });
            timer.start();
        }
        serviceQuery.run();
    }

    public void stop() {
        finish(null);
    }

    public final Signal1<Connection> onConnectFinished = new Signal1<Connection>();

    private Connector(String hostname, DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory) {
        this.hostname = hostname;
        this.resolver = resolver;
        this.connectionFactory = connectionFactory;
        this.timerFactory = timerFactory;
    }

    private void handleServiceQueryResult(Collection<Result> result) {
        serviceQueryResults = new ArrayList<Result>();
        serviceQueryResults.addAll(result);
        serviceQuery = null;
        tryNextServiceOrFallback();
    }

    private void handleAddressQueryResult(Collection<HostAddress> addresses, DomainNameResolveError error) {
      	//std::cout << "Connector::handleAddressQueryResult(): Start" << std::endl;
	addressQuery = null;
	if (error != null || addresses.isEmpty()) {
		if (!serviceQueryResults.isEmpty()) {
			serviceQueryResults.remove(0);
		}
		tryNextServiceOrFallback();
	}
	else {
		addressQueryResults = new ArrayList<HostAddress>();
                addressQueryResults.addAll(addresses);
		tryNextAddress();
	}
    }

    private void queryAddress(String hostname) {
        assert addressQuery == null;
        addressQuery = resolver.createAddressQuery(hostname);
        addressQuery.onResult.connect(new Slot2<Collection<HostAddress>, DomainNameResolveError>() {
            public void call(Collection<HostAddress> p1, DomainNameResolveError p2) {
                handleAddressQueryResult(p1, p2);
            }
        });
        addressQuery.run();
    }

    private void tryNextServiceOrFallback() {
        if (queriedAllServices) {
		//std::cout << "Connector::tryNextServiceOrCallback(): Queried all hosts. Error." << std::endl;
		finish(null);
	}
	else if (serviceQueryResults.isEmpty()) {
		//std::cout << "Connector::tryNextHostName(): Falling back on A resolution" << std::endl;
		// Fall back on simple address resolving
		queriedAllServices = true;
		queryAddress(hostname);
	}
	else {
		//std::cout << "Connector::tryNextHostName(): Querying next address" << std::endl;
		queryAddress(serviceQueryResults.get(0).hostname);
	}
    }

    private void tryNextAddress() {
       	if (addressQueryResults.isEmpty()) {
		//std::cout << "Connector::tryNextAddress(): Done trying addresses. Moving on" << std::endl;
		// Done trying all addresses. Move on to the next host.
		if (!serviceQueryResults.isEmpty()) {
			serviceQueryResults.remove(0);
		}
		tryNextServiceOrFallback();
	}
	else {
		//std::cout << "Connector::tryNextAddress(): trying next address." << std::endl;
		HostAddress address = addressQueryResults.get(0);
		addressQueryResults.remove(0);

		int port = 5222;
		if (!serviceQueryResults.isEmpty()) {
			port = serviceQueryResults.get(0).port;
		}

		tryConnect(new HostAddressPort(address, port));
	}
    }

    private void tryConnect(HostAddressPort target) {
       	assert currentConnection == null;
	//std::cout << "Connector::tryConnect() " << target.getAddress().toString() << " " << target.getPort() << std::endl;
	currentConnection = connectionFactory.createConnection();
	currentConnectionConnectFinishedConnection = currentConnection.onConnectFinished.connect(new Slot1<Boolean>() {
            public void call(Boolean p1) {
                handleConnectionConnectFinished(p1);
            }
        });

	currentConnection.connect(target);
    }

    private void handleConnectionConnectFinished(boolean error) {
        //std::cout << "Connector::handleConnectionConnectFinished() " << error << std::endl;
	currentConnectionConnectFinishedConnection.disconnect();
	if (error) {
		currentConnection = null;
		if (!addressQueryResults.isEmpty()) {
			tryNextAddress();
		}
		else {
			if (!serviceQueryResults.isEmpty()) {
				serviceQueryResults.remove(0);
			}
			tryNextServiceOrFallback();
		}
	}
	else {
		finish(currentConnection);
	}
    }

    private void finish(Connection connection) {
      	if (timer != null) {
		timer.stop();
		timer.onTick.disconnectAll();
		timer = null;
	}
	if (serviceQuery != null) {
		serviceQuery.onResult.disconnectAll();
		serviceQuery = null;
	}
	if (addressQuery != null) {
		addressQuery.onResult.disconnectAll();
		addressQuery = null;
	}
	if (currentConnection != null) {
		currentConnectionConnectFinishedConnection.disconnect();
		currentConnection = null;
	}
	onConnectFinished.emit(connection);
    }

    private void handleTimeout() {
        finish(null);
    }
    private String hostname;
    private DomainNameResolver resolver;
    private ConnectionFactory connectionFactory;
    private TimerFactory timerFactory;
    private int timeoutMilliseconds = 0;
    private Timer timer;
    private DomainNameServiceQuery serviceQuery;
    private ArrayList<DomainNameServiceQuery.Result> serviceQueryResults;
    private DomainNameAddressQuery addressQuery;
    private ArrayList<HostAddress> addressQueryResults;
    private boolean queriedAllServices = true;
    private Connection currentConnection;
    private SignalConnection currentConnectionConnectFinishedConnection;
}
