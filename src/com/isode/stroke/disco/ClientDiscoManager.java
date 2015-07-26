/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.presence.PayloadAddingPresenceSender;
import com.isode.stroke.presence.PresenceSender;
import com.isode.stroke.queries.IQRouter;

public class ClientDiscoManager {
    private PayloadAddingPresenceSender presenceSender;
    private CryptoProvider crypto;
    private DiscoInfoResponder discoInfoResponder;
    private String capsNode = "";
    private CapsInfo capsInfo;

    /**
     * Constructs the manager
     * 
     * \param iqRouter the router on which requests will be answered \param
     * presenceSender the presence sender to which all outgoing presence (with
     * caps information) will be sent.
     */
    public ClientDiscoManager(IQRouter iqRouter, PresenceSender presenceSender,
            CryptoProvider crypto) {
        this.crypto = crypto;
        discoInfoResponder = new DiscoInfoResponder(iqRouter);
        discoInfoResponder.start();
        this.presenceSender = new PayloadAddingPresenceSender(presenceSender);
    }

    void delete() {
        discoInfoResponder.stop();
    }

    /**
     * Needs to be called before calling setDiscoInfo().
     */
    public void setCapsNode(final String node) {
        capsNode = node;
    }

    /**
     * Sets the capabilities of the client.
     */
    public void setDiscoInfo(final DiscoInfo discoInfo) {
        capsInfo = new CapsInfoGenerator(capsNode, crypto).generateCapsInfo(discoInfo);
        discoInfoResponder.clearDiscoInfo();
        discoInfoResponder.setDiscoInfo(discoInfo);
        discoInfoResponder.setDiscoInfo(
                capsInfo.getNode() + "#" + capsInfo.getVersion(), discoInfo);
        presenceSender.setPayload(capsInfo);
    }

    /**
     * Returns the presence sender through which all outgoing presence should be
     * sent. The manager will add the necessary caps information, and forward it
     * to the presence sender passed at construction time.
     */
    public PresenceSender getPresenceSender() {
        return presenceSender;
    }

    /**
     * Called when the client is connected. This resets the presence sender,
     * such that it assumes initial presence hasn't been sent yet.
     */
    public void handleConnected() {
        presenceSender.reset();
    }

}
