/*  Copyright (c) 2012-2013, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written licence agreement from Isode Limited,
 *  or a written licence from an organisation licensed by Isode Limited Limited
 *  to grant such a licence.
 *
 */
 
package com.isode.stroke.tls.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509ExtendedKeyManager;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.tls.CAPICertificate;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.CertificateVerificationError.Type;
import com.isode.stroke.tls.CertificateWithKey;
import com.isode.stroke.tls.PKCS12Certificate;
import com.isode.stroke.tls.TLSContext;



/**
 * Concrete implementation of a TLSContext which uses SSLEngine 
 * and maybe other stuff? ..tbs...
 * 
 */
public class JSSEContext extends TLSContext {

	
    
    private static class JSSEContextError {
        public final Exception exception;
        public final String message;
        /**
         * Create a new object
         * @param e exception; may be null
         * @param m message; may be null
         */
        public JSSEContextError(Exception e, String m) {
            exception = e;
            message = m;
        }
        @Override
        public String toString() {
            return "JSSEContextError: " +
                    (message == null ? "No message" : message) + "; " +
                    (exception == null ? "No exception" : exception.getMessage());
        }
    }
    /**
     * If an error occurs, it will be added to this vector
     */
    private Vector<JSSEContextError> errorsEmitted = new Vector<JSSEContextError>();
    
    /**
     * Whether the handshake has finished
     */
    private boolean handshakeCompleted = false;

    /**
     * Whether the server has sent a close notification
     */
    private boolean closeNotifyReceived = false;

    /**
     * Determine whether an error has occurred.  If an error has occurred, then
     * you probably don't want to try doing any more stuff.
     * 
     * @return <em>true</em> if an error has occurred, <em>false</em>
     * otherwise
     */
    private boolean hasError() {
        return (!errorsEmitted.isEmpty());
    }
    /**
     * Emit an error, and keep track of which errors have been emitted
     * @param e the Exception which caused this error (may be null)
     * @param m a String describing what caused this error (may be null)
     */
    private void emitError(Exception e, String m) {
        JSSEContextError jsseContextError = new JSSEContextError(e, m);
        /* onError.emit() won't provide any info about what the error was,
         * so log a warning here as well
         */
        logger_.log(Level.WARNING, jsseContextError.toString(), e);
        errorsEmitted.add(jsseContextError);
        onError.emit();        
    }
    
    @Override
    public void connect() {
        try {
            doSetup();            
        }
        catch (SSLException e) {
            emitError(e,"doSetup() failed");
        }       
    }


