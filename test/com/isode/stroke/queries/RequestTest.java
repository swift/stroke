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

package com.isode.stroke.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.DummyIQChannel;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Slot2;
import java.util.Vector;

public class RequestTest {

	private IQRouter router_;
	private DummyIQChannel channel_;
	private Payload payload_;
	private Payload responsePayload_;
	private int responsesReceived_;
	private Vector<ErrorPayload> receivedErrors = new Vector<ErrorPayload>();

	public class MyPayload extends Payload {

		public MyPayload() {
			this("");
		}

		public MyPayload(final String s) {
			this.text_ = s;
		}

		public String text_ = "";
	}

	public class MyOtherPayload extends Payload {

	}

	public class MyRequest extends Request {

		public MyRequest(IQ.Type type, final JID receiver, Payload payload, IQRouter router) {
			super(type, receiver, payload, router);
		}

		public void handleResponse(Payload payload, ErrorPayload error) {
			onResponse.emit(payload, error);
		}

		public final Signal2<Payload, ErrorPayload> onResponse = new Signal2<Payload, ErrorPayload>();
	}

	private	void handleResponse(Payload p, ErrorPayload e) {
		if (e != null) {
			receivedErrors.add(e);
		}
		else {
			MyPayload payload = (MyPayload)p;
			assertNotNull(payload);
			assertEquals("bar", payload.text_);
			++responsesReceived_;
		}
	}

	private void handleDifferentResponse(Payload p, ErrorPayload e) {
		assertNull(e);
		assertNull(p);
		++responsesReceived_;
	}

 	private void handleRawXMLResponse(Payload p, ErrorPayload e) {
		assertNull(e);
		assertNotNull(p);
		assertNotNull((MyOtherPayload)p);
		++responsesReceived_;
	}

	private IQ createResponse(final JID from, final String id) {
		IQ iq = new IQ(IQ.Type.Result);
		iq.setFrom(from);
		iq.addPayload(responsePayload_);
		iq.setID(id);
		return iq;
	}

	private IQ createError(final JID from, final String id) {
		IQ iq = new IQ(IQ.Type.Error);
		iq.setFrom(from);
		iq.setID(id);
		return iq;
	}

	@Before
	public void setUp() {
		channel_ = new DummyIQChannel();
		router_ = new IQRouter(channel_);
		payload_ = (Payload)(new MyPayload("foo"));
		responsePayload_ = (Payload)(new MyPayload("bar"));
		responsesReceived_ = 0;
	}

	@Test
	public void testSendSet() {
		MyRequest testling = new MyRequest(IQ.Type.Set, new JID("foo@bar.com/baz"), payload_, router_);
		testling.send();

		assertEquals(1, (channel_.iqs_.size()));
		assertEquals(new JID("foo@bar.com/baz"), channel_.iqs_.get(0).getTo());
		assertEquals(IQ.Type.Set, channel_.iqs_.get(0).getType());
		assertEquals(("test-id"), channel_.iqs_.get(0).getID());
	}

	@Test
	public void testSendGet() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.send();

		assertEquals(1, (channel_.iqs_.size()));
		assertEquals(IQ.Type.Get, channel_.iqs_.get(0).getType());
	}

	@Test
	public void testHandleIQ() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();
		
		channel_.onIQReceived.emit(createResponse(new JID("foo@bar.com/baz"),"test-id"));

		assertEquals(1, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	// FIXME: Doesn't test that it didn't handle the payload
	@Test
	public void testHandleIQ_InvalidID() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("foo@bar.com/baz"),"different-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_Error() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		IQ error = createError(new JID("foo@bar.com/baz"),"test-id");
		Payload errorPayload = new ErrorPayload(ErrorPayload.Condition.InternalServerError);
		error.addPayload(errorPayload);
		channel_.onIQReceived.emit(error);

		assertEquals(0, responsesReceived_);
		assertEquals(1, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
		assertEquals(ErrorPayload.Condition.InternalServerError, receivedErrors.get(0).getCondition());
	}

	@Test
	public void testHandleIQ_ErrorWithoutPayload() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createError(new JID("foo@bar.com/baz"),"test-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(1, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
		assertEquals(ErrorPayload.Condition.UndefinedCondition, receivedErrors.get(0).getCondition());
	}

	@Test
	public void testHandleIQ_BeforeSend() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		channel_.onIQReceived.emit(createResponse(new JID("foo@bar.com/baz"),"test-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(0, (channel_.iqs_.size()));
	}
	
	@Test
	public void testHandleIQ_DifferentPayload() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleDifferentResponse(p, e);
			}
		});
		testling.send();

		responsePayload_ = new MyOtherPayload();
		channel_.onIQReceived.emit(createResponse(new JID("foo@bar.com/baz"),"test-id"));

		assertEquals(1, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_RawXMLPayload() {
		payload_ = new RawXMLPayload("<bla/>");
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleRawXMLResponse(p, e);
			}
		});
		testling.send();

		responsePayload_ = new MyOtherPayload();
		channel_.onIQReceived.emit(createResponse(new JID("foo@bar.com/baz"),"test-id"));

		assertEquals(1, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_GetWithSameID() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		IQ response = createResponse(new JID("foo@bar.com/baz"),"test-id");
		response.setType(IQ.Type.Get);
		channel_.onIQReceived.emit(response);

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(2, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_SetWithSameID() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		IQ response = createResponse(new JID("foo@bar.com/baz"), "test-id");
		response.setType(IQ.Type.Set);
		channel_.onIQReceived.emit(response);

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(2, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_IncorrectSender() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID("foo@bar.com/baz"), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("anotherfoo@bar.com/baz"), "test-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_IncorrectSenderForServerQuery() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID(), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("foo@bar.com/baz"), "test-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_IncorrectOtherResourceSenderForServerQuery() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID(), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("alice@wonderland.lit/RabbitHole"), "test-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_ServerRespondsWithDomain() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID(), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("wonderland.lit"),"test-id"));

		assertEquals(0, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_ServerRespondsWithBareJID() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID(), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("alice@wonderland.lit"),"test-id"));

		assertEquals(1, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	// This tests a bug in ejabberd servers (2.0.5)
	@Test	
	public void testHandleIQ_ServerRespondsWithFullJID() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID(), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID("alice@wonderland.lit/TeaParty"),"test-id"));

		assertEquals(1, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}

	@Test
	public void testHandleIQ_ServerRespondsWithoutFrom() {
		MyRequest testling = new MyRequest(IQ.Type.Get, new JID(), payload_, router_);
		router_.setJID(new JID("alice@wonderland.lit/TeaParty"));
		testling.onResponse.connect(new Slot2<Payload, ErrorPayload>() {
			@Override
			public void call(Payload p, ErrorPayload e) {
				handleResponse(p, e);
			}
		});
		testling.send();

		channel_.onIQReceived.emit(createResponse(new JID(),"test-id"));

		assertEquals(1, responsesReceived_);
		assertEquals(0, (receivedErrors.size()));
		assertEquals(1, (channel_.iqs_.size()));
	}
}
