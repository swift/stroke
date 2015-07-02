/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import java.util.TreeMap;
import java.util.Map;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Slot1;

public class EntityCapsManager extends EntityCapsProvider {
    private final CapsProvider capsProvider;
    private final Map<JID, String> caps = new TreeMap<JID, String>();

    public EntityCapsManager(CapsProvider capsProvider, StanzaChannel stanzaChannel) {
        this.capsProvider = capsProvider;
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
        capsProvider.onCapsAvailable.connect(new Slot1<String>() {
            @Override
            public void call(String p1) {
                handleCapsAvailable(p1);
            }
        });
    }

    private void handlePresenceReceived(Presence presence) {
        JID from = presence.getFrom();
        if (presence.isAvailable()) {
            CapsInfo capsInfo = presence.getPayload(new CapsInfo());
            if (capsInfo == null || !capsInfo.getHash().equals("sha-1") || presence.getPayload(new ErrorPayload()) != null) {
                return;
            }
            String hash = capsInfo.getVersion();
            String i = caps.get(from);
            if (!hash.equals(i)) {
                caps.put(from, hash);
                DiscoInfo disco = capsProvider.getCaps(hash);
                if (disco != null || i != null) {
                    onCapsChanged.emit(from);
                }
            }
        }
        else {
            if (caps.remove(from) != null) {
                onCapsChanged.emit(from);
            }
        }
    }

    private void handleStanzaChannelAvailableChanged(boolean available) {
        if (available) {
            for (JID i : caps.keySet()) {
                onCapsChanged.emit(i);
            }
            caps.clear();
        }
    }

    private void handleCapsAvailable(final String hash) {
        // TODO: Use Boost.Bimap ?
        for (JID i : caps.keySet()) {
            if (caps.get(i).equals(hash)) {
                onCapsChanged.emit(i);
            }
        }
    }

    public DiscoInfo getCaps(final JID jid) {
        if (caps.containsKey(jid)) {
            return capsProvider.getCaps(caps.get(jid));
        }
        return null;
    }
}
