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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.Error;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.URL;
import com.isode.stroke.network.BOSHConnection.BOSHError.Type;
import com.isode.stroke.parser.BOSHBodyExtractor;
import com.isode.stroke.parser.BOSHBodyExtractor.BOSHBody;
import com.isode.stroke.parser.XMLParserFactory;
import com.isode.stroke.session.SessionStream.SessionStreamError;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.streamstack.DummyStreamLayer;
import com.isode.stroke.streamstack.HighLayer;
import com.isode.stroke.streamstack.TLSLayer;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.CertificateWithKey;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.tls.TLSError;
import com.isode.stroke.tls.TLSOptions;

public class BOSHConnection {

    private final URL boshURL_;
    private Connector connector_;
    private final XMLParserFactory parserFactory_;
    private Connection connection_;
    private final HighLayer dummyLayer_;
    private final TLSLayer tlsLayer_;
    private String sid_ = "";
    private boolean waitingForStartResponse_ = false;
    private long rid_ = 0;
    private final SafeByteArray buffer_ = new SafeByteArray();
    private boolean pending_ = false;
    private boolean connectionReady_ = false;
    
    public final Signal1<Boolean> onConnectionFinished = new Signal1<Boolean>();
    public final Signal1<Boolean> onDisconnected = new Signal1<Boolean>();
    public final Signal1<BOSHError> onSessionTerminated = new Signal1<BOSHError>();
    public final Signal2<String, Integer> onSessionStarted = new Signal2<String, Integer>();
    public final Signal1<SafeByteArray> onXMPPDataRead = new Signal1<SafeByteArray>();
    public final Signal1<SafeByteArray> onBOSHDataRead = new Signal1<SafeByteArray>();
    public final Signal1<SafeByteArray> onBOSHDataWritten = new Signal1<SafeByteArray>();
    public final Signal1<String> onHTTPError = new Signal1<String>();
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private SignalConnection onConnectFinishedConnection;
    
    public static class BOSHError extends com.isode.stroke.session.SessionStream.SessionStreamError {
        
        public enum Type {
            BadRequest, HostGone, HostUnknown, ImproperAddressing,
            InternalServerError, ItemNotFound, OtherRequest, PolicyViolation,
            RemoteConnectionFailed,RemoteStreamError,SeeOtherURI,SystemShutdown,
            UndefinedCondition,NoError;
        }
        
        public BOSHError(Type type) {
            super(SessionStreamError.Type.ConnectionReadError);
            type_ = type;
        }
        
        public Type getType() {
            return type_;
        }
        
        private final Type type_;
        
    }
    
    public static class Pair<T1,T2> {
        public final T1 first;
        public final T2 second;
        
        public Pair(T1 first,T2 second) {
           this.first = first;
           this.second = second;
        }
    }
    
    private BOSHConnection(URL url,Connector connector,XMLParserFactory parserFactory,
            TLSContextFactory tlsContextFactory,TLSOptions tlsOptions) {
        boshURL_ = url;
        connector_ = connector;
        parserFactory_ = parserFactory;
        if ("https".equals(boshURL_.getScheme())) {
            tlsLayer_ = new TLSLayer(tlsContextFactory, tlsOptions);
            dummyLayer_ = new DummyStreamLayer(tlsLayer_);
        }
        else {
            tlsLayer_ = null;
            dummyLayer_ = null;
        }
    }
    
    public static BOSHConnection create(URL url,Connector connector,
            XMLParserFactory parserFactory,TLSContextFactory tlsContextFactory,TLSOptions tlsOptions) {
        return new BOSHConnection(url, connector, parserFactory, tlsContextFactory, tlsOptions);
    }
    
    public void connect() {
        onConnectFinishedConnection = connector_.onConnectFinished.connect(new Slot2<Connection, Error>() {
            
            @Override
            public void call(Connection connection, Error error) {
                handleConnectFinished(connection);
            }
            
        });
        connector_.start();
    }
    
    public void disconnect() {
        if (connection_ != null) {
            connection_.disconnect();
            sid_ = "";
        }
        else {
            handleDisconnected(null);
        }
    }
    
