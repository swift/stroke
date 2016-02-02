/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.UnblockPayload;

/**
 * {@link BlockSerializer} for {@link UnblockPayload}
 */
public class BlockUnblockPayloadSerializer extends BlockSerializer<UnblockPayload> {

    /**
     * @param tag Tag
     */
    public BlockUnblockPayloadSerializer(String tag) {
        super(UnblockPayload.class, tag);
    }

}
