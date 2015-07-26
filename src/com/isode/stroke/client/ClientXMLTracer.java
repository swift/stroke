/*
 * Copyright (c) 2010-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.client;

import com.isode.stroke.client.CoreClient;
import com.isode.stroke.client.XMLBeautifier;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

public class ClientXMLTracer {

	private XMLBeautifier beautifier;
	private boolean bosh;
	private SignalConnection onDataReadConnection;
	private SignalConnection onDataWrittenConnection;

	public ClientXMLTracer(CoreClient client) {
		this(client, false);
	}

	public ClientXMLTracer(CoreClient client, boolean bosh) {
		this.bosh = bosh;
		beautifier = new XMLBeautifier(true, true);
		onDataReadConnection = client.onDataRead.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray s) {
				printData('<', s);
			}
		});
		onDataWrittenConnection = client.onDataWritten.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray s) {
				printData('>', s);
			}
		});
	}

	private void printData(char direction, final SafeByteArray data) {
		printLine(direction);
		if (bosh) {
			String line = data.toString(); 
	// Disabled because it swallows bits of XML (namespaces, if I recall)
	//		size_t endOfHTTP = line.find("\r\n\r\n");
	//		if (false && endOfHTTP != std::string::npos) {
	//			std::cerr << line.substr(0, endOfHTTP) << std::endl << beautifier->beautify(line.substr(endOfHTTP)) << std::endl;
	//		}
	//		else {
				System.err.println(line);
	//		}
		}
		else {
			System.err.println(beautifier.beautify(data.toString()));
		}
	}

	private void printLine(char c) {
		for (int i = 0; i < 80; ++i) {
			System.err.println(c);
		}
		System.err.println();
	}	
}
