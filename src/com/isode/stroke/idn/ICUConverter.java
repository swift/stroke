/*
 * Copyright (c) 2012-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.idn;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.IDNA;
import com.ibm.icu.text.StringPrep;
import com.ibm.icu.text.StringPrepParseException;

public class ICUConverter implements IDNConverter {

	public String getStringPrepared(String s, StringPrepProfile profile) throws IllegalArgumentException {
		StringPrep str = StringPrep.getInstance(getICUProfileType(profile));
		try {
		    String preparedData = str.prepare(s, StringPrep.DEFAULT);
		    return preparedData;
		}catch(StringPrepParseException e){
		    throw new IllegalArgumentException(e);
		}
	}

	public SafeByteArray getStringPrepared(SafeByteArray s, StringPrepProfile profile) throws IllegalArgumentException {
		StringPrep str = StringPrep.getInstance(getICUProfileType(profile));

		try {
		    String preparedData = str.prepare(s.toString(), StringPrep.DEFAULT);
		    return new SafeByteArray(preparedData);
		}catch(StringPrepParseException e){
		    throw new IllegalArgumentException(e);
		}
	}

	public String getIDNAEncoded(String s) {
		return IDNA.getEncoded(s);
	}

	private int getICUProfileType(IDNConverter.StringPrepProfile profile) {
		switch(profile) {
			case NamePrep: return StringPrep.RFC3491_NAMEPREP;
			case XMPPNodePrep: return StringPrep.RFC3920_NODEPREP;
			case XMPPResourcePrep: return StringPrep.RFC3920_RESOURCEPREP;
			case SASLPrep: return StringPrep.RFC4013_SASLPREP;
		}
		assert(false);
		return StringPrep.RFC3491_NAMEPREP;
	}
}