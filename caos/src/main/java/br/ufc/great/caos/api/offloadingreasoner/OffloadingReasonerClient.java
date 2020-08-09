/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.offloadingreasoner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;

import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;
import br.ufc.great.caos.api.offload.InvocableMethod;
import br.ufc.great.caos.api.profile.ProfileMonitor;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.api.util.device.Device;
import br.ufc.great.caos.transition.OffloadingReasonerData;
import br.ufc.great.caos.weka.AndroidWekaTreeUtil;

/**
 * OffloadingReasoner helps the decision maker. It receives the offloading data
 * structure and check if it's possible to make offloading
 */
public final class OffloadingReasonerClient extends Thread {

	private static boolean isRunning = false;

	public static boolean isRunning() {
		return isRunning;
	}

	private static OffloadingReasonerClient sOffloadingReasonerClient = null;

	BasicCoapClient client;

	private static OffloadingReasonerData dataStructure = new OffloadingReasonerData();

	/**
	 * Method that decides whether the InvocableMethod must be offloaded or not
	 * 
	 * @param invocableMethod
	 * @param data
	 * @return
	 */
	public Boolean makeDecision(InvocableMethod invocableMethod) {
		try {
			synchronized (dataStructure) {
				if (dataStructure.getMethodsList().contains(invocableMethod.getMethodId())) {
					Map<Short, Object> monitorContext = ProfileMonitor.getMonitorContext();
					monitorContext.put((short) 0, invocableMethod.getMethodId());
					// argsize
					monitorContext.put((short) 5, ProfileMonitor.getParametersSize(invocableMethod));
					// use the decision tree to decide
					boolean decision = AndroidWekaTreeUtil.mustOffload(monitorContext, dataStructure.getDecisionTree());

					return decision;
				} else {
					// if the method has never been executed on remote server,
					// offload
					return true;
				}
			}
		} catch (Exception e) {
			// TODO: log exception for debug purpose
			// in case of any error, run locally
			e.printStackTrace();
			return false;
		}
	}

	private static void updateOffloadingDataStructure(OffloadingReasonerData newDataStructure) {
		synchronized (dataStructure) {
			if (newDataStructure != null) {
				dataStructure = newDataStructure;
			}
		}
	}

	private OffloadingReasonerClient() {
		client = new BasicCoapClient();
	}

	public static OffloadingReasonerClient getInstance() {
		if (sOffloadingReasonerClient == null) {
			sOffloadingReasonerClient = new OffloadingReasonerClient();
		}
		return sOffloadingReasonerClient;
	}

	@Override
	public void run() {
		isRunning = true;
		client.channelManager = BasicCoapChannelManager.getInstance();
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Thread.sleep(30000);
				if (Util.hasConnection(Caos.getContext())) {
					client.sendOffloadingReasoningRequest();
				}
			}
		} catch (InterruptedException e) {
			Log.e("OffloadingReasonerClient", e.getLocalizedMessage());
		}
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendOffloadingReasoningRequest() {

			try {
				Device mDevice = new Device(Caos.getContext());
				String[] payloadMessage = { Caos.getContext().getPackageName(), mDevice.getApplicationVersion(), Util.getIp() };

				clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp), Ports.reasoner);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
				coapRequest.setPayload(Util.wrap(payloadMessage));
				clientChannel.sendMessage(coapRequest);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
			System.out.println("Connection Failed");
		}

		@Override
		public void onResponse(CoapClientChannel channel, CoapResponse response) {
			OffloadingReasonerData offloadingReasonerData = (OffloadingReasonerData) Util.unwrap(response.getPayload());
			if(offloadingReasonerData != null) {
				updateOffloadingDataStructure(offloadingReasonerData);
			}
			
		}
	}

}