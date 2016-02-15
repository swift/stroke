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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.SafeString;
import com.isode.stroke.base.URL;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.network.BOSHConnection.BOSHError;
import com.isode.stroke.parser.XMLParserFactory;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.CertificateWithKey;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.tls.TLSOptions;

public class BOSHConnectionPool {

    private final URL boshURL_;
    private ConnectionFactory connectionFactory_;
    private final XMLParserFactory xmlParserFactory_;
    private final TimerFactory timerFactory_;
    private final List<BOSHConnection> connections_ = new ArrayList<BOSHConnection>();
    private final Map<BOSHConnection, Set<SignalConnection>> connectionsSignalConnections_
        = new HashMap<BOSHConnection, Set<SignalConnection>>();
    private String sid_ = "";
    private long rid_;
    private final List<SafeByteArray> dataQueue_ = new ArrayList<SafeByteArray>();
    private boolean pendingTerminate_;
    private String to_;
    private int requestLimit_;
    private int restartCount_;
    private boolean pendingRestart_;
    private List<ConnectionFactory> myConnectionFactories_;
    private final CachingDomainNameResolver resolver_;
    private CertificateWithKey clientCertificate_;
    private TLSContextFactory tlsContextFactory_;
    private TLSOptions tlsOptions_;
    private final List<Certificate> pinnedCertificateChain_ = new ArrayList<Certificate>();
    private CertificateVerificationError lastVerificationError_;
    
    public final Signal1<BOSHError> onSessionTerminated = new Signal1<BOSHError>();
    public final Signal onSessionStarted = new Signal();
    public final Signal1<SafeByteArray> onXMPPDataRead = new Signal1<SafeByteArray>();
    public final Signal1<SafeByteArray> onBOSHDataRead = new Signal1<SafeByteArray>();
    public final Signal1<SafeByteArray> onBOSHDataWritten = new Signal1<SafeByteArray>();
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public BOSHConnectionPool(URL boshURL,DomainNameResolver resolver,
            ConnectionFactory connectionFactory, XMLParserFactory parserFactory,
            TLSContextFactory tlsFactory, TimerFactory timerFactory, EventLoop eventLoop,
            String to,long initialRID,URL boshHTTPConnectProxyURL,
            SafeString boshHTTPConnectProxyAuthID,SafeString boshHTTPConnectProxyAuthPassword,
            TLSOptions tlsOptions) {
        this(boshURL, resolver, connectionFactory, parserFactory, tlsFactory, timerFactory, 
                eventLoop, to, initialRID, boshHTTPConnectProxyURL, boshHTTPConnectProxyAuthID, 
                boshHTTPConnectProxyAuthPassword, tlsOptions, null);
    }
    
    public BOSHConnectionPool(URL boshURL,DomainNameResolver realResolver,
            ConnectionFactory connectionFactory, XMLParserFactory parserFactory,
            TLSContextFactory tlsFactory, TimerFactory timerFactory, EventLoop eventLoop,
            String to,long initialRID,URL boshHTTPConnectProxyURL,
            SafeString boshHTTPConnectProxyAuthID,SafeString boshHTTPConnectProxyAuthPassword,
            TLSOptions tlsOptions,HTTPTrafficFilter trafficFilter) {
        boshURL_ = boshURL;
        connectionFactory_ = connectionFactory;
        xmlParserFactory_ = parserFactory;
        timerFactory_ = timerFactory;
        rid_ = initialRID;
        pendingTerminate_ = false;
        to_ = to;
        requestLimit_ = 2;
        restartCount_ = 0;
        pendingRestart_ = false;
        tlsContextFactory_ = tlsFactory;
        tlsOptions_ = tlsOptions;
        if (!boshHTTPConnectProxyURL.isEmpty()) {
            this.connectionFactory_ = 
                    new HTTPConnectProxiedConnectionFactory(realResolver, connectionFactory, 
                            timerFactory, boshHTTPConnectProxyURL.getHost(), 
                            URL.getPortOrDefaultPort(boshHTTPConnectProxyURL), 
                            boshHTTPConnectProxyAuthID.getData(), 
                            boshHTTPConnectProxyAuthPassword.getData(), trafficFilter);
        }
        resolver_ = new CachingDomainNameResolver(realResolver, eventLoop);
    }
    
    public void open() {
        createConnection();
    }
    