    private void doSetup() throws SSLException {
        SSLContext sslContext = getSSLContext();
        
        if (sslContext == null) {
            throw new SSLException("Could not create SSLContext");
        }
        
        sslEngine = null;
        try {
            sslEngine = sslContext.createSSLEngine();
        }
        catch (UnsupportedOperationException e) {
            /* "the underlying provider does not implement the operation" */
            throw new SSLException(e);
        }
        catch (IllegalStateException e) {
            /* "the SSLContextImpl requires initialization and init() has not been called" */
            throw new SSLException(e);
        }
        
        /* Restrict cipher suites if necessary */
        if (restrictedCipherSuites != null) {
            String[] supportedSuites = sslEngine.getSupportedCipherSuites();
            Set<String> matchedSuites = new HashSet<String>();
            for (String suite:supportedSuites) {
                if (restrictedCipherSuites.contains(suite)) {
                    matchedSuites.add(suite);
                }
            }
            String[] suitesToEnable = new String[]{};
            if (!matchedSuites.isEmpty()) {
                suitesToEnable = matchedSuites.toArray(new String[matchedSuites.size()]);
            }
        
            sslEngine.setEnabledCipherSuites(suitesToEnable);
        }
                
        sslEngine.setUseClientMode(true); /* I am a client */
        sslEngine.setEnableSessionCreation(true); /* can create new sessions */
        

        /* Will get "the current size of the largest application data that is
         * expected when using this session". 
         * 
         * If we get packets larger than this, we'll grow the buffers by this
         * amount.
         */
        appBufferSize = sslEngine.getSession().getApplicationBufferSize();
                
        /* "A SSLEngine using this session may generate SSL/TLS packets of
         * any size up to and including the value returned by this method"
         * 
         * Note though, that this doesn't mean we might not be asked to 
         * process data chunks that are larger than this: we cannot rely on this 
         * value being big enough to hold anything that comes in through
         * "handleDataFromNetwork()".
         */        
        netBufferSize = sslEngine.getSession().getPacketBufferSize();
        
        /* All buffers are normally in "write" mode. Access to all of them
         * must be synchronized
         */
        plainToSend = ByteBuffer.allocate(appBufferSize + 50);
        wrappedToSend = ByteBuffer.allocate(netBufferSize);
        
        encryptedReceived = ByteBuffer.allocate(netBufferSize);

        unwrappedReceived = ByteBuffer.allocate(appBufferSize + 50); 

       
        /* Note that calling beginHandshake might not actually do anything; 
         * the SSLEngine may not actually send the handshake until it's had 
         * some data from the application.  And the higher level won't send 
         * any data until it thinks the handshake is completed.  
         *
         * So as well as calling beginHandshake(), we also call wrapAndSendData()
         * even though there is actually no data yet to send.  But sending this
         * empty buffer will prompt the SSLEngine into doing the handshake.
         */
        
        sslEngine.beginHandshake();
        wrapAndSendData();
                
    }

    
    /**
     * Unwrap any data in the "encryptedReceived" buffer and put it into
     * the "unwrappedReceived" buffer. An event will be generated to the
     * end-user's listener if there's anything pending in the 
     * unwrappedReceived buffer.  Caller should check handshake status
     * after this returns
     * 
     * @return the number of bytes that SSLEngine consumed
     */
    private int unwrapPendingData()
    {
        SSLEngineResult sslEngineResult;
        Status status;   
        int bytesProduced = 0;
        int bytesConsumed = 0;
        int bytesToUnwrap = 0;
        HandshakeStatus handshakeStatus = null;
        ByteArray byteArray = null;

        synchronized(recvMutex) {
            try {
                encryptedReceived.flip();
                
                boolean unwrapDone = false;
                do {
                    bytesToUnwrap = encryptedReceived.remaining();
                    sslEngineResult = sslEngine.unwrap(encryptedReceived, unwrappedReceived);
                    status = sslEngineResult.getStatus();
                    handshakeStatus = sslEngineResult.getHandshakeStatus();
                    /* A call to unwrap can generate a status of FINISHED, which
                     * you won't get from SSLEngine.getHandshakeStatus.  Such
                     * a status is an indication that we need to re-check whether
                     * anything's pending to be written
                     */
                    if (handshakeStatus == HandshakeStatus.FINISHED ||
			(!handshakeCompleted &&
			 handshakeStatus == HandshakeStatus.NOT_HANDSHAKING)) {
                        /* Special case will happen when the handshake completes following
                         * an unwrap.  The first time we tried wrapping some plain stuff,
                         * it triggers the handshake but won't itself have been dealt with.
                         * So now the handshake has finished, we have to try sending it
                         * again
			 * The second checking clause is necessary for certain
			 * SSLEngine implementations (notably Apache Harmony
			 * used on Android) which never return FINISHED
			 */

                        handshakeCompleted = true;
                        wrapAndSendData();
                        onConnected.emit();
                        status = sslEngineResult.getStatus();
                    }

                    
                    switch (status) {
                    case BUFFER_OVERFLOW :
                        unwrappedReceived = getLargerBuffer("unwrappedReceived",unwrappedReceived,appBufferSize);
                        unwrapDone = false;
                        break;
                        
                    case BUFFER_UNDERFLOW:
                        /* There's not enough data yet for engine to be able to decode
                         * a full message. Not a problem; assume that more will come
                         * in to the socket eventually
                         */
                        unwrapDone = true;
                        break;
		    case CLOSED:
			/* This is taken to mean that the server end has
			 * sent "SSL close notify alert".  Once it sees this,
			 * ths SSLEngine will respond by generating an
			 * appropriate handshake response that should be
			 * sent to the server (as per RFC 2246 7.2.1).  
			 * In this case, the SSLEngine will move into a
			 * NEED_WRAP state, and we should emit whatever data
			 * it's generated over the network.
			 */
			closeNotifyReceived = true;

			/* Tell the SSLEngine that the application won't be
			 * sending any more data (this probably has no effect
			 * but it does no harm).
			 */
			sslEngine.closeOutbound();
			return bytesConsumed;

		    case OK:
                        /* Some stuff was unwrapped. */
                        bytesConsumed += sslEngineResult.bytesConsumed();
                        bytesProduced = sslEngineResult.bytesProduced();
                        
                        /* It may be that the unwrap consumed some, but not all of
                         * the data. In which case, the loop continues to give it
                         * another chance to process whatever's remaining
                         */
                        if (sslEngineResult.bytesConsumed() == 0) {
                            /* No point looping around again */
                            unwrapDone = true;
                        }
                        else {
                            /* It consumed some bytes, but perhaps not everything */
                            unwrapDone = (sslEngineResult.bytesConsumed() == bytesToUnwrap);
                        }
                        break;
                    }
                } while (!unwrapDone);
                
                encryptedReceived.compact();



                bytesConsumed += sslEngineResult.bytesConsumed();
                bytesProduced = sslEngineResult.bytesProduced();
            }
            catch (SSLException e) {
                emitError(e, "unwrap failed");
                return bytesConsumed;
            }

            if (bytesProduced > 0) {
                unwrappedReceived.flip();
                byte[] result = new byte[unwrappedReceived.remaining()];
                unwrappedReceived.get(result);
                unwrappedReceived.compact();
                byteArray = new ByteArray(result);
            }

        }
        
        /* Now out of synchronized block */
        if (byteArray != null) {
            onDataForApplication.emit(byteArray);
        }
        return bytesConsumed;

    }

