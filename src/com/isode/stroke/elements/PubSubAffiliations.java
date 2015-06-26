/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import java.util.ArrayList;
import com.isode.stroke.elements.PubSubPayload;

public class PubSubAffiliations extends PubSubPayload {

public PubSubAffiliations() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public ArrayList<PubSubAffiliation> getAffiliations() {
	return affiliations_;
}

public void setAffiliations(ArrayList<PubSubAffiliation> affiliations) {
	affiliations_ = affiliations;
}

public void addAffiliation(PubSubAffiliation value) {
	affiliations_.add(value);
}

private String node_;
private ArrayList<PubSubAffiliation> affiliations_ = new ArrayList<PubSubAffiliation>();

}
