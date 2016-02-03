/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.avatars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.vcards.VCardMemoryStorage;

/**
 * Tests for {@link CombinedAvatarProvider}
 */
public class CombinedAvatarProviderTest {

    private final DummyAvatarProvider avatarProvider1 = new DummyAvatarProvider();
    private final DummyAvatarProvider avatarProvider2 = new DummyAvatarProvider();
    private final JID user1 = new JID("user1@bar.com/bla");
    private final JID user2 = new JID("user2@foo.com/baz");
    private final String avatarHash1 = "ABCDEFG";
    private final String avatarHash2 = "XYZU";
    private final String avatarHash3 = "IDGH";
    private final List<JID> changes = new ArrayList<JID>();

    @Test
    public void testGetAvatarWithNoAvatarProviderReturnsEmpty() {
        CombinedAvatarProvider testling = createProvider();
        String hash = testling.getAvatarHash(user1);
        assertNull(hash);
    }
    
    @Test
    public void testGetAvatarWithSingleAvatarProvider() {
        CombinedAvatarProvider testling = createProvider();
        avatarProvider1.avatars.put(user1, avatarHash1);
        testling.addProvider(avatarProvider1);
        String hash = testling.getAvatarHash(user1);
        assertEquals(avatarHash1, hash);
    }
    
    @Test
    public void testGetAvatarWithMultipleAvatarProviderReturnsFirstAvatar() {
        CombinedAvatarProvider testling = createProvider();
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider2.avatars.put(user1, avatarHash2);
        testling.addProvider(avatarProvider1);
        testling.addProvider(avatarProvider2);
        String hash = testling.getAvatarHash(user1);
        assertEquals(avatarHash1,hash);
    }
    
    @Test
    public void testGetAvatarWithMultipleAvatarProviderAndFailingFirstProviderReturnsSecondAvatar() {
        CombinedAvatarProvider testling = createProvider();
        avatarProvider2.avatars.put(user1, avatarHash2);
        testling.addProvider(avatarProvider1);
        testling.addProvider(avatarProvider2);
        String hash = testling.getAvatarHash(user1);
        assertEquals(avatarHash2,hash);
    }
    