    /**
     * Use the SSLEngine to wrap everything that we've so far got
     * in "plainToSend", and then send all of that to the socket.  Caller
     * is responsible for checking the handshake status on return
     * 
     * @return the number of bytes that were sent out to the network
     * 
     */
    private int wrapAndSendData() {

        int bytesSentToSocket = 0;
        ByteArray byteArray = null;
        SSLEngineResult sslEngineResult = null;
        Status status = null;
        HandshakeStatus handshakeStatus = null;
        boolean handshakeFinished = false;
        
        synchronized(sendMutex) {
            /* Check if there's anything outstanding to be sent at the
             * top of the loop, so that we clear the "wrappedToSend"
             * buffer before asking the engine to encrypt anything
             * TODO: is this required? I don't think anything gets put in
             * wrappedToSend apart from in here?
             */
            wrappedToSend.flip();
            if (wrappedToSend.hasRemaining()) {
                byte[] b = new byte[(wrappedToSend.remaining())];
                wrappedToSend.get(b);
                byteArray = new ByteArray(b);
            }
            wrappedToSend.compact();
        } /* end synchronized */

        if (byteArray != null ) {
            int s =  byteArray.getSize();
            onDataForNetwork.emit(byteArray);
            bytesSentToSocket += s;
            byteArray = null;
        }

        /* There's nothing waiting to be sent. Now see what new data needs 
         * encrypting
         */
        synchronized(sendMutex) {
            plainToSend.flip();
	    /* It does no harm to call SSLEngine.wrap if there is nothing in
	     * "plainToSend" and this will be required in at least two
	     * cases:
	     * - during the initial handshake, when the application hasn't
	     *   yet sent anything, but wrap() must be called to generate
	     *   the TLS handshake data
	     * - during closure, when wrap() will generate the response to
	     *   the server's close notify message
	     */
	    try {
                boolean wrapDone = false;
                do {
                    sslEngineResult = sslEngine.wrap(plainToSend, wrappedToSend);
                    handshakeStatus = sslEngineResult.getHandshakeStatus();
                    status = sslEngineResult.getStatus();
                    
                    
                    if (status == Status.BUFFER_OVERFLOW) {
                        wrappedToSend = getLargerBuffer(
                                "wrappedToSend", wrappedToSend, netBufferSize);
                    }
                    else {
                        wrapDone = true;
                    }
                }
                while (!wrapDone);
            }
            
            catch (SSLException e) {
                /* This could result from the "enlargeBuffer" running out of space */
                emitError(e,"SSLEngine.wrap failed");
                return bytesSentToSocket;
            }
            plainToSend.compact();

            /* FINISHED can only come back for wrap() or unwrap(); so check to 
             * see if we just had it.
	     * The second checking clause is necessary for certain
	     * SSLEngine implementations (notably Apache Harmony
	     * used on Android) which never return FINISHED
             */
            if (handshakeStatus == HandshakeStatus.FINISHED ||
		(!handshakeCompleted &&
		 handshakeStatus == HandshakeStatus.NOT_HANDSHAKING)) {
                handshakeFinished = true;
            }

            switch (status) {
            case CLOSED :
                /* Engine closed - this is expected if a close notify has
		 * been sent by the server, and the SSLEngine has finished
		 * generating the response to that message.
		 */
		if (!closeNotifyReceived) {
		    emitError(null, "SSLEngine.wrap returned " + status);
		    return (bytesSentToSocket);
		}
		/* CLOSED was expected, so fall through to OK, in order
		 * to send the close response back to the server
		 */
            case OK:
                /* This is the status we expect here. It means data
                 * was successfully wrapped and that there's something
                 * to be sent.
                 */  
                wrappedToSend.flip();
                if (wrappedToSend.hasRemaining()) {
                    byte[] b = new byte[(wrappedToSend.remaining())];
                    wrappedToSend.get(b);
                    byteArray = new ByteArray(b);
                }
                wrappedToSend.compact();
                break;

            case BUFFER_UNDERFLOW:
                /* Can't happen for a wrap */
            case BUFFER_OVERFLOW:
                /* We already dealt with this, so don't expect to come here
                 */
                emitError(null, "SSLEngine.wrap returned " + status);
                return bytesSentToSocket;

            }
        } /* end synchronized */
        
        if (handshakeFinished) {
            handshakeCompleted = true;
            onConnected.emit();
        }
        
        if (byteArray != null) {
            int s = byteArray.getSize();

            onDataForNetwork.emit(byteArray);
            bytesSentToSocket += s;
            byteArray = null;
        }

        /* Note that there may still be stuff in "plainToSend" that hasn't
         * yet been consumed
         */
        return bytesSentToSocket;

    }

    
    /**
     * Process the current handshake status. 
     * @return <em>true</em> if this method needs to be called again, or
     * <em>false</em> if there's no more handshake status to be processed
     */
    private boolean processHandshakeStatus() {
        HandshakeStatus handshakeStatus;

        handshakeStatus = sslEngine.getHandshakeStatus();
        switch (handshakeStatus) {
        case NOT_HANDSHAKING:
            /* No handshaking going on - session is available, no more
             * handshake status to process
             */
            return false;
        case NEED_TASK:
            runDelegatedTasks(false); /* false==don't create separate threads */ 
            
            /* after tasks have run, need to come back here and check
             * handshake status again
             */
            return true;
            
        case NEED_WRAP:
            /* SSLEngine wants some data that it can wrap for sending to the
             * other side
             */
            wrapAndSendData();
            /* after sending data, need to check handshake status again */
            return true;

        case NEED_UNWRAP:
            
            /* SSLEngine wants data from other side that it can unwrap and
             * process
             */
            int consumed = unwrapPendingData();
            return (consumed > 0);
             
        case FINISHED:
            /* "This value is only generated by a call to wrap/unwrap when
             * that call finishes a handshake. It is never generated by
             * SSLEngine.getHandshakeStatus()"
             */
            
        default:
            /* There are no other values, but compiler requires this */
            throw new RuntimeException("unexpected handshake status " + handshakeStatus);
        }        
    }
    
