/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.StartTLSRequest;

class StartTLSParser extends GenericElementParser<StartTLSRequest> {

    public StartTLSParser() {
        super(StartTLSRequest.class);
    }

}
