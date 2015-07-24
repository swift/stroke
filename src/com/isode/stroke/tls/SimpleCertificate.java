/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.tls;

import java.util.List;
import java.util.ArrayList;
import com.isode.stroke.base.ByteArray;

public class SimpleCertificate extends Certificate {

	private String subjectName = "";
	private ByteArray der = new ByteArray();
	private List<String> commonNames = new ArrayList<String>();
	private List<String> dnsNames = new ArrayList<String>();
	private List<String> xmppAddresses = new ArrayList<String>();
	private List<String> srvNames = new ArrayList<String>();

	public void setSubjectName(final String name) {
		subjectName = name;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public List<String> getCommonNames() {
		return commonNames;
	}

	public void addCommonName(final String name) {
		commonNames.add(name);
	}

	public void addSRVName(final String name) {
		srvNames.add(name);
	}

	public void addDNSName(final String name) {
		dnsNames.add(name);
	}

	public void addXMPPAddress(final String addr) {
		xmppAddresses.add(addr);
	}

	public List<String> getSRVNames() {
		return srvNames;
	}

	public List<String> getDNSNames() {
		return dnsNames;
	}

	public List<String> getXMPPAddresses() {
		return xmppAddresses;
	}

	public ByteArray toDER() {
		return der;
	}

	public void setDER(final ByteArray der) {
		this.der = der;
	}

	private void parse() {

	}
}