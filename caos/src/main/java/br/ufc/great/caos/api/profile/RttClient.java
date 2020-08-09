/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.profile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.RttPingData;
import br.ufc.great.caos.transition.RttResultData;

/**
 * This class is responsible for calculating the ping time interval between the
 * device and the cloudlet.
 */
public class RttClient implements Runnable, CaosService {

	private static RttClient sRttClient = null;

	private static boolean isRunning = false;

	public static boolean isRunning() {
		return isRunning;
	}

	BasicCoapClient client;
	List<Long> pings = Collections.synchronizedList(new ArrayList<Long>());

	private RttClient() {
		client = new BasicCoapClient();
	}

	public static RttClient getInstance() {
		if (sRttClient == null) {
			sRttClient = new RttClient();
		}
		return sRttClient;
	}

	public void run() {
		isRunning = true;
		client.channelManager = BasicCoapChannelManager.getInstance();
		if (Util.hasConnection(Caos.getContext())) {
			client.sendRtt();
		}
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendResult(RttResultData result) {
			try {
				clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp), Ports.rtt);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);

				coapRequest.setPayload(Util.wrap(result));
				clientChannel.sendMessage(coapRequest);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		public void sendRtt() {
			for (int i = 0; i < 10; i++) {
				try {
					clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp), Ports.rtt);
					CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);

					RttPingData sendTime = new RttPingData(System.currentTimeMillis(), Util.getIp());
					coapRequest.setPayload(Util.wrap(sendTime));
					clientChannel.sendMessage(coapRequest);

				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			isRunning = false;
		}

		@Override
		public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
			System.out.println("Connection Failed");
		}

		@Override
		public void onResponse(CoapClientChannel channel, CoapResponse response) {
			System.out.println("Received response");

			Object requestObject = Util.unwrap(response.getPayload());
			if (requestObject instanceof RttPingData) {
				RttPingData sendTime = (RttPingData) requestObject;
				Long receiveTime = System.currentTimeMillis();
				sendTime.setIpDevice(Util.getIp());
				synchronized (pings) {
					pings.add(receiveTime - sendTime.getTime());
					checkPing();
				}
			}
		}
	}

	private synchronized void checkPing() {
		if (pings.size() == 10) {
			processPing();
		}
	}

	private void processPing() {
		double min = 100000;
		double max = -1;
		double avg = 0;
		double sum = 0;
		for (Long ping : pings) {
			sum += ping;
			if (ping < min) {
				min = ping;
			}
			if (ping > max) {
				max = ping;
			}
		}
		avg = sum / (double) pings.size();

		RttResultData result = new RttResultData(String.valueOf(min), String.valueOf(max), String.valueOf(avg), Util.getIp());
		Log.i("RttProfile", "RESULT:" + result.getRttMin() + ":" + result.getRttMax() + ":" + result.getRttAvg());

		ProfileMonitor.setRTT(avg);

		if (Util.hasConnection(Caos.getContext())) {
			client.sendResult(result);
		}
	}

	@Override
	public void shutDown() {
		Log.i("RttProfile", "Stoping profiling...");
		isRunning = false;
		Thread.currentThread().interrupt();
	}
}