    public void write(SafeByteArray data) {
        write(data,false,false);
    }
    
    private void write(SafeByteArray data, boolean streamRestart, boolean terminate) {
        assert(connectionReady_);
        assert(!sid_.isEmpty());

        SafeByteArray safeHeader =
                createHTTPRequest(data, streamRestart, terminate, rid_, sid_, boshURL_).first;

        onBOSHDataWritten.emit(safeHeader);
        writeData(safeHeader);
        pending_ = true;

        String logMessage = "write data: " + safeHeader.toString() + "\n";
        logger.log(Level.FINE,logMessage);
    }

    public String getSID() {
        return sid_;
    }
    
    public void setRID(long rid) {
        rid_ = rid;
    }
    
    public void setSID(String sid) {
        sid_ = sid;
    }
    
    public void startStream(String to,long rid) {
        assert(connectionReady_);
        
        String content = "<body content='text/xml; charset=utf-8'"
                + " hold='1'"
                + " to='" + to + "'"
                + " rid='" + rid + "'"
                + " ver='1.6'"
                + " wait='60'" 
                + " xml:lang='en'"
                + " xmlns:xmpp='urn:xmpp:bosh'"
                + " xmpp:version='1.0'"
                + " xmlns='http://jabber.org/protocol/httpbind' />";

        StringBuilder headerBuilder = new StringBuilder("POST " + boshURL_.getPath() + " HTTP/1.1\r\n"
                + "Host: " + boshURL_.getHost());
        Integer boshPort = boshURL_.getPort();
        if (boshPort != null) {
            headerBuilder.append(":");
            headerBuilder.append(boshPort);
        }
        headerBuilder.append("\r\n");
        headerBuilder.append("Accept-Encoding: deflate\r\n");
        headerBuilder.append("Content-Type: text/xml; charset=utf-8\r\n");
        headerBuilder.append("Content-Length: ");
        headerBuilder.append(content.length());
        headerBuilder.append("\r\n\r\n");
        headerBuilder.append(content);
        
        waitingForStartResponse_ = true;
        SafeByteArray safeHeader = new SafeByteArray(headerBuilder.toString());
        onBOSHDataWritten.emit(safeHeader);
        writeData(safeHeader);
        logger.fine("write stream header: "+safeHeader.toString()+"\n");
    }
    
    public void terminateStream() {
        write(new SafeByteArray(),false,true);
    }
    
    public boolean isReadyToSend() {
        // Without pipelining you need to not send more without first receiving the response
        // With pipelining you can. Assuming we can't, here
        return connectionReady_ && !pending_ && !waitingForStartResponse_ && !sid_.isEmpty();
    }
    
    public void restartStream() {
        write(new SafeByteArray(),true,false);
    }
    
    public boolean setClientCertificate(CertificateWithKey cert) {
        if (tlsLayer_ != null) {
            logger.fine("set client certificate\n");
            return tlsLayer_.setClientCertificate(cert);
        }
        else {
            return false;
        }
    }
    
    public Certificate getPeerCertificate() {
        if (tlsLayer_ != null) {
            return tlsLayer_.getPeerCertificate();
        }
        return null;
    }
    
    public CertificateVerificationError getPeerCertificateVerficationError() {
        if (tlsLayer_ != null) {
            return tlsLayer_.getPeerCertificateVerificationError();
        }
        return null;
    }
    
    public List<Certificate> getPeerCertificateChain() {
        if (tlsLayer_ != null) {
            return tlsLayer_.getPeerCertificateChain();
        }
        return new ArrayList<Certificate>();
    }
    
