/*  Copyright (c) 2012, Isode Limited, London, England.
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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
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

import com.isode.stroke.base.ByteArray;
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
                
        sslEngine.setUseClientMode(true); /* I am a client */
        sslEngine.setEnableSessionCreation(true); /* can create new sessions */
        

        /* Will get "the current size of the largest application data that is
         * expected when using this session". 
         * 
         * If we get packets larger than this, we'll grow the buffers by this
         * amount.
         */
        appBufferSize = sslEngine.getSession().getApplicationBufferSize();
        
        /*
         * Don't grow application buffers bigger than this
         */
        appBufferMax = (appBufferSize * 10);
        
        /* "A SSLEngine using this session may generate SSL/TLS packets of
         * any size up to and including the value returned by this method"
         * 
         * Note though, that this doesn't mean we might not be asked to 
         * process data chunks that are larger than this: we cannot rely on this 
         * value being big enough to hold anything that comes in through
         * "handleDataFromNetwork()".
         */        
        netBufferSize = sslEngine.getSession().getPacketBufferSize();
        /*
         * Don't grow network buffers bigger than this
         */
        netBufferMax = (netBufferSize * 10);
        
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
         * So this is a hack to force the handshake to occur: on the assumption
         * that the first thing to be sent once TLS is running is 
         * the "<" from the start of a tag, we send a less-than sign now, 
         * which we'll remember must be removed that from the first message
         * we get told to send.
         */
        
        sslEngine.beginHandshake();

        ByteArray ba = new ByteArray("<".getBytes());
        hack = HackStatus.SENDING_FAKE_LT;
        handleDataFromApplication(ba);
                
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
                    if (handshakeStatus == HandshakeStatus.FINISHED) {
                        /* Special case will happen when the handshake completes following
                         * an unwrap.  The first time we tried wrapping some plain stuff,
                         * it triggers the handshake but won't itself have been dealt with.
                         * So now the handshake has finished, we have to try sending it
                         * again
                         */
                        handshakeCompleted = true;
                        wrapAndSendData();
                        onConnected.emit();
                        status = sslEngineResult.getStatus();
                    }

                    
                    switch (status) {
                    case BUFFER_OVERFLOW :
                        unwrappedReceived = enlargeBuffer("unwrappedReceived",unwrappedReceived,appBufferSize, appBufferMax);
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
                        /* Engine closed - don't expect this here */
                        emitError(null, "SSLEngine.unwrap returned " + status);
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
     */
    private void wrapAndSendData() {

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

        if (byteArray != null) {
            int s = byteArray.getSize();
            onDataForNetwork.emit(byteArray);
            bytesSentToSocket += s;
            byteArray = null;
        }

        /* There's nothing waiting to be sent. Now see what new data needs 
         * encrypting
         */
        synchronized(sendMutex) {
            plainToSend.flip();
            if (!plainToSend.hasRemaining()) {
                /* Nothing more to be encrypted */
                plainToSend.compact();
                return;
            }
            try {
                boolean wrapDone = false;
                do {
                    sslEngineResult = sslEngine.wrap(plainToSend, wrappedToSend);
                    handshakeStatus = sslEngineResult.getHandshakeStatus();
                    status = sslEngineResult.getStatus();
                    
                    
                    if (status == Status.BUFFER_OVERFLOW) {
                        wrappedToSend = enlargeBuffer(
                                "wrappedToSend", wrappedToSend, netBufferSize, netBufferMax);
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
                return;
            }
            plainToSend.compact();

            /* FINISHED can only come back for wrap() or unwrap(); so check to 
             * see if we just had it
             */
            if (handshakeStatus == HandshakeStatus.FINISHED) {
                handshakeFinished = true;
            }

            switch (status) {
            case OK:
                /* This is the only status we expect here. It means the
                 * data was successfully wrapped and that there's something
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
            case CLOSED :
                /* Engine closed - don't expect this here */
            case BUFFER_OVERFLOW:
                /* We already dealt with this, so don't expect to come here
                 */
                emitError(null, "SSLEngine.wrap returned " + status);
                return;

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
        return;

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
     * larger capacity.
     * @param bufferName the name of the buffer, for logging purposes
     * @param bb the original ByteBuffer
     * @param growBy how many bytes to grow the buffer by
     * @param maxSize the maximum size that the output buffer is allowed to be
     * @return a ByteBuffer that will have been enlarged by <em>growBy</em>
     * @throws BufferOverflowException if adding <em>growBy</em> would take
     * the buffer's size to greater than <em>maxSize</em>
     */
    private ByteBuffer enlargeBuffer(
            String bufferName, ByteBuffer bb, int growBy, int maxSize) 
    throws SSLException {
        int newSize = bb.capacity() + growBy;
        if (newSize <= maxSize) {
            logger_.fine("Buffer " + bufferName + 
                    " growing from " + bb.capacity() + " to " + newSize);
            ByteBuffer temp = ByteBuffer.allocate(newSize);
            bb.flip();
            temp.put(bb);
            return temp;
        }
        throw new SSLException("Buffer for " + bufferName + 
                " exceeded maximum size of " + maxSize);
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
    
    
    @Override
    public boolean setClientCertificate(CertificateWithKey cert) {
        if (cert == null || cert.isNull()) {
            emitError(null,cert + " has no useful contents");
            return false;
        }
        if (!(cert instanceof PKCS12Certificate)) {
            emitError(null,"setClientCertificate can only work with PKCS12 objects");
            return false;
        }
        PKCS12Certificate p12 = (PKCS12Certificate)cert;
        if (!p12.isPrivateKeyExportable()) {
            emitError(null,cert + " does not have exportable private key");
            return false;
        }
        
        /* Get a reference that can be used in any error messages */
        File p12File = new File(p12.getCertStoreName());
        
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
            ByteArray ba = p12.getData();
            byte[] p12Bytes = ba.getData();
                        
            ByteArrayInputStream bis = new ByteArrayInputStream(p12Bytes);
            
            /* Both of the next two calls require that we supply the password */
            keyStore.load(bis, p12.getPassword());
            kmf.init(keyStore, p12.getPassword());
            
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
                    try {
                        encryptedReceived = enlargeBuffer(
                                "encryptedReceived", encryptedReceived, netBufferSize, netBufferMax);
                        /* We know that this will now give us a non-zero value */
                        chunkSize = encryptedReceived.remaining();
                    }
                    catch (SSLException e) {
                        /* Enlarging buffer failed */
                        emitError(e, "encryptedReceived buffer reached maximum size");
                        return;                        
                    }
                    
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
                    try {
                        plainToSend = enlargeBuffer("plainToSend", plainToSend, appBufferSize, appBufferMax);
                        /* We know that this will now give us a non-zero value */
                        chunkSize = plainToSend.remaining();
                    }
                    catch (SSLException e) {
                        /* Enlarging buffer failed */
                        emitError(e, "plainToSend buffer reached maximum size");
                        return;
                    }
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
                    switch (hack) {
                    case SENDING_FAKE_LT :
                        plainToSend.put(b, chunkPos, chunkSize);
                        hack = HackStatus.DISCARD_FIRST_LT;
                        break;

                    case DISCARD_FIRST_LT:
                        if (b.length > 0) {
                            if (b[0] == (byte)'<') {
                                plainToSend.put(b,1,chunkSize - 1);
                                hack = HackStatus.HACK_DONE;
                            }
                            else {
                                emitError(null,
                                        "First character sent after TLS started was " + 
                                        b[0] + " and not '<'"); 
                                return;
                            }
                        }
                        break;
                    case HACK_DONE:
                        plainToSend.put(b, chunkPos, chunkSize);
                        break;
                    }
                    
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

            wrapAndSendData();

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
     */
    public JSSEContext() {
        /* */
    }

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
     * The maximum amount to grow any buffer for application data
     */
    private int appBufferMax;
    
    /** 
     * Initial size of buffer used for encrypted data to/from SSL.
     */
    private int netBufferSize;
    
    /**
     * The maximum amount to grow any buffer for network data
     */
    private int netBufferMax;
    
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
    
    /**
     * Used to remember what state we're in when doing the hack to overcome the
     * issue of SSLEngine not starting to handshake until it's got some data
     * to send
     */
    private static enum HackStatus { SENDING_FAKE_LT, DISCARD_FIRST_LT, HACK_DONE }
    private HackStatus hack = HackStatus.SENDING_FAKE_LT;

          
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