    @Test
    public void testProviderUpdateTriggersChange() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider1.onAvatarChanged.emit(user1);
        assertEquals(1,changes.size());
        assertEquals(user1,changes.get(0));
    }
    
    @Test
    public void testProviderUpdateWithoutChangeDoesNotTriggerChange() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        testling.addProvider(avatarProvider2);
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider1.onAvatarChanged.emit(user1);
        changes.clear();
        
        avatarProvider2.avatars.put(user1, avatarHash2);
        avatarProvider2.onAvatarChanged.emit(user1);
        
        assertEquals(0,changes.size());
    }
    
    @Test
    public void testProviderSecondUpdateTriggersChange() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider1.onAvatarChanged.emit(user1);
        changes.clear();
        avatarProvider1.avatars.put(user1, avatarHash2);
        avatarProvider1.onAvatarChanged.emit(user1);
        
        assertEquals(1,changes.size());
        assertEquals(user1,changes.get(0));
    }
    
    @Test
    public void testProviderUpdateWithAvatarDisappearingTriggersChange() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider1.onAvatarChanged.emit(user1);
        changes.clear();
        avatarProvider1.avatars.clear();
        avatarProvider1.onAvatarChanged.emit(user1);
        assertNull(testling.getAvatarHash(user1));
        assertEquals(1, changes.size());
        assertEquals(user1,changes.get(0));
    }
    
    @Test
    public void testProviderUpdateAfterAvatarDisappearedTriggersChange() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider1.onAvatarChanged.emit(user1);
        avatarProvider1.avatars.clear();
        avatarProvider1.onAvatarChanged.emit(user1);
        changes.clear();
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider1.onAvatarChanged.emit(user1);
        assertEquals(1,changes.size());
        assertEquals(user1,changes.get(0));
    }
    
    @Test
    public void testProviderUpdateAfterGetDoesNotTriggerChange() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        avatarProvider1.avatars.put(user1, avatarHash1);
        testling.getAvatarHash(user1);
        avatarProvider1.onAvatarChanged.emit(user1);
        assertEquals(0,changes.size());
    }
    
    @Test
    public void testRemoveProviderDisconnectsUpdates() {
        CombinedAvatarProvider testling = createProvider();
        testling.addProvider(avatarProvider1);
        testling.addProvider(avatarProvider2);
        testling.removeProvider(avatarProvider1);
        avatarProvider1.avatars.put(user1, avatarHash1);
        avatarProvider2.avatars.put(user1, avatarHash2);
        avatarProvider1.onAvatarChanged.emit(user1);
        assertEquals(0,changes.size());
    }
    
    @Test
    public void testProviderUpdateBareJIDAfterGetFullJID() {
        CombinedAvatarProvider testling = createProvider();
        avatarProvider1.useBare = true;
        testling.addProvider(avatarProvider1);
        
        avatarProvider1.avatars.put(user1.toBare(),avatarHash1);
        testling.getAvatarHash(user1);
        avatarProvider1.avatars.put(user1.toBare(),avatarHash2);
        avatarProvider1.onAvatarChanged.emit(user1.toBare());
        
        String hash = testling.getAvatarHash(user1);
        assertEquals(avatarHash2,hash);
    }
    
    @Test
    public void testAddRemoveFallthrough() {
        JID ownJID = new JID("user0@own.com/res");
        JID user1 = new JID("user1@bar.com/bla");
        
        CryptoProvider crypto = new JavaCryptoProvider();
        DummyStanzaChannel stanzaChannel = new DummyStanzaChannel();
        stanzaChannel.setAvailable(true);
        IQRouter iqRouter = new IQRouter(stanzaChannel);
        MUCRegistry mucRegistry = new MUCRegistry();
        AvatarMemoryStorage avatarStorage = new AvatarMemoryStorage();
        VCardMemoryStorage vCardStorage = new VCardMemoryStorage(crypto);
        VCardManager vcardManager = new VCardManager(ownJID, iqRouter, vCardStorage);
        
        VCardUpdateAvatarManager updateManager = 
                new VCardUpdateAvatarManager(vcardManager, stanzaChannel, avatarStorage, 
                        crypto, mucRegistry);
        updateManager.onAvatarChanged.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
                handleAvatarChanged(jid);
            }
            
        });
        
        VCardAvatarManager manager = 
                new VCardAvatarManager(vcardManager, avatarStorage, crypto, mucRegistry);
        manager.onAvatarChanged.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
                handleAvatarChanged(jid);
            }
            
        });
        
        OfflineAvatarManager offlineManager = new OfflineAvatarManager(avatarStorage);
        offlineManager.onAvatarChanged.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
                handleAvatarChanged(jid);
            }
            
        });
        
        CombinedAvatarProvider testling = createProvider();
        avatarProvider1.useBare = true;
        testling.addProvider(updateManager);
        testling.addProvider(manager);
        testling.addProvider(offlineManager);
        
        // Place an avatar in the cache, check that it reads back OK
        
        assertEquals(0,changes.size());
        
        ByteArray avatar1 = new ByteArray("abcdefg");
        String avatar1Hash = Hexify.hexify(crypto.getSHA1Hash(avatar1));
        VCard vcard1 = new VCard();
        vcard1.setPhoto(avatar1);
        
        vCardStorage.setVCard(user1.toBare(), vcard1);
        String testHash = testling.getAvatarHash(user1.toBare());
        assertEquals(avatar1Hash,testHash);
        
        VCard storedVCard = vCardStorage.getVCard(user1.toBare());
        assertNotNull(storedVCard);
        testHash = Hexify.hexify(crypto.getSHA1Hash(storedVCard.getPhoto()));
        assertEquals(avatar1Hash, testHash);
        
        // Change the Avatar by sending a VCard IQ
        
        vcardManager.requestVCard(user1.toBare());
        assertEquals(1,stanzaChannel.sentStanzas.size());
        IQ request = (IQ) stanzaChannel.sentStanzas.lastElement();
        VCard payload = request.getPayload(new VCard());
        assertNotNull(payload);
        stanzaChannel.sentStanzas.clear();
        
        ByteArray avatar2 = new ByteArray("1234567");
        String avatar2Hash = Hexify.hexify(crypto.getSHA1Hash(avatar2));
        VCard vcard2 = new VCard();
        vcard2.setPhoto(avatar2);
        
        IQ reply = new IQ();
        reply.setTo(request.getFrom());
        reply.setFrom(request.getTo());
        reply.setID(request.getID());
        reply.addPayload(vcard2);
        reply.setType(IQ.Type.Result);
        
        stanzaChannel.onIQReceived.emit(reply);
        
        // Check that we changed the avatar succesfully and that we were notified of the changes
        
        testHash = testling.getAvatarHash(user1.toBare());
        assertEquals(avatar2Hash,testHash);
        assertEquals(3,changes.size());
        assertEquals(user1.toBare(),changes.get(0));
        assertEquals(user1.toBare(),changes.get(1));
        assertEquals(user1.toBare(),changes.get(2));
        changes.clear();
        storedVCard = vCardStorage.getVCard(user1.toBare());
        assertNotNull(storedVCard);
        testHash = Hexify.hexify(crypto.getSHA1Hash(storedVCard.getPhoto()));
        assertEquals(avatar2Hash,testHash);
        
        // Change the avatar to an empty avatar
        
        vcardManager.requestVCard(user1.toBare());
        assertEquals(1,stanzaChannel.sentStanzas.size());
        request = (IQ) stanzaChannel.sentStanzas.lastElement();
        payload = request.getPayload(new VCard());
        assertNotNull(payload);
        stanzaChannel.sentStanzas.clear();
        
        VCard vcard3 = new VCard();
        reply = new IQ();
        reply.setTo(request.getFrom());
        reply.setFrom(request.getTo());
        reply.setID(request.getID());
        reply.addPayload(vcard3);
        reply.setType(IQ.Type.Result);
        stanzaChannel.onIQReceived.emit(reply);
        
        // Check that we changed the avatar successfully
        
        testHash = testling.getAvatarHash(user1.toBare());
        assertNotNull(testHash);
        assertEquals("",testHash);
        assertEquals(3,changes.size());
        assertEquals(user1.toBare(),changes.get(0));
        assertEquals(user1.toBare(),changes.get(1));
        assertEquals(user1.toBare(),changes.get(2));
        changes.clear();
        storedVCard = vCardStorage.getVCard(user1.toBare());
        assertNotNull(storedVCard);
        assertNull(storedVCard.getPhoto());
    }
    
    private CombinedAvatarProvider createProvider() {
        CombinedAvatarProvider result = new CombinedAvatarProvider();
        result.onAvatarChanged.connect(new Slot1<JID>() {
            
            @Override
            public void call(JID jid) {
                handleAvatarChanged(jid);
            }
            
        });
        return result;
    }
    
    private void handleAvatarChanged(JID jid) {
        changes.add(jid);
    }
    
    private static class DummyAvatarProvider extends AvatarProvider {

        @Override
        public String getAvatarHash(JID jid) {
            JID actualJID = useBare ? jid.toBare() : jid;
            if (avatars.containsKey(actualJID)) {
                return avatars.get(actualJID);
            }
            return null;
        }

        @Override
        public void delete() {
            // Empty Method
        }
        
        private boolean useBare = false;

        private final Map<JID,String> avatars = new HashMap<JID,String>();
        
    }
    
}
