/*
 * Copyright (c) 2013-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Signal3;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import java.util.logging.Logger;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.stringcodecs.Hexify;

public class DefaultFileTransferTransporterFactory implements FileTransferTransporterFactory {

	private SOCKS5BytestreamRegistry s5bRegistry; 
	private SOCKS5BytestreamServerManager s5bServerManager;
	private SOCKS5BytestreamProxiesManager s5bProxiesManager; 
	private IDGenerator idGenerator; 
	private ConnectionFactory connectionFactory; 
	private TimerFactory timerFactory; 
	private CryptoProvider cryptoProvider;
	private IQRouter iqRouter;

	DefaultFileTransferTransporterFactory(
		SOCKS5BytestreamRegistry s5bRegistry, 
		SOCKS5BytestreamServerManager s5bServerManager,
		SOCKS5BytestreamProxiesManager s5bProxy, 
		IDGenerator idGenerator, 
		ConnectionFactory connectionFactory, 
		TimerFactory timerFactory, 
		CryptoProvider cryptoProvider,
		IQRouter iqRouter) {
		this.s5bRegistry = s5bRegistry;
		this.s5bProxiesManager = s5bProxy;
		this.s5bServerManager = s5bServerManager;
		this.idGenerator = idGenerator;
		this.connectionFactory = connectionFactory;
		this.timerFactory = timerFactory;
		this.cryptoProvider = cryptoProvider;
		this.iqRouter = iqRouter;
	}

	public FileTransferTransporter createInitiatorTransporter(
			final JID initiator, final JID responder, final FileTransferOptions options) {
		DefaultFileTransferTransporter transporter = new DefaultFileTransferTransporter(
			initiator, 
			responder,
			DefaultFileTransferTransporter.Role.Initiator,
			s5bRegistry,
			s5bServerManager,
			s5bProxiesManager,
			idGenerator,
			connectionFactory,
			timerFactory,
			cryptoProvider,
			iqRouter,
			options);
		transporter.initialize();
		return transporter;
	}

	public FileTransferTransporter createResponderTransporter(
			final JID initiator, final JID responder, final String s5bSessionID, final FileTransferOptions options) {
		DefaultFileTransferTransporter transporter = new DefaultFileTransferTransporter(
			initiator, 
			responder,
			DefaultFileTransferTransporter.Role.Responder,
			s5bRegistry,
			s5bServerManager,
			s5bProxiesManager,
			idGenerator,
			connectionFactory,
			timerFactory,
			cryptoProvider,
			iqRouter,
			options);
		transporter.initialize(s5bSessionID);
		return transporter;
	}
}