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
import com.isode.stroke.elements.PubSubOwnerPayload;

public class PubSubOwnerAffiliations extends PubSubOwnerPayload {

public PubSubOwnerAffiliations() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public ArrayList<PubSubOwnerAffiliation> getAffiliations() {
	return affiliations_;
}

public void setAffiliations(ArrayList<PubSubOwnerAffiliation> affiliations) {
	affiliations_ = affiliations;
}

public void addAffiliation(PubSubOwnerAffiliation value) {
	affiliations_.add(value);
}

private String node_ = "";
private ArrayList<PubSubOwnerAffiliation> affiliations_ = new ArrayList<PubSubOwnerAffiliation>();

}
