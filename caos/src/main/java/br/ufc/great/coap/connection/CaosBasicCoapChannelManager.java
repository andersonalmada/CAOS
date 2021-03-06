/** Copyright [2011] [University of Rostock]
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*****************************************************************************/
package br.ufc.great.coap.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.ws4d.coap.Constants;
import org.ws4d.coap.connection.BasicCoapServerChannel;
import org.ws4d.coap.connection.BasicCoapSocketHandler;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapMessage;
import org.ws4d.coap.interfaces.CoapServer;
import org.ws4d.coap.interfaces.CoapServerChannel;
import org.ws4d.coap.interfaces.CoapSocketHandler;
import org.ws4d.coap.messages.BasicCoapRequest;

public class CaosBasicCoapChannelManager implements CoapChannelManager {
	// global message id
	private final static Logger logger = Logger.getLogger(CaosBasicCoapChannelManager.class);
	private int globalMessageId;
	private static CaosBasicCoapChannelManager instance;
	private HashMap<Integer, SocketInformation> socketMap = new HashMap<Integer, SocketInformation>();
	CoapServer serverListener = null;

	private CaosBasicCoapChannelManager() {
		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		logger.setLevel(Level.WARN);
		initRandom();
	}

	public synchronized static CaosBasicCoapChannelManager getInstance() {
		if (instance == null) {
			instance = new CaosBasicCoapChannelManager();
		}
		return instance;
	}

	/**
	 * Creates a new server channel
	 */
	@Override
	public synchronized CoapServerChannel createServerChannel(CoapSocketHandler socketHandler, CoapMessage message,
			InetAddress addr, int port) {
		SocketInformation socketInfo = socketMap.get(socketHandler.getLocalPort());

		if (socketInfo.serverListener == null) {
			/* this is not a server socket */
			throw new IllegalStateException("Invalid server socket");
		}

		if (!message.isRequest()) {
			throw new IllegalStateException("Incomming message is not a request message");
		}

		CoapServer server = socketInfo.serverListener.onAccept((BasicCoapRequest) message);
		if (server == null) {
			/* Server rejected channel */
			return null;
		}
		CoapServerChannel newChannel = new BasicCoapServerChannel(socketHandler, server, addr, port);
		return newChannel;
	}

	/**
	 * Creates a new, global message id for a new COAP message
	 */
	@Override
	public synchronized int getNewMessageID() {
		if (globalMessageId < Constants.MESSAGE_ID_MAX) {
			++globalMessageId;
		} else
			globalMessageId = Constants.MESSAGE_ID_MIN;
		return globalMessageId;
	}

	@Override
	public synchronized void initRandom() {
		// generate random 16 bit messageId
		Random random = new Random();
		globalMessageId = random.nextInt(Constants.MESSAGE_ID_MAX + 1);
	}

	@Override
	public void createServerListener(CoapServer serverListener, int localPort) {
		if (!socketMap.containsKey(localPort)) {
			try {
				SocketInformation socketInfo = new SocketInformation(new BasicCoapSocketHandler(this, localPort),
						serverListener);
				socketMap.put(localPort, socketInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalStateException();
		}
	}

	public void caosCreateServerListener(CoapServer serverListener, int localPort) throws IOException {
		if (!socketMap.containsKey(localPort)) {
			SocketInformation socketInfo = new SocketInformation(new BasicCoapSocketHandler(this, localPort),
					serverListener);
			socketMap.put(localPort, socketInfo);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public CoapClientChannel connect(CoapClient client, InetAddress addr, int port) {
		CoapSocketHandler socketHandler = null;
		try {
			socketHandler = new BasicCoapSocketHandler(this);
			SocketInformation sockInfo = new SocketInformation(socketHandler, null);
			socketMap.put(socketHandler.getLocalPort(), sockInfo);
			return socketHandler.connect(client, addr, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class SocketInformation {
		public CoapServer serverListener = null;

		public SocketInformation(CoapSocketHandler socketHandler, CoapServer serverListener) {
			super();
			this.serverListener = serverListener;
		}
	}

	@Override
	public void setMessageId(int globalMessageId) {
		this.globalMessageId = globalMessageId;
	}
}
