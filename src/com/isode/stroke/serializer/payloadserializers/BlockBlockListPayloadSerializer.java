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

import com.isode.stroke.elements.BlockListPayload;

/**
 * {@link BlockSerializer} for {@link BlockListPayload}
 */
public class BlockBlockListPayloadSerializer extends BlockSerializer<BlockListPayload> {

    /**
     * Constructor 
     * @param tag Tag
     */
    protected BlockBlockListPayloadSerializer(String tag) {
        super(BlockListPayload.class, tag);
    }

}
