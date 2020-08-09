/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.net.endpoint.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;

import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;
import br.ufc.great.caos.api.config.CaosConfig;
import br.ufc.great.caos.api.offloadingreasoner.OffloadingReasonerClient;
import br.ufc.great.caos.api.profile.ProfileMonitor;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.DiscoveryData;

/**
 * Discovery service is based on broadcast. The server has a broadcast ip and we
 * use that to connect
 */
public class DiscoveryClient extends Thread {

	private Integer connectPort;
	private BasicCoapClient client;
	boolean isRunning = false;
	boolean serverCreated = false;
	CaosConfig config;

	private static DiscoveryClient sDiscoveryClient = null;

	String BROADCASTADDRESS = "";

	private DiscoveryClient() {
		client = new BasicCoapClient();
		connectPort = Ports.discoveryReceive;
		config = Caos.sClassToBeAnalised.getClass().getAnnotation(CaosConfig.class);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public static DiscoveryClient getInstance() {
		if (sDiscoveryClient == null) {
			sDiscoveryClient = new DiscoveryClient();
		}
		return sDiscoveryClient;
	}

	@Override
	public void run() {
		isRunning = true;
		client.channelManager = BasicCoapChannelManager.getInstance();
		while (true) {
			if (Util.hasConnection(Caos.getContext())) {
				client.sendDiscoveryRequest();
			}
			try {
				Thread.sleep(15000);
			} catch (Exception e) {
				System.out.println("CAOS - Service of processing offloading not found");
			}
		}
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendDiscoveryRequest() {
			try {
				if (config.primaryEndpoint().equals("")) {
					String response = discoveryCloudlet();

					if (response != null) {
						BROADCASTADDRESS = response;
					} else {
						BROADCASTADDRESS = "";	
					}
				} else {
					BROADCASTADDRESS = config.primaryEndpoint();
				}

				if (!BROADCASTADDRESS.equals("")) {
					Log.i("Discovery", "Endpoint: " + BROADCASTADDRESS);

					clientChannel = channelManager.connect(this, InetAddress.getByName(BROADCASTADDRESS),
							Ports.discoveryRequest);
					CoapRequest coapRequest = clientChannel.createRequest(false, CoapRequestCode.POST);
					String[] dataDevice = { String.valueOf(connectPort), Util.getIp() };
					coapRequest.setPayload(Util.wrap(dataDevice));
					clientChannel.sendMessage(coapRequest);
				} else {
					Log.i("Discovery", "Endpoint not found !!");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
			System.out.println("Connection Failed");
		}

		@Override
		public void onResponse(CoapClientChannel channel, CoapResponse response) {
			DiscoveryData discoveryData = (DiscoveryData) Util.unwrap(response.getPayload());

			Ports.updatePorts(discoveryData.getAuthentication(), discoveryData.getOffloading(),
					discoveryData.getPacketLoss(), discoveryData.getProfileSync(), discoveryData.getReasoner(),
					discoveryData.getRtt(), discoveryData.getThroughput());

			Caos.coapForOffloading = discoveryData.getCoapForOffloading();
			Caos.sCloudletIp = BROADCASTADDRESS;

			startAuthentication();
			startProfileMonitor();
			startOffloadingReasonerObserver();
		}
	}

	private void startOffloadingReasonerObserver() {
		OffloadingReasonerClient client = OffloadingReasonerClient.getInstance();
		if (!OffloadingReasonerClient.isRunning()) {
			client.start();
		}
	}

	/**
	 * Starts the authentication process. Basically, the authentication process gets
	 * the ip from cloud and call the authentication service
	 */
	private void startAuthentication() {
		AuthenticationClient client = AuthenticationClient.getInstance();
		client.renew();
	}

	/**
	 * After authentication, it starts the profilemonitor
	 */
	private void startProfileMonitor() {
		ProfileMonitor.initProfiling();
	}

	public String discoveryCloudlet() {
		// Find the server using UDP broadcast
		try {
			// Open a random port to send the package
			DatagramSocket c = new DatagramSocket();
			c.setBroadcast(true);

			byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();
			// Try the 255.255.255.255 first
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName("255.255.255.255"), 31020);
				c.send(sendPacket);
			} catch (Exception e) {
			}
			// Broadcast the message over all the network interfaces
			Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();

					if (broadcast == null) {
						continue;
					}
					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 31020);
						c.send(sendPacket);
					} catch (Exception e) {
					}
				}
			}
			// Wait for a response
			byte[] recvBuf = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			c.setSoTimeout(500);
			c.receive(receivePacket);
			// Check if the message is correct
			String message = new String(receivePacket.getData()).trim();
			
			if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
				// DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
				return receivePacket.getAddress().getHostAddress();
			}

			// Close the port!
			c.close();
		} catch (IOException ex) {}
			
		return null;
	}
}