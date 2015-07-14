/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.muc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.elements.MUCOwnerPayload;
import com.isode.stroke.elements.MUCUserPayload;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.presence.StanzaChannelPresenceSender;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.signals.Slot2;

/**
 * Unit tests for MUC
 *
 */
public class MUCTest {
    private DummyStanzaChannel channel;
    private IQRouter router;
    private MUCRegistry mucRegistry;
    private StanzaChannelPresenceSender stanzaChannelPresenceSender;
    private DirectedPresenceSender presenceSender;
    private static class JoinResult {
        String nick = "";
        ErrorPayload error;
    };
    private Vector<JoinResult> joinResults = new Vector<JoinResult>();
    private int nickChanges;

    @Before
    public void setUp() {
        channel = new DummyStanzaChannel();
        router = new IQRouter(channel);
        mucRegistry = new MUCRegistry();
        stanzaChannelPresenceSender = new StanzaChannelPresenceSender(channel);
        presenceSender = new DirectedPresenceSender(stanzaChannelPresenceSender);
        nickChanges = 0;
    }

    @Test
    public void testJoin() {
        MUC testling = createMUC(new JID("foo@bar.com"));
        testling.joinAs("Alice");

        assertTrue(mucRegistry.isMUC(new JID("foo@bar.com")));
        Presence p = channel.getStanzaAtIndex(new Presence(),0);
        assertTrue(p != null);
        assertEquals(new JID("foo@bar.com/Alice"), p.getTo());
    }

    @Test
    public void testJoin_ChangePresenceDuringJoinDoesNotSendPresenceBeforeJoinSuccess() {
        MUC testling = createMUC(new JID("foo@bar.com"));
        testling.joinAs("Alice");

        presenceSender.sendPresence(new Presence("Test"));
        assertEquals(2, channel.sentStanzas.size());
    }

    @Test
    public void testJoin_ChangePresenceDuringJoinResendsPresenceAfterJoinSuccess() {
        MUC testling = createMUC(new JID("foo@bar.com"));
        testling.joinAs("Alice");

        presenceSender.sendPresence(new Presence("Test"));
        receivePresence(new JID("foo@bar.com/Rabbit"), "Here");

        assertEquals(3, channel.sentStanzas.size());
        Presence p = channel.getStanzaAtIndex(new Presence(),2);
        assertTrue(p != null);
        assertEquals(new JID("foo@bar.com/Alice"), p.getTo());
        assertEquals("Test", p.getStatus());
    }

    @Test
    public void testJoin_NoPresenceChangeDuringJoinDoesNotResendAfterJoinSuccess() {
        MUC testling = createMUC(new JID("foo@bar.com"));
        testling.joinAs("Alice");

        receivePresence(new JID("foo@bar.com/Rabbit"), "Here");

        assertEquals(1, channel.sentStanzas.size());
        Presence p = channel.getStanzaAtIndex(new Presence(), 0);
        assertTrue(p != null);
        assertEquals(new JID("foo@bar.com/Alice"), p.getTo());
        assertEquals("", p.getStatus());
    }

    @Test
    public void testCreateInstant() {
        MUC testling = createMUC(new JID("rabbithole@wonderland.lit"));
        testling.joinAs("Alice");
        Presence serverRespondsLocked = new Presence();
        serverRespondsLocked.setFrom(new JID("rabbithole@wonderland.lit/Alice"));
        MUCUserPayload mucPayload = new MUCUserPayload();
        MUCItem myItem = new MUCItem();
        myItem.affiliation = MUCOccupant.Affiliation.Owner;
        myItem.role = MUCOccupant.Role.Moderator;
        mucPayload.addItem(myItem);
        mucPayload.addStatusCode(new MUCUserPayload.StatusCode(110));
        mucPayload.addStatusCode(new MUCUserPayload.StatusCode(201));
        serverRespondsLocked.addPayload(mucPayload);
        channel.onPresenceReceived.emit(serverRespondsLocked);
        assertEquals(2, channel.sentStanzas.size());
        IQ iq = channel.getStanzaAtIndex(new IQ(),1);
        assertTrue(iq != null);
        MUCOwnerPayload ownerPl = new MUCOwnerPayload();
        assertNotNull(iq.getPayload(ownerPl));
        assertNotNull(iq.getPayload(ownerPl).getForm());
        assertEquals(Form.Type.SUBMIT_TYPE, iq.getPayload(ownerPl).getForm().getType());
    }
    
    @Test
    public void testReplicateBug() {
        Presence initialPresence = new Presence();
        initialPresence.setStatus("");
        
        VCard vcard = new VCard();
        vcard.setPhoto(new ByteArray("15c30080ae98ec48be94bf0e191d43edd06e500a"));
        initialPresence.addPayload(vcard);
        CapsInfo caps = new CapsInfo();
        caps.setNode("http://swift.im");
        caps.setVersion("p2UP0DrcVgKM6jJqYN/B92DKK0o=");
        initialPresence.addPayload(caps);
        
        channel.sendPresence(initialPresence);

        MUC testling = createMUC(new JID("test@rooms.swift.im"));
        testling.joinAs("Test");
        Presence serverRespondsLocked = new Presence();
        serverRespondsLocked.setFrom(new JID("test@rooms.swift.im/Test"));
        serverRespondsLocked.setTo(new JID("test@swift.im/6913d576d55f0b67"));        
        serverRespondsLocked.addPayload(vcard);
        serverRespondsLocked.addPayload(caps);
        serverRespondsLocked.setStatus("");
        MUCUserPayload mucPayload = new MUCUserPayload();
        MUCItem myItem = new MUCItem();
        myItem.affiliation = MUCOccupant.Affiliation.Owner;
        myItem.role = MUCOccupant.Role.Moderator;
        mucPayload.addItem(myItem);
        mucPayload.addStatusCode(new MUCUserPayload.StatusCode(201));
        serverRespondsLocked.addPayload(mucPayload);
        channel.onPresenceReceived.emit(serverRespondsLocked);
        assertEquals(3, channel.sentStanzas.size());
        IQ iq = channel.getStanzaAtIndex(new IQ(),2);
        assertTrue(iq != null);
        assertTrue(iq.getPayload(new MUCOwnerPayload()) != null);
        assertTrue(iq.getPayload(new MUCOwnerPayload()).getForm() != null);
        assertEquals(Form.Type.SUBMIT_TYPE, iq.getPayload(new MUCOwnerPayload()).getForm().getType());
    }

