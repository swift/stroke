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

import com.isode.stroke.elements.BlockPayload;

/**
 * {@link BlockSerializer} for {@link BlockPayload}
 */
public class BlockBlockPayloadSerializer extends BlockSerializer<BlockPayload> {

    /**
     * Constructor
     * @param tag Tag
     */
    public BlockBlockPayloadSerializer(String tag) {
        super(BlockPayload.class, tag);
    }

}
