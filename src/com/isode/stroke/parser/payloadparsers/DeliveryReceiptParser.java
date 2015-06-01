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

import com.isode.stroke.elements.DeliveryReceipt;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class DeliveryReceiptParser extends GenericPayloadParser<DeliveryReceipt> {
    
    private int level_;
    
    public DeliveryReceiptParser() {
        super(new DeliveryReceipt());
    }
    
    public void handleStartElement(String element, String ns, AttributeMap attributeMap) {
        if (level_ == 0) {
            if ("received".equals(element)) {
                String id = attributeMap.getAttributeValue("id");
                if (id != null) {
                    getPayloadInternal().setReceivedID(id);
                }
            }
        }
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
    }

    public void handleCharacterData(String data) {
        
    }


}
