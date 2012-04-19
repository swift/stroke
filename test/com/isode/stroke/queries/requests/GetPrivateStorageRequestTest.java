/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.queries.requests;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.PrivateStorage;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.DummyIQChannel;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot2;

public class GetPrivateStorageRequestTest {
    private IQRouter router;
    private DummyIQChannel channel;
    private Vector<ErrorPayload> errors = new Vector<ErrorPayload>();
    private Vector<Payload> responses = new Vector<Payload>();

    public class MyPayload extends Payload {
        public MyPayload()  {
            this.text = "";
        }
        public MyPayload(String text)  {
            this.text = text;
        }
        public String text;
    }

    @Before
    public void setUp() {
        channel = new DummyIQChannel();
        router = new IQRouter(channel);
    }

    @Test
    public void testSend() throws Exception {
        MyPayload mpl = new MyPayload();
        PrivateStorage privStType = new PrivateStorage(mpl);
        GetPrivateStorageRequest<MyPayload> request = GetPrivateStorageRequest.create(mpl,router);
        request.send();

        assertEquals(1, channel.iqs_.size());
        assertEquals(new JID(), channel.iqs_.get(0).getTo());
        assertEquals(IQ.Type.Get, channel.iqs_.get(0).getType());

        PrivateStorage storage = channel.iqs_.get(0).getPayload(privStType);
        assertTrue(storage != null);
        MyPayload payload = (MyPayload)storage.getPayload();
        assertTrue(payload != null);
    }

    @Test
    public void testHandleResponse() {
        MyPayload mpl = new MyPayload();
        GetPrivateStorageRequest<MyPayload> testling = GetPrivateStorageRequest.create(mpl,router);
        testling.onResponse.connect(new Slot2<MyPayload, ErrorPayload>() {
            @Override
            public void call(MyPayload p1, ErrorPayload p2) {
                handleResponse(p1,p2);              
            }            
        });
        testling.send();
        channel.onIQReceived.emit(createResponse("test-id", "foo"));

        assertEquals(1, responses.size());
        assertEquals("foo", ((MyPayload)responses.get(0)).text);
    }

    @Test
    public void testHandleResponse_Error() {
        MyPayload mpl = new MyPayload();
        GetPrivateStorageRequest<MyPayload> testling = GetPrivateStorageRequest.create(mpl,router);
        testling.onResponse.connect(new Slot2<MyPayload, ErrorPayload>() {
            @Override
            public void call(MyPayload p1, ErrorPayload ep) {
                handleResponse(p1, ep);                
            }            
        });
        testling.send();
        channel.onIQReceived.emit(createError("test-id"));

        assertEquals(0, responses.size());
        assertEquals(1, errors.size());
    }

    private void handleResponse(Payload p, ErrorPayload e) {
        if (e != null) {
            errors.add(e);
        } else {
            responses.add(p);
        }
    }

    private IQ createResponse(String id, String text) {
        IQ iq = new IQ(IQ.Type.Result);
        MyPayload mPl = new MyPayload(text);
        PrivateStorage storage = new PrivateStorage(mPl);
        storage.setPayload(mPl);
        iq.addPayload(storage);
        iq.setID(id);
        return iq;
    }

    private IQ createError(String id) {
        IQ iq = new IQ(IQ.Type.Error);
        iq.setID(id);
        return iq;
    }
}
