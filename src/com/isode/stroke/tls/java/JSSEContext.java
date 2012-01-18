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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Vector;

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
import com.isode.stroke.tls.PKCS12Certificate;
import com.isode.stroke.tls.TLSContext;


/**
 * Concrete implementation of a TLSContext which uses SSLEngine 
 * and maybe other stuff? ..tbs...
 * 
 */
public class JSSEContext extends TLSContext {

    private static class JSSEContextError {
        public Throwable throwable;
        public String message;
        /**
         * Create a new object
         * @param t throwable; may be null
         * @param m message; may be null
         */
        public JSSEContextError(Throwable t, String m) {
            throwable = t;
            message = m;
        }
        @Override
        public String toString() {
            return "JSSEContextError: " +
                    (message == null ? "No message" : message) + "; " +
                    (throwable == null ? "No exception" : throwable.getMessage());
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
     * @param t the Throwable which caused this error (may be null)
     * @param m a String describing what caused this error (may be null)
     */
    private void emitError(Throwable t, String m) {
        JSSEContextError jsseContextError = new JSSEContextError(t,m);
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
       
        // May throw NoSuchAlgorithmException
        SSLContext sslContext = null;

        sslContext = getSSLContext();
        
        if (sslContext == null) {
            throw new SSLException("Could not create SSLContext");
        }
        
        sslEngine = null;
        try {
            sslEngine = sslContext.createSSLEngine();
        }
        catch (UnsupportedOperationException e) {
            // "the underlying provider does not implement the operation"
            throw new SSLException(e);
        }
        catch (IllegalStateException e) {
            // "the SSLContextImpl requires initialization and init() has not been called"
            throw new SSLException(e);
        }
        
        sslEngine.setUseClientMode(true); // I am a client
        sslEngine.setEnableSessionCreation(true); // can create new sessions
        

        int appBufferMax = sslEngine.getSession().getApplicationBufferSize();
        int netBufferMax = sslEngine.getSession().getPacketBufferSize();
        
        // All buffers are normally in "write" mode. Access to all of them
        // must be synchronized
        plainToSend = ByteBuffer.allocate(appBufferMax + 50);
        wrappedToSend = ByteBuffer.allocate(netBufferMax);
        encryptedReceived = ByteBuffer.allocate(netBufferMax);
        unwrappedReceived = ByteBuffer.allocate(appBufferMax + 50); 
        
        // Note that calling beginHandshake might not actually do anything; 
        // the SSLEngine may not actually send the handshake until it's had 
        // some data from the application.  And the higher level won't send 
        // any data until it thinks the handshake is completed.  
        //
        // So this is a hack to force the handshake to occur: on the assumption
        // that the first thing to be sent once TLS is running is 
        // the "<" from the start of a tag, we send a less-than sign now, 
        // which we'll remember must be removed that from the first message
        // we get told to send.
        
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
        HandshakeStatus handshakeStatus = null;
        ByteArray byteArray = null;

        synchronized(recvMutex) {
            try {
                encryptedReceived.flip();
                sslEngineResult = sslEngine.unwrap(encryptedReceived, unwrappedReceived);
                encryptedReceived.compact();

                // A call to unwrap can generate a status of FINISHED, which
                // you won't get from SSLEngine.getHandshakeStatus.  Such
                // a status is an indication that we need to re-check whether
                // anything's pending to be written
                handshakeStatus = sslEngineResult.getHandshakeStatus();


                bytesConsumed += sslEngineResult.bytesConsumed();
                bytesProduced = sslEngineResult.bytesProduced();
            }
            catch (SSLException e) {
                throw new RuntimeException("unwrap produced: " + e);
            }


            status = sslEngineResult.getStatus();
            boolean finished = false;
            while (!finished) {
                switch (status) {
                case BUFFER_UNDERFLOW:
                    // There's not enough data yet for engine to be able to decode
                    // a full message. Not a problem; assume that more will come
                    // in to the socket eventually
                    finished = true;
                    break;
                case OK:
                    // Unwrap was OK
                    finished = true;
                    break;
                case BUFFER_OVERFLOW:
                    // Not enough room in "unwrappedReceived" to write the data
                    // TODO: need to fix this
                case CLOSED:
                    // Engine closed - don't expect this here
                    emitError(null, "SSLEngine.unwrap returned " + status);
                    return bytesConsumed;
                }
            }

            if (handshakeStatus == HandshakeStatus.FINISHED) {
                // Special case will happen when the handshake completes following
                // an unwrap.  The first time we tried wrapping some plain stuff,
                // it triggers the handshake but won't itself have been dealt with.
                // So now the handshake has finished, we have to try sending it
                // again
                handshakeCompleted = true;
                wrapAndSendData();
                onConnected.emit();
            }

            if (bytesProduced > 0) {
                unwrappedReceived.flip();
                byte[] result = new byte[0];
                result = new byte[unwrappedReceived.remaining()];
                unwrappedReceived.get(result);
                unwrappedReceived.compact();
                byteArray = new ByteArray(result);
            }

        }
        
        // Now out of synchronized block
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
        boolean handshakeFinished = false;
       
        synchronized(sendMutex) {
            // Check if there's anything outstanding to be sent at the
            // top of the loop, so that we clear the "wrappedToSend"
            // buffer before asking the engine to encrypt anything
            // TODO: is this required? I don't think anything gets put in
            // wrappedToSend apart from in here?
            wrappedToSend.flip();
            if (wrappedToSend.hasRemaining()) {
                byte[] b = new byte[(wrappedToSend.remaining())];
                wrappedToSend.get(b);
                byteArray = new ByteArray(b);
            }
            wrappedToSend.compact();
        } // end synchronized

        if (byteArray != null) {
            int s = byteArray.getSize();
            
            onDataForNetwork.emit(byteArray);
            bytesSentToSocket += s;
            byteArray = null;
        }

        // There's nothing waiting to be sent. Now see what new data needs 
        // encrypting
        synchronized(sendMutex) {
            plainToSend.flip();
            if (!plainToSend.hasRemaining()) {
                // Nothing more to be encrypted
                plainToSend.compact();
                return;
            }
            try {
                sslEngineResult = sslEngine.wrap(plainToSend, wrappedToSend);
            }
            catch (SSLException e) {
                // TODO: Is there anything more that can be done here?
                // TODO: this is called inside the mutex, does this matter?
                emitError(e,"SSLEngine.wrap failed");
                return;
            }
            plainToSend.compact();

            status = sslEngineResult.getStatus();

            // FINISHED can only come back for wrap() or unwrap(); so check to
            // see if we just had it
            if (sslEngineResult.getHandshakeStatus() == HandshakeStatus.FINISHED) {
                handshakeFinished = true;
            }

            switch (status) {
            case OK:
                // This is the only status we expect here. It means the
                // data was successfully wrapped and that there's something
                // to be sent.  
                wrappedToSend.flip();
                if (wrappedToSend.hasRemaining()) {
                    byte[] b = new byte[(wrappedToSend.remaining())];
                    wrappedToSend.get(b);
                    byteArray = new ByteArray(b);
                }
                wrappedToSend.compact();
                break;

            case BUFFER_UNDERFLOW:
                // Can't happen for a wrap
            case CLOSED :
                // ???
            case BUFFER_OVERFLOW:
                // The "wrappedToSend" buffer, which we had previously made
                // sure was empty, isn't big enough. 
                // TODO: I don't think this can happen though
                // TODO: Note that we're in sychronized block here
                emitError(null, "SSLEngine.wrap returned " + status);
                return;

            }
        } // end synchronized
        
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

        // Note that there may still be stuff in "plainToSend" that hasn't
        // yet been consumed
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
            // No handshaking going on - session is available, no more
            // handshake status to process
            return false;
        case NEED_TASK:
            runDelegatedTasks(false); // false==don't create separate threads 
            
            // after tasks have run, need to come back here and check
            // handshake status again
            return true;
            
        case NEED_WRAP:
            // SSLEngine wants some data that it can wrap for sending to the
            // other side
            wrapAndSendData();
            // after sending data, need to check handshake status again
            return true;
        case NEED_UNWRAP:
            
            // SSLEngine wants data from other side that it can unwrap and
            // process
            int consumed = unwrapPendingData();
            return (consumed > 0);
             
        case FINISHED:
            // "This value is only generated by a call to wrap/unwrap when
            // that call finishes a handshake. It is never generated by
            // SSLEngine.getHandshakeStatus()"
            
        default:
            // There are no other values, but compiler requires this
            throw new RuntimeException("unexpected handshake status " + handshakeStatus);
        }        
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
        
        // Swiften uses SSL_get_verify_result() for this, and the documentation
        // for that says it "while the verification of a certificate can fail
        // because of many reasons at the same time. Only the last verification
        // error that occurred..is available". 
        // So once one problem is found, don't bother looking for others.

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
    public boolean setClientCertificate(PKCS12Certificate cert) {
        // TODO: NYI.
        // It's possible this is going to change as a result of Alexey's work
        // so will leave for now
        return false;
    }

    @Override
    public void handleDataFromNetwork(ByteArray data) {
        if (hasError()) {
            // We have previously seen, and reported, an error.  Emit again
            onError.emit();        
            return;
        }
        byte[] b = data.getData();
        
        synchronized(recvMutex) {
            try {
                // TODO: could "encryptedReceived" already have stuff in it?
                encryptedReceived.put(b);
            }
            catch (BufferOverflowException e) {
                emitError(e, "Unable to add data to encryptedReceived");
                return;
            }
        }
        
        unwrapPendingData();
        
        // Now keep checking SSLEngine until no more handshakes are required
        do {
            //
        } while (processHandshakeStatus());
               
    }

    @Override
    public void handleDataFromApplication(ByteArray data) {
        if (hasError()) {
            // We have previously seen, and reported, an error.  Emit again
            onError.emit();        
            return;
        }
        byte[] b = data.getData();
               
        synchronized(sendMutex) {
            try {
                // Note that "plainToSend" may not be empty, because it's possible
                // that calls to SSLEngine.wrap haven't yet consumed everything
                // in there
                switch (hack) {
                case SENDING_FAKE_LT :
                    plainToSend.put(b);
                    hack = HackStatus.DISCARD_FIRST_LT;
                    break;
                    
                case DISCARD_FIRST_LT:
                    if (b.length > 0) {
                        if (b[0] == (byte)'<') {
                            plainToSend.put(b,1,b.length - 1);
                            hack = HackStatus.HACK_DONE;
                        }
                        else {
                            emitError(null,
                                    "First character sent after TLS started was " + 
                                    b[0] + " and not '<'");                            
                        }
                    }
                    break;
                case HACK_DONE:
                    plainToSend.put(b);
                    break;
                }
            }
            catch (BufferOverflowException e) {
                // TODO: anything else here?
                emitError(e, "plainToSend.put failed");
                return;
            }
        }

        wrapAndSendData();
        
        // Now keep checking SSLEngine until no more handshakes are required
        do {
            //
        } while (processHandshakeStatus());

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
        // TODO: Doesn't appear to be an obvious way to get this
        // information from SSLEngine et al.  For now, return null.
        
        return null;
    }
    
    @Override
    public String toString() {
        String errors = null;
        if (hasError()) {
            errors = "; errors emitted:";
            for (JSSEContextError e:errorsEmitted) {
                errors += "\n  " + e;
            }
        }
        
        String result = 
            "JSSEContext with SSLEngine = " + 
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
        //
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
    private static HackStatus hack = HackStatus.SENDING_FAKE_LT;

          
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
        try {
            JavaTrustManager [] tm = new JavaTrustManager[] { new JavaTrustManager(this)};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    null,   // KeyManager[]
                    tm,     // TrustManager[]
                    null);  // SecureRandom
            return sslContext;
        }
        catch (SSLException e) {
            emitError(e, "Couldn't create JavaTrustManager");
        }
        catch (NoSuchAlgorithmException e) {
            emitError(e, "Couldn't create SSLContext");
        }
        catch (KeyManagementException e) {
            emitError(e, "Couldn't initialise SSLContext");
        }
        return null;
        
    }
}