    public void write(SafeByteArray data) {
        dataQueue_.add(data);
        tryToSendQueuedData();
    }
    
    public void writeFooter() {
        pendingTerminate_ = true;
        tryToSendQueuedData();
    }
    
    public void close() {
        if (!sid_.isEmpty()) {
            writeFooter();
        }
        else {
            pendingTerminate_ = true;
            List<BOSHConnection> connectionCopies = new ArrayList<BOSHConnection>(connections_);
            for(BOSHConnection connection : connectionCopies) {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
    
    public void restartStream() {
        BOSHConnection connection = getSuitableConnection();
        if (connection != null) {
            pendingRestart_ = false;
            rid_++;
          connection.setRID(rid_);
          connection.restartStream();
          restartCount_++;
        }
        else {
            pendingRestart_ = true;
        }
    }
    
    public void setTLSCertificate(CertificateWithKey certWithKey) {
        clientCertificate_ = certWithKey;
    }

    public boolean isTLSEncrypted() {
        return !pinnedCertificateChain_.isEmpty();
    }

    public Certificate getPeerCertificate() {
        Certificate peerCertificate = null;
        if (!pinnedCertificateChain_.isEmpty()) {
            peerCertificate = pinnedCertificateChain_.get(0);
        }
        return peerCertificate;
    }
    
    
    public List<Certificate> getPeerCertificateChain() {
        return new ArrayList<Certificate>(pinnedCertificateChain_);
    }
    
    public CertificateVerificationError getPeerCertificateVerificationError() {
        return lastVerificationError_;
    }
    
    private void handleDataRead(SafeByteArray data) {
        onXMPPDataRead.emit(data);
        tryToSendQueuedData(); // Will rebalance the connections 
    }
    
    private void handleSessionStarted(String sid, int requests) {
        sid_ = sid;
        requestLimit_ = requests;
        onSessionStarted.emit();
    }
    
    private void handleBOSHDataRead(SafeByteArray data) {
        onBOSHDataRead.emit(data);
    }
    
    private void handleBOSHDataWritten(SafeByteArray data) {
        onBOSHDataWritten.emit(data);
    }
    
    private void handleSessionTerminated(BOSHError error) {
        onSessionTerminated.emit(error);
    }
    
    private void handleConnectFinished(boolean error, BOSHConnection connection) {
        if (error) {
            onSessionTerminated.emit(new BOSHError(BOSHError.Type.UndefinedCondition));
        }
        else {
            if ((connection.getPeerCertificate() != null) && pinnedCertificateChain_.isEmpty()) {
                pinnedCertificateChain_.clear();
                pinnedCertificateChain_.addAll(connection.getPeerCertificateChain());
            }
            if (!pinnedCertificateChain_.isEmpty()) {
                lastVerificationError_ = connection.getPeerCertificateVerficationError();
            }

            if (sid_.isEmpty()) {
                connection.startStream(to_, rid_);
            }
            if (pendingRestart_) {
                restartStream();
            }
            tryToSendQueuedData();
        }
    }
    
    private void handleConnectionDisconnected(boolean error, BOSHConnection connection) {
        destroyConnection(connection);
        if (pendingTerminate_ && sid_.isEmpty() && connections_.isEmpty()) {
            handleSessionTerminated(null);
        }
        else {
            /* We might have just freed up a connection slot to send with */
            tryToSendQueuedData();
        }
    }
    
    private void handleHTTPError(String errorCode) {
        handleSessionTerminated(new BOSHError(BOSHError.Type.UndefinedCondition));
    }
    
    private BOSHConnection createConnection() {
        Connector connector = Connector.create(boshURL_.getHost(), 
                URL.getPortOrDefaultPort(boshURL_), null, resolver_, 
                connectionFactory_, timerFactory_);
        final BOSHConnection connection = BOSHConnection.create(boshURL_, connector, 
                xmlParserFactory_, tlsContextFactory_, tlsOptions_);
        Set<SignalConnection> signalConnections = new HashSet<SignalConnection>();
        signalConnections.add(connection.onXMPPDataRead.connect(new Slot1<SafeByteArray>() {
            
            @Override
            public void call(SafeByteArray data) {
                handleDataRead(data);
            }
            
        }));
        signalConnections.add(connection.onSessionStarted.connect(new Slot2<String, Integer>() {
            
            @Override
            public void call(String sid, Integer requests) {
                handleSessionStarted(sid, requests.intValue());
            }
            
        }));
        signalConnections.add(connection.onBOSHDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleBOSHDataRead(data);
            }
            
        }));
        signalConnections.add(connection.onBOSHDataWritten.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleBOSHDataWritten(data);
            }
            
        }));
        signalConnections.add(connection.onDisconnected.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean wasError) {
                handleConnectionDisconnected(wasError.booleanValue(), connection);
            }
            
        }));
        signalConnections.add(connection.onConnectionFinished.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean wasError) {
                handleConnectFinished(wasError.booleanValue(), connection);
            }
            
        }));
        signalConnections.add(connection.onSessionTerminated.connect(new Slot1<BOSHConnection.BOSHError>() {

            @Override
            public void call(BOSHError error) {
                handleSessionTerminated(error);
            }
            
        }));
        signalConnections.add(connection.onHTTPError.connect(new Slot1<String>() {

            @Override
            public void call(String httpErrorCode) {
                handleHTTPError(httpErrorCode);
            }
            
        }));

        if ("https".equals(boshURL_.getScheme())) {
            boolean success = connection.setClientCertificate(clientCertificate_);
            logger.fine("setClientCertificate, success: " + success + "\n");
        }

        connection.connect();
        connections_.add(connection);
        connectionsSignalConnections_.put(connection, signalConnections);
        return connection;
        
    }
    
    private void destroyConnection(BOSHConnection connection) {
          while (connections_.remove(connection)) {
              // Loop will run till all instances of connection are removed
          }
          Set<SignalConnection> signalConnections = connectionsSignalConnections_.remove(connection);
          if (signalConnections != null) {
              for (SignalConnection signalConnection : signalConnections) {
                  if (signalConnection != null) {
                      signalConnection.disconnect();
                  }
              }
              signalConnections.clear();
          }
    }
    
    private void tryToSendQueuedData() {
        if (sid_.isEmpty()) {
            // If we've not got as far as stream start yet, pend 
            return;
        }

        BOSHConnection suitableConnection = getSuitableConnection();
        boolean toSend = !dataQueue_.isEmpty();
        if (suitableConnection != null) {
            if (toSend) {
                rid_++;
                suitableConnection.setRID(rid_);
                SafeByteArray data = new SafeByteArray();
                for(SafeByteArray datum : dataQueue_) {
                    data.append(datum);
                }
                suitableConnection.write(data);
                dataQueue_.clear();
            }
            else if (pendingTerminate_) {
                rid_++;
                suitableConnection.setRID(rid_);
                suitableConnection.terminateStream();
                sid_ = "";
                close();
            }
        }
        if (!pendingTerminate_) {
            // Ensure there's always a session waiting to read data for us
            boolean pending = false;
            for(BOSHConnection connection : connections_) {
                if (connection != null && !connection.isReadyToSend()) {
                    pending = true;
                }
            }
            if (!pending) {
                if (restartCount_ >= 1) {
                    // Don't open a second connection until we've restarted the stream twice - i.e. we've authed and resource bound
                    if (suitableConnection != null) {
                        rid_++;
                        suitableConnection.setRID(rid_);
                        suitableConnection.write(new SafeByteArray());
                    }
                    else {
                        // My thought process I went through when writing this, to aid anyone else confused why this can happen...
                        //
                        // What to do here? I think this isn't possible.
                        // If you didn't have two connections, suitable would have made one.
                        // If you have two connections and neither is suitable, pending would be true.
                        // If you have a non-pending connection, it's suitable.
                        //
                        // If I decide to do something here, remove assert above.
                        //
                        // Ah! Yes, because there's a period between creating the connection and it being connected. */
                    }
                }
            }
        }
    }
    
    private BOSHConnection getSuitableConnection() {
        BOSHConnection suitableConnection = null;
        for(BOSHConnection connection : connections_) {
            if (connection.isReadyToSend()) {
                suitableConnection = connection;
                break;
            }
        }

        if (suitableConnection == null && (connections_.size() < requestLimit_)) {
            // This is not a suitable connection because it won't have yet connected and added TLS if needed.
            BOSHConnection newConnection = createConnection();
            newConnection.setSID(sid_);
        }
        assert(connections_.size() <= requestLimit_);
        assert((suitableConnection == null) || suitableConnection.isReadyToSend());
        return suitableConnection;
    }
    
}
