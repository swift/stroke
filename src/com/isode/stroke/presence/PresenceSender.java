/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.presence;

import com.isode.stroke.elements.Presence;

/**
 * Interface to be implemented by a Presence Sender
 *
 */
public interface PresenceSender {
    /**
     * Send a Presence
     * @param pres presence, not null
     */
    public void sendPresence(Presence pres);

    /**
     * Determine if the PresenceSender is available
     * @return true if available and false otherwise
     */
    public boolean isAvailable();
}