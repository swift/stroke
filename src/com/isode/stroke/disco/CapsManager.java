/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;

public class CapsManager extends CapsProvider {

    private final IQRouter iqRouter;
    private final CryptoProvider crypto;
    private final CapsStorage capsStorage;
    private boolean warnOnInvalidHash;
    private Set<String> requestedDiscoInfos = new HashSet<String>();
    private Set<CapsPair> failingCaps = new HashSet<CapsPair>();
    private Map<String, Set<CapsPair>> fallbacks = new HashMap<String, Set<CapsPair>>();

    private class CapsPair {
        JID jid;
        String node;

        CapsPair(JID j, String n) {jid = j; node = n;}

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CapsPair)) return false;
            CapsPair o1 = (CapsPair) o;
            return jid.equals(o1.jid) && node.equals(o1.node);
        }

        @Override public int hashCode() {return jid.hashCode() * 5 + node.hashCode();}
    }

    public CapsManager(CapsStorage capsStorage, StanzaChannel stanzaChannel,
            IQRouter iqRouter, CryptoProvider crypto) {
        this.iqRouter = iqRouter;
        this.crypto = crypto;
        this.capsStorage = capsStorage;
        this.warnOnInvalidHash = true;

        stanzaChannel.onPresenceReceived.connect(new Slot1<Presence>() {
            @Override
            public void call(Presence p1) {
                handlePresenceReceived(p1);
            }
        });
        stanzaChannel.onAvailableChanged.connect(new Slot1<Boolean>() {
            @Override
            public void call(Boolean p1) {
                handleStanzaChannelAvailableChanged(p1);
            }
        });
    }

    private void handlePresenceReceived(Presence presence) {
        CapsInfo capsInfo = presence.getPayload(new CapsInfo());
        if (capsInfo == null || !capsInfo.getHash().equals("sha-1")
                || presence.getPayload(new ErrorPayload()) != null) {
            return;
        }
        String hash = capsInfo.getVersion();
        if (capsStorage.getDiscoInfo(hash) != null) {
            return;
        }
        if (failingCaps.contains(new CapsPair(presence.getFrom(), hash))) {
            return;
        }
        if (requestedDiscoInfos.contains(hash)) {
            Set<CapsPair> fallback = fallbacks.get(hash);
            if (fallback == null) fallbacks.put(hash, fallback = new HashSet<CapsPair>());
            fallback.add(new CapsPair(presence.getFrom(), capsInfo.getNode()));
            return;
        }
        requestDiscoInfo(presence.getFrom(), capsInfo.getNode(), hash);
    }

    private void handleStanzaChannelAvailableChanged(boolean available) {
        if (available) {
            failingCaps.clear();
            fallbacks.clear();
            requestedDiscoInfos.clear();
        }
    }

    private void handleDiscoInfoReceived(final JID from, final String hash, DiscoInfo discoInfo, ErrorPayload error) {
        requestedDiscoInfos.remove(hash);
        if (error != null || discoInfo == null
                || !new CapsInfoGenerator("", crypto).generateCapsInfo(discoInfo).getVersion().equals(hash)) {
            if (warnOnInvalidHash && error == null && discoInfo != null) {
//                std.cerr << "Warning: Caps from " << from.toString() << " do not verify" << std.endl;
            }
            failingCaps.add(new CapsPair(from, hash));
            Set<CapsPair> i = fallbacks.get(hash);
            if (i != null && !i.isEmpty()) {
                CapsPair fallbackAndNode = i.iterator().next();
                i.remove(fallbackAndNode);
                requestDiscoInfo(fallbackAndNode.jid, fallbackAndNode.node, hash);
            }
            return;
        }
        fallbacks.remove(hash);
        capsStorage.setDiscoInfo(hash, discoInfo);
        onCapsAvailable.emit(hash);
    }

    private void requestDiscoInfo(final JID jid, final String node, final String hash) {
        GetDiscoInfoRequest request = GetDiscoInfoRequest.create(jid, node
                + "#" + hash, iqRouter);
        request.onResponse.connect(new Slot2<DiscoInfo, ErrorPayload>() {
            @Override
            public void call(DiscoInfo p1, ErrorPayload p2) {
                handleDiscoInfoReceived(jid, hash, p1, p2);
            }
        });
        requestedDiscoInfos.add(hash);
        request.send();
    }

    @Override
    DiscoInfo getCaps(String hash) {
        return capsStorage.getDiscoInfo(hash);
    }

    // Mainly for testing purposes
    void setWarnOnInvalidHash(boolean b) {
        warnOnInvalidHash = b;
    }

}