    protected static Pair<SafeByteArray,Integer> createHTTPRequest(SafeByteArray data,
            boolean streamRestart,boolean terminate,long rid,String sid,URL boshURL) {
        int size = 0;
        StringBuilder contentBuilder = new StringBuilder();
        SafeByteArray contentTail = new SafeByteArray("</body>");
        StringBuilder headerBuilder = new StringBuilder();
        
        contentBuilder.append("<body rid='");
        contentBuilder.append(rid);
        contentBuilder.append("' sid='");
        contentBuilder.append(sid);
        contentBuilder.append("'");
        if (streamRestart) {
            contentBuilder.append(" xmpp:restart='true' xmlns:xmpp='urn:xmpp:xbosh'");
        }
        if (terminate) {
            contentBuilder.append(" type='terminate'");
        }
        contentBuilder.append(" xmlns='http://jabber.org/protocol/httpbind'>");
        
        SafeByteArray safeContent = new SafeByteArray(contentBuilder.toString());
        safeContent.append(data);
        safeContent.append(contentTail);
        
        size = safeContent.getSize();
        
        headerBuilder.append("POST " + boshURL.getPath() + " HTTP/1.1\r\n"
                + "Host: " + boshURL.getHost());
        Integer boshPort = boshURL.getPort();
        if (boshPort != null) {
            headerBuilder.append(":");
            headerBuilder.append(boshPort);
        }
        headerBuilder.append("\r\n");
        headerBuilder.append("Accept-Encoding: deflate\r\n");
        headerBuilder.append("Content-Type: text/xml; charset=utf-8\r\n");
        headerBuilder.append("Content-Length: ");
        headerBuilder.append(size);
        headerBuilder.append("\r\n\r\n");
        
        SafeByteArray safeHeader = new SafeByteArray(headerBuilder.toString());
        safeHeader.append(safeContent);
        
        return new Pair<SafeByteArray, Integer>(safeHeader, size);
    }
    
    private void handleConnectFinished(Connection connection) {
        cancelConnector();
        connectionReady_ = (connection != null);
        if (connectionReady_) {
            connection_ = connection;
            if (tlsLayer_ != null) {
                connection_.onDataRead.connect(new Slot1<SafeByteArray>() {

                    @Override
                    public void call(SafeByteArray data) {
                        handleRawDataRead(data);
                    }

                });
                connection_.onDisconnected.connect(new Slot1<Connection.Error>() {

                    @Override
                    public void call(
                            com.isode.stroke.network.Connection.Error error) {
                        handleDisconnected(error);
                    }

                });

                tlsLayer_.getContext().onDataForNetwork.connect(new Slot1<SafeByteArray>() {

                    @Override
                    public void call(SafeByteArray data) {
                        handleTLSNetworkDataWriteRequest(data);
                    }

                });
                tlsLayer_.getContext().onDataForApplication.connect(new Slot1<SafeByteArray>() {

                    @Override
                    public void call(SafeByteArray data) {
                        handleTLSApplicationDataRead(data);
                    }

                });

                tlsLayer_.onConnected.connect(new Slot() {

                    @Override
                    public void call() {
                        handleTLSConnected();
                    }

                });

                tlsLayer_.onError.connect(new Slot1<TLSError>() {

                    @Override
                    public void call(TLSError error) {
                        handleTLSError(error);
                    }

                });
                tlsLayer_.connect();
            }
            else {
                connection_.onDataRead.connect(new Slot1<SafeByteArray>() {

                    @Override
                    public void call(SafeByteArray data) {
                        handleDataRead(data);
                    }

                });
                connection_.onDisconnected.connect(new Slot1<Connection.Error>() {

                    @Override
                    public void call(Connection.Error error) {
                        handleDisconnected(error);
                    }

                });
            }

            if (!connectionReady_ || tlsLayer_ == null) {
                onConnectionFinished.emit(!connectionReady_);
            }
        }
    }
    