    /**
     * Create a ByteBuffer that is a copy of an existing buffer, but with a
     * larger capacity. Note that no limits are imposed on the size of the
     * new buffer, and so repeated calls to this method will eventually
     * exhaust memory.
     * @param bufferName the name of the buffer, for logging purposes
     * @param bb the original ByteBuffer
     * @param growBy how many bytes to grow the buffer by
     * @return a ByteBuffer that will have been enlarged by <em>growBy</em>
     */
    private ByteBuffer getLargerBuffer(
            String bufferName, ByteBuffer bb, int growBy) {
        int newSize = bb.capacity() + growBy;
        logger_.fine("Buffer " + bufferName + 
                " growing from " + bb.capacity() + " to " + newSize);
        ByteBuffer temp = ByteBuffer.allocate(newSize);
        bb.flip();
        temp.put(bb);
        return temp;
    }
    
    /**
     * Create and start running delegated tasks for all pending delegated tasks
     * @param createThreads <em>true</em> to run tasks in separate threads,
     * <em>false</em> to run them all in series in the current thread
     */
    private void runDelegatedTasks(boolean createThreads)    
    {
        Runnable nextTask = sslEngine.getDelegatedTask();
        
        while (nextTask != null) {
            final Runnable task = nextTask;
            Thread delegatedTaskThread = new Thread() {
                public void run() {
                    task.run();
                }
            };
            
            if (createThreads) {
                delegatedTaskThread.setDaemon(true);
                delegatedTaskThread.start();
            }
            else {
                delegatedTaskThread.run();
            }
            nextTask = sslEngine.getDelegatedTask();
        } 
    }
    
    /**
     * This method must be called to inform the JSSEContext object of the 
     * certificate(s) which were presented by the peer during the handshake.
     * 
     * <p>For example, an X509TrustManager implementation may obtain
     * peer certificates inside <em>checkServerTrusted</em>, and call this
     * method with those certificates. 
     * 
     * @param certs chain of certificates presented by the server.  Will
     * subsequently be returned by any call to {@link #getPeerCertificate()}
     * 
     * @param certificateException any exception resulting from an attempt 
     * to verify the certificate. May be null. If non-null, then will be used 
     * to generate the value that is returned by any subsequent call to 
     * {@link #getPeerCertificateVerificationError()}
     */
    public void setPeerCertificateInfo(X509Certificate[] certs, 
            CertificateException certificateException) {
        if (certs == null || certs.length == 0) {
            return;
        }
                
        peerCertificate = new JavaCertificate(certs[0]);
        
        /* Swiften uses SSL_get_verify_result() for this, and the documentation
         * for that says it "while the verification of a certificate can fail
         * because of many reasons at the same time. Only the last verification
         * error that occurred..is available". 
         * So once one problem is found, don't bother looking for others.
         */

        if (certificateException != null) {
            if (certificateException instanceof CertificateNotYetValidException) {
                peerCertificateVerificationError = new CertificateVerificationError(Type.NotYetValid);
                return;
                
            }
            
            if (certificateException instanceof CertificateExpiredException) {
                peerCertificateVerificationError = new CertificateVerificationError(Type.Expired);
                return;
            }
        }
        
    }
    
