/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the BSD license.
 * See http://www.opensource.org/licenses/bsd-license.php for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.DeliveryReceiptRequest;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class DeliveryReceiptRequestParser extends GenericPayloadParser<DeliveryReceiptRequest> {

    public DeliveryReceiptRequestParser() {
        super(new DeliveryReceiptRequest());
    }
    
    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        
    }

    public void handleEndElement(String element, String ns) {
        
    }

    public void handleCharacterData(String data) {
        
    }


}