    private void handleDataRead(SafeByteArray data) {
        onBOSHDataRead.emit(data);
        buffer_.append(data);
        String response = buffer_.toString();
        if (!response.contains("\r\n\r\n")) {
            onBOSHDataRead.emit(new SafeByteArray("[[Previous read incomplete, pending]]"));
            return;
        }
        int httpCodeIndex = response.indexOf(" ")+1;
        String httpCode = response.substring(httpCodeIndex,httpCodeIndex+3);
        if (!"200".equals(httpCode)) {
            onHTTPError.emit(httpCode);
            return;
        }
        ByteArray boshData = new ByteArray(response.substring(response.indexOf("\r\n\r\n")));
        BOSHBodyExtractor parser = new BOSHBodyExtractor(parserFactory_, boshData);
        BOSHBody boshBody = parser.getBody();
        if (boshBody != null) {
            String typeAttribute = boshBody.getAttributes().getAttribute("type");
            if ( "terminate".equals(typeAttribute) ) {
                String conditionAttribute = boshBody.getAttributes().getAttribute("condition");
                BOSHError.Type errorType = parseTerminationCondition(conditionAttribute);
                onSessionTerminated.emit(errorType == BOSHError.Type.NoError ? null : new BOSHError(errorType));
            }
            buffer_.clear();
            if (waitingForStartResponse_) {
                waitingForStartResponse_ = false;
                sid_ = boshBody.getAttributes().getAttribute("sid");
                String requestsString = boshBody.getAttributes().getAttribute("requests");
                Integer requests = Integer.valueOf(2);
                if (requestsString != null && !requestsString.isEmpty()) {
                    try {
                        requests = Integer.valueOf(requestsString);
                    } catch (NumberFormatException e) {
                        requests = Integer.valueOf(2);
                    }
                }
                onSessionStarted.emit(sid_, requests);
            }
            
            SafeByteArray payload = new SafeByteArray(boshBody.getContent());
            /* Say we're good to go again, so don't add anything after here in the method */
            pending_ = false;
            onXMPPDataRead.emit(payload);
        }
    }
    
    private void handleDisconnected(Connection.Error error) {
        cancelConnector();
        onDisconnected.emit(error != null ? Boolean.TRUE : Boolean.FALSE);
        sid_ = "";
        connectionReady_ = false;
    }
    
    private Type parseTerminationCondition(String text) {
        Type condition = Type.UndefinedCondition;
        if ("bad-request".equals(text)) {
            condition = Type.BadRequest;
        }
        else if ("host-gone".equals(text)) {
            condition = Type.HostGone;
        }
        else if ("host-unknown".equals(text)) {
            condition = Type.HostUnknown;
        }
        else if ("improper-addressing".equals(text)) {
            condition = Type.ImproperAddressing;
        }
        else if ("internal-server-error".equals(text)) {
            condition = Type.InternalServerError;
        }
        else if ("item-not-found".equals(text)) {
            condition = Type.ItemNotFound;
        }
        else if ("other-request".equals(text)) {
            condition = Type.OtherRequest;
        }
        else if ("policy-violation".equals(text)) {
            condition = Type.PolicyViolation;
        }
        else if ("remote-connection-failed".equals(text)) {
            condition = Type.RemoteConnectionFailed;
        }
        else if ("remote-stream-error".equals(text)) {
            condition = Type.RemoteStreamError;
        }
        else if ("see-other-uri".equals(text)) {
            condition = Type.SeeOtherURI;
        }
        else if ("system-shutdown".equals(text)) {
            condition = Type.SystemShutdown;
        }
        else if ("".equals(text)) {
            condition = Type.NoError;
        }
        return condition;
    }
    
    private void cancelConnector() {
        if (connector_ != null) {
            if (onConnectFinishedConnection != null) {
                onConnectFinishedConnection.disconnect();
            }
            connector_.stop();
            connector_ = null;
        }
    }

    private void handleTLSConnected() {
        logger.fine("\n");
        onConnectionFinished.emit(Boolean.FALSE);
    }
    
    private void handleTLSApplicationDataRead(SafeByteArray data) {
        logger.fine("\n");
        handleDataRead(new SafeByteArray(data));
    }
    
    private void handleTLSNetworkDataWriteRequest(SafeByteArray data) {
        logger.fine("\n");
        connection_.write(data);
    }
    
    private void handleRawDataRead(SafeByteArray data) {
        logger.fine("\n");
        tlsLayer_.handleDataRead(data);
    }
    
    private void handleTLSError(TLSError error) {
        // Empty Method
    }

    private void writeData(SafeByteArray data) {
        if (tlsLayer_ != null) {
            tlsLayer_.writeData(data);
        }
        else {
            connection_.write(data);
        }
    }

}