    @Test
    public void testNicknameChange() {
        MUC testling = createMUC(new JID("foo@bar.com"));
        // Join as Rabbit
        testling.joinAs("Rabbit");

        // Rabbit joins
        Presence rabbitJoins = new Presence();
        rabbitJoins.setTo(new JID("test@swift.im/6913d576d55f0b67"));
        rabbitJoins.setFrom(new JID(testling.getJID().toString() + "/Rabbit"));
        channel.onPresenceReceived.emit(rabbitJoins);
        assertEquals(true, testling.hasOccupant("Rabbit"));

        // Alice joins
        Presence aliceJoins = new Presence();
        aliceJoins.setTo(new JID("test@swift.im/6913d576d55f0b67"));
        aliceJoins.setFrom(new JID(testling.getJID().toString() + "/Alice"));
        channel.onPresenceReceived.emit(aliceJoins);
        assertEquals(true, testling.hasOccupant("Alice"));

        // Change nick to Dodo
        testling.changeNickname("Dodo");
        Presence stanza = channel.getStanzaAtIndex(new Presence(), 1);
        assertNotNull(stanza);
        assertEquals("Dodo", stanza.getTo().getResource());

        // Alice changes nick to Alice2
        stanza = new Presence();
        stanza.setFrom(new JID("foo@bar.com/Alice"));
        stanza.setTo(router.getJID());
        stanza.setType(Presence.Type.Unavailable);
        MUCUserPayload mucPayload = new MUCUserPayload();
        MUCItem myItem = new MUCItem();
        myItem.affiliation = MUCOccupant.Affiliation.Member;
        myItem.nick = "Alice2";
        myItem.role = MUCOccupant.Role.Participant;
        mucPayload.addItem(myItem);
        mucPayload.addStatusCode(new MUCUserPayload.StatusCode(303));
        stanza.addPayload(mucPayload);
        channel.onPresenceReceived.emit(stanza);
        assertEquals(1, nickChanges);
        assertEquals(false, testling.hasOccupant("Alice"));
        assertEquals(true, testling.hasOccupant("Alice2"));

        // We (Rabbit) change nick to Robot
        stanza = new Presence();
        stanza.setFrom(new JID("foo@bar.com/Rabbit"));
        stanza.setTo(router.getJID());
        stanza.setType(Presence.Type.Unavailable);
        mucPayload = new MUCUserPayload();
        myItem.affiliation = MUCOccupant.Affiliation.Member;
        myItem.nick = "Robot";
        myItem.role = MUCOccupant.Role.Participant;
        mucPayload.addItem(myItem);
        mucPayload.addStatusCode(new MUCUserPayload.StatusCode(303));
        stanza.addPayload(mucPayload);
        channel.onPresenceReceived.emit(stanza);
        assertEquals(2, nickChanges);
        assertEquals(false, testling.hasOccupant("Rabbit"));
        assertEquals(true, testling.hasOccupant("Robot"));
    }

    /*void testJoin_Success() {
        MUC::ref testling = createMUC(JID("foo@bar.com"));
        testling.onJoinFinished.connect(boost::bind(&MUCTest::handleJoinFinished, this, _1, _2));
        testling.joinAs("Alice");
        receivePresence(JID("foo@bar.com/Rabbit"), "Here");

        CPPUNIT_ASSERT_EQUAL(1, static_cast<int>(joinResults.size()));
        CPPUNIT_ASSERT_EQUAL(std::string("Alice"), joinResults[0].nick);
        CPPUNIT_ASSERT(joinResults[0].error);
    }

    void testJoin_Fail() {
        //CPPUNIT_ASSERT(!mucRegistry.isMUC(JID("foo@bar.com")));
    }*/

    private MUC createMUC(JID jid) {
        MUC muc = new MUCImpl(channel, router, presenceSender, jid, mucRegistry);
        muc.onOccupantNicknameChanged.connect(new Slot2<String, String>() {
            @Override
            public void call(String s1, String s2) {
                handleOccupantNicknameChanged(s1, s2);
            }
        });
        return muc;        
    }

    private void handleJoinFinished(String nick, ErrorPayload error) {
        JoinResult r = new JoinResult();
        r.nick = nick;
        r.error = error;
        joinResults.add(r);
    }
    
    private void receivePresence(JID jid, String status) {        
        Presence p = new Presence(status);
        p.setFrom(jid);
        //MUCUserPayload mucUserPayload = new MUCUserPayload();
        //mucUserPayload.addItem(item);
        //p.addPayload(mucUserPayload);        
        channel.onPresenceReceived.emit(p);
    }

    private void handleOccupantNicknameChanged(final String s1, final String s2) {
        nickChanges++;
    }
}