    /**
     * Private method to handle PKCS12Certificate case for setClientCertificate
     */
    private boolean setClientCertificatePKCS12(PKCS12Certificate p12Cert) {
        if (!p12Cert.isPrivateKeyExportable()) {
            emitError(null,p12Cert + " does not have exportable private key");
            return false;
        }
        
        /* Get a reference that can be used in any error messages */
        File p12File = new File(p12Cert.getCertStoreName());
        
        /* Attempt to build a usable identity from the P12 file. This set of
         * operations can result in a variety of exceptions, all of which
         * mean that the operation is regarded as having failed.
         * If it works, then "myKeyManager_" will be initialised for use
         * by any subsequent call to getSSLContext() 
         */
        KeyStore keyStore = null;
        KeyManagerFactory kmf = null;
        
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            kmf = KeyManagerFactory.getInstance("SunX509");
            
            /* The PKCS12Certificate object has read the file contents already */
            ByteArray ba = p12Cert.getData();
            byte[] p12Bytes = ba.getData();
                        
            ByteArrayInputStream bis = new ByteArrayInputStream(p12Bytes);
            
            /* Both of the next two calls require that we supply the password */
            keyStore.load(bis, p12Cert.getPassword());
            kmf.init(keyStore, p12Cert.getPassword());
            
            KeyManager[] keyManagers = kmf.getKeyManagers();
            if (keyManagers == null || keyManagers.length == 0) {
                emitError(null, "Unable to get KeyManager for SunX509");
                return false;
            }
            
            /* Just take the first one (there probably will only be one) */
            myKeyManager_ = keyManagers[0];
                        
            return true;
            
        }
        catch (KeyStoreException e) {
            emitError(e, "Cannot get PKCS12 KeyStore");
        }
        catch (NoSuchAlgorithmException e) {
            emitError(e, "Unable to initialise KeyStore from " + p12File);
        }
        catch (CertificateException e) {
            emitError(e, "Unable to load certificates from " + p12File);
        }
        catch (IOException e) {
            if (e.getCause() != null && e.getCause() instanceof UnrecoverableKeyException) {
                emitError(e, "Password incorrect for " +p12File);
            }
            else {
                emitError(e, "Unable to read " + p12File);
            }
        }
        catch (UnrecoverableKeyException e) {
            emitError(e, "Unable to initialise KeyStore from " + p12File);            
        }
        
        /* Fall through here after any exception */
        return false;
        
    }

    /**
     * Structure used to keep track of a KeyStore/alias tuple
     */
    private static class KeyStoreAndAlias {
        public KeyStore keyStore;
        public String alias;
        KeyStoreAndAlias(KeyStore keyStore, String alias) {
            this.keyStore = keyStore;
            this.alias = alias;
        }
    }
    
    
    /**
     * See if a given X509Certificate can be found in a specific CAPI keystore. 
     * @param x509Cert the certificate to look for. Must not be null.
     * @param keyStoreName the name of the keystore to search. Must not be null.
     * @return a StoreAndAlias object containing references the keystore and
     * alias which match, or null if <em>x509Cert</em> was not found.
     */
    private KeyStoreAndAlias findCAPIKeyStoreForCertificate(
            X509Certificate x509Cert, 
            String keyStoreName) {
        
        KeyStore ks = null;
             
        /* Try to instantiate a CAPI keystore. This will fail on non-Windows
         * platforms
         */      
        try {
            ks = KeyStore.getInstance(keyStoreName, CAPIConstants.sunMSCAPIProvider);
        }
        catch (NoSuchProviderException e) {  
            /* Quite likely we're not on Windows */
            emitError(e, "Unable to instantiate " + CAPIConstants.sunMSCAPIProvider + " provider");
            return null;
        }
        catch (KeyStoreException e) {
            /* The keystore name is not right. Most likely the caller specified
             * an unrecognized keystore name when creating the CAPICertificate.
             */
            emitError(e, "Cannot load " + keyStoreName + " from " + CAPIConstants.sunMSCAPIProvider);
            return null;
        }

        /* All the exceptions that might be thrown here need to be caught but
         * indicate something unexpected has happened, so the catch clauses 
         * all emit errors
         */
        try {
            /* For a CAPI keystore, no parameters are required for loading */       
            ks.load(null,null);            
            String alias = ks.getCertificateAlias(x509Cert);
            
            return (alias == null ? null : new KeyStoreAndAlias(ks, alias));

        } catch (CertificateException e) {
            emitError(e, "Unexpected exception when loading CAPI keystore");            
            return null;             
        } catch (NoSuchAlgorithmException e) {
            emitError(e, "Unexpected exception when loading CAPI keystore");            
            return null;             
        } catch (IOException e) {
            /* This exception is meant to be for when you're loading a keystore
             * from a file, and so isn't expected for a CAPI, so emit an error 
             * error 
             */
            emitError(e, "Unexpected exception when loading CAPI keystore");
            return null;
        } catch (KeyStoreException e) {
            /* Thrown by KeyStore.getCertificateAlias when the keystore 
             * hasn't been initialized, so not expected here
             */
            emitError(e, "Unexpected exception when reading CAPI keystore");
            return null;            
        }
    }
    

    
    /**
     * Private method to handle CAPICertificate case for setClientCertificate
     * @param capiCert a CAPICertificate, not null.
     * @return <em>true</em> if the operation was successful, <em>false</em>
     * otherwise.
     */
    private boolean setClientCertificateCAPI(CAPICertificate capiCert) {
        KeyStoreAndAlias keyStoreAndAlias = null;

        X509Certificate x509Cert = capiCert.getX509Certificate();
        String keyStoreName = capiCert.getKeyStoreName();
        
        if (keyStoreName != null) {
            keyStoreAndAlias = findCAPIKeyStoreForCertificate(x509Cert, keyStoreName);
        }
        else {
            /* Try the list of predefined values, looking for the first match */            
            for (String keyStore:CAPIConstants.knownSunMSCAPIKeyStores) {
                keyStoreAndAlias = findCAPIKeyStoreForCertificate(x509Cert, keyStore);
                if (keyStoreAndAlias != null) {
                    break;
                }
            }
        }
        if (keyStoreAndAlias == null) {
            emitError(null,"Unable to load " + capiCert + " from CAPI");
            return false;
        }

        KeyManagerFactory kmf = null;

        try {    

            String defaultAlg = KeyManagerFactory.getDefaultAlgorithm();

            kmf = KeyManagerFactory.getInstance(defaultAlg);
            kmf.init(keyStoreAndAlias.keyStore,null);
            KeyManager[] kms = kmf.getKeyManagers();
            if (kms != null && kms.length > 0) {
                /* Successfully loaded the KeyManager. Look for the first 
                 * one which is suitable for our use (there's almost certainly
                 * only one in the list in any case)
                 */
                for (KeyManager km:kms) { 
                    if (km instanceof X509ExtendedKeyManager) {
                        CAPIKeyManager ckm = new CAPIKeyManager(
                                (X509ExtendedKeyManager)km);
                        
                        /* Make sure that the alias used for client certificate
                         * is the one that the caller asked for 
                         */
                        ckm.setEngineClientAlias(keyStoreAndAlias.alias);
                        myKeyManager_ = ckm;
                        return true;
                    }
                }
                emitError(null,"Unable to find suitable X509ExtendedKeyManager");
                return false;                
            }
            return false;

        } catch (NoSuchAlgorithmException e) {
            /* From KeyManagerFactory.getInstance() or KeyManagerFactory.init() */
            return false;
        } catch (UnrecoverableKeyException e) {
            /* From KeyManagerFactory.init() */
            return false;
        } catch (KeyStoreException e) {
            /* From KeyManagerFactory.init() */
            return false;
        } 
    }
    
    @Override
    public boolean setClientCertificate(CertificateWithKey cert) {
        if (cert == null || cert.isNull()) {
            emitError(null,cert + " has no useful contents");
            return false;
        }
        
        /* Use subclass-specific method depending on what subclass it is */
        if (cert instanceof PKCS12Certificate) {
            return setClientCertificatePKCS12((PKCS12Certificate)cert);
        }
        
        if (cert instanceof CAPICertificate) {
            return setClientCertificateCAPI((CAPICertificate)cert);
        }
        
        /* Not a type that is recognised 
         */
        emitError(null,"setClientCertificate cannot work with " 
                + cert.getClass() + " objects");

        return false;

    }

    @Override
    public void handleDataFromNetwork(ByteArray data) {
        if (hasError()) {
            /* We have previously seen, and reported, an error.  Emit again */
            onError.emit();        
            return;
        }

        /* Note that we need to deal with arbitrarily large ByteArrays here;
         * specifically it may be that the number of bytes from the network is
         * larger than the value of "netBufferMax" that was used to size the
         * encryptedReceived buffer 
         */
        byte[] b = data.getData();
        
        /* We need to deal with arbitrarily large ByteArrays here; specifically
         * it may be that the number of bytes from the network is
         * larger than the value of "netBufferMax" that was used to size the
         * encryptedReceived buffer 
         */
        int remaining = b.length;
        int chunkPos = 0;
        while (remaining > 0) {                
            synchronized(recvMutex) {
                int chunkSize = encryptedReceived.remaining();
                if (chunkSize == 0) {
                    encryptedReceived = getLargerBuffer(
                            "encryptedReceived", encryptedReceived, netBufferSize);
                    /* We know that this will now give us a non-zero value */
                    chunkSize = encryptedReceived.remaining();                    
                }
                if (remaining <= chunkSize) {
                    /* There's room in the buffer for all remaining bytes */
                    chunkSize = remaining;
                }
                try {
                    encryptedReceived.put(b, chunkPos, chunkSize);
                    remaining = (remaining - chunkSize);
                    chunkPos = (chunkPos + chunkSize);
                }
                catch (BufferOverflowException e) {
                    /* We never expect buffer overflow, because we are being
                     * careful not to write too much.  If this happens, 
                     * then include info in the error that may help
                     * diagnosis
                     */
                    emitError(e, "Unexpected when writing encryptedReceived; remaining=" + 
                            remaining + 
                            "; chunkPos=" + chunkPos +
                            "; chunkSize= " + chunkSize + 
                            "; encryptedReceived=" + encryptedReceived);
                    return;
                }
            }

            unwrapPendingData();

            /* Now keep checking SSLEngine until no more handshakes are required */
            do {
                /* */
            } while (processHandshakeStatus());
            
            if (closeNotifyReceived) {
                /* This is the only way to let the application know that close 
                 * notify was received.  This check is done after finishing with
                 * the handshake checks, so that the SSLEngine's response to 
                 * the close notify has been dealt with.
                 */
                emitError(null, "SSL Close notify received");
                return;
            }
            
            /* Loop round so long as there are still bytes from the network
             * to be processed
             */
        }
    }

    @Override
    public void handleDataFromApplication(ByteArray data) {
        if (hasError()) {
            /* We have previously seen, and reported, an error.  Emit again */
            onError.emit();        
            return;
        }
	if (closeNotifyReceived) {
	    /* After the server has closed the TLS connection, no more
	     * application data should be sent.
	     */
	    emitError(null,
	      "handleDataFromApplication called after SSLEngine closed");
	}
        byte[] b = data.getData();

        /* Need to cope in the case that the application sends a ByteArray
         * with more data than will fit in the "plainToSend" buffer
         */
        int remaining = b.length;
        int chunkPos = 0;
        while (remaining > 0) {
            synchronized(sendMutex) {
                int chunkSize = plainToSend.remaining();
                if (chunkSize == 0) {              
                    plainToSend = getLargerBuffer("plainToSend", plainToSend, appBufferSize);
                    /* We know that this will now give us a non-zero value */
                    chunkSize = plainToSend.remaining();
                }
                if (remaining <= chunkSize) {
                    /* There's room in the buffer for all remaining bytes */
                    chunkSize = remaining;
                }
                try {
                    /* Note that "plainToSend" may not be empty, because it's possible
                     * that calls to SSLEngine.wrap haven't yet consumed everything
                     * in there
                     */
                    
                    plainToSend.put(b, chunkPos, chunkSize);
                    remaining = (remaining - chunkSize);
                    chunkPos = (chunkPos + chunkSize);
                }
                catch (BufferOverflowException e) {
                    /* We never expect buffer overflow, because we are being
                     * careful not to write too much. If this happens, then
                     * include info in the error that may help diagnosis
                     */
                    emitError(e, "Unexpected when writing to plainToSend; remaining=" +
                            remaining +
                            "; chunkPos=" + chunkPos +
                            "; chunkSize=" + chunkSize +
                            "; plainToSend=" + plainToSend);
                    return;
                }
            }

            int sentBytes = wrapAndSendData();

            /* Now keep checking SSLEngine until no more handshakes are required */
            do {
                /* */
            } while (processHandshakeStatus());

            /* Loop round so long as there are still bytes from the application
             * to be processed
             */
        }
    }



    @Override
    public Certificate getPeerCertificate() {
        return peerCertificate;
    }

    @Override
    public CertificateVerificationError getPeerCertificateVerificationError() {
        return peerCertificateVerificationError;
    }

    @Override
    public ByteArray getFinishMessage() {
        /* TODO: Doesn't appear to be an obvious way to get this
         * information from SSLEngine et al.  For now, return null.
         */
        
        return null;
    }
    
    @Override
    public String toString() {
        String errors = null;
        if (hasError()) {
            errors = "; errors emitted:";
            for (JSSEContextError e : errorsEmitted) {
                errors += "\n  " + e;
            }
        }
        
        String result = 
            "JSSEContext(" + hashCode() + ") with SSLEngine = " + 
            sslEngine + 
            "; handshakeCompleted=" + handshakeCompleted;
        
        if (errors == null) {
            return result + " (no errors)";
        }
        return result + errors;
    }

    /**
     * Construct a new JSSEContext object. 
     * @param restrictedCipherSuites a list of cipher suites that are to be
     * enabled for this context. Null means no restriction
     */
    public JSSEContext(Set<String> restrictedCipherSuites) {
        if (restrictedCipherSuites != null) {
            this.restrictedCipherSuites = new HashSet<String>(restrictedCipherSuites);
        }
    }
    

    /**
     * Specific list of suites to allow - null (the default) means
     * no restriction.
     */
    private Set<String> restrictedCipherSuites = null;

    /**
     * Reference to the SSLEngine being used
     */
    private SSLEngine sslEngine;
    /**
     * Contains plaintext information supplied by the caller which is
     * waiting to be encrypted and sent out over the socket. 
     */
    private ByteBuffer plainToSend;
    
    /**
     * The initial size of the buffer used for application data.  This is
     * likely to be enough for plaintext buffers, but in cases where the size
     * is exceeded (for example, the result of decrypting a particularly huge 
     * message), the buffer will be increased by this amount.
     */
    private int appBufferSize;
        
    /** 
     * Initial size of buffer used for encrypted data to/from SSL.
     */
    private int netBufferSize;
    
    /**
     * Contains encrypted information produced by the SSLEngine which is
     * waiting to be sent over the socket
     */
    private ByteBuffer wrappedToSend;
    
    /**
     * Contains (presumably encrypted) information received from the socket
     * which is waiting to be unwrapped by the SSLEngine. 
    */
    private ByteBuffer encryptedReceived;
    
    /**
     * Contains data that the SSLEngine has unwrapped and is now waiting to
     * be read by the caller
     */
    private ByteBuffer unwrappedReceived;
    /**
     * Used to synchronize access to both plainToSend and wrappedToSend
     */
    private Object sendMutex = new Object();
    /**
     * Used to synchronize access to both encryptedReceived and unwrappedReceived
     */
    private Object recvMutex = new Object();
    
    /**
     * The server certificate as obtained from the TLS handshake
     */
    private JavaCertificate peerCertificate = null;
    
    /**
     * The CertificateVerificationError derived from the peerCertificate. This
     * may be null if no error was found.
     */
    private CertificateVerificationError peerCertificateVerificationError = null;
              
    private final Logger logger_ = Logger.getLogger(this.getClass().getName());
    
    private KeyManager myKeyManager_ = null;
    
    /**
     * Set up the SSLContext and JavaTrustManager that will be used for this
     * JSSEContext.
     * 
     * TODO: We probably want a way to allow callers to supply their own
     * values for SSLContext and TrustManager
     * 
     * @return an SSLContext, or null if one cannot be created. In this case,
     * an error will have been emitted.
     */
    private SSLContext getSSLContext()
    {
        JavaTrustManager[] tm = null;
        
        try {
            tm = new JavaTrustManager[] { new JavaTrustManager(this)};
        }
        catch (SSLException e) {
            emitError(e, "Couldn't create JavaTrustManager");
        }
        
        /*
         * This is the list of protocols, in preference order, that will be
         * used to obtain an SSLContext.
         * 
         * Note that "TLSv1.2" and "TLSv1.1" appear to be available for
         * JRE7 but not JRE6
         * 
         * Note that the actual protocol negotiated will depend on what 
         * the server can support: the one offered by the client is "best",
         * and server may not support that so will use a lesser value
         * 
         * The loop will pick the first protocol that returns an SSLContext.
         * 
         */
        final String protocols[] = { 
                /* These work for JRE 7 but may not be available for JRE 6*/
                "TLSv1.2", "TLSv1.1", 
                
                /* These work for JRE 6 */
                "TLSv1", "TLS", "SSLv3" };
        
        /* Accumulate a list of problems which will be discarded if things
         * go well, but including in the error if things fail
         */
        String problems = "";
        GeneralSecurityException lastException = null;
        
        SSLContext sslContext = null;
        for (String protocol:protocols) {
            try {            	
				sslContext = SSLContext.getInstance(protocol);
                
                /* If a KeyManager has been set up in setClientCertificate()
                 * then use it; otherwise the "default" implementation will be 
                 * used, which will be sufficient for starting TLS with no
                 * client certificate
                 */
                KeyManager[] keyManagers = null;
                if (myKeyManager_ != null) {
                    keyManagers = new KeyManager[] { myKeyManager_ };
                }
                try {
                    sslContext.init(
                            keyManagers,    /* KeyManager[] */
                            tm,      /* TrustManager[] */
                            null);   /* SecureRandom */
                    
                    return sslContext;
                }
                catch (KeyManagementException e) {
                    lastException = e;
                    problems += "Could not get SSLContext for " + protocol + " (" + e + ")\n";
                }
            }
            catch (NoSuchAlgorithmException e) {
                lastException = e;
                problems += "Could not get SSLContext for " + protocol + " (" + e + ")\n";
                /* Try the next one */
            }
        }
        
        /* Fell through without being able to initialise using any
         * of the protocols
         */
        emitError(lastException, problems);
        return null;
       
    }
}
