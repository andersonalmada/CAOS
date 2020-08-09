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
import java.util.HashMap;
import java.util.Map;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;

import android.annotation.SuppressLint;
import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.ProfileSyncData;

/**
 * This class sends the list of local executions information to the cloudlet.
 */
public class ProfileSyncClient extends Thread implements CaosService {
	private Map<String, HashMap<Integer, Long>> knownLocalExectime;
	private Map<String, ProfileSyncData> lastLocalExectime;

	BasicCoapClient client;

	private static ProfileSyncClient sProfileSyncClient = null;

	private static boolean isRunning = false;

	public static boolean isRunning() {
		return isRunning;
	}

	private ProfileSyncClient() {
		this.knownLocalExectime = new HashMap<String, HashMap<Integer, Long>>();
		this.lastLocalExectime = new HashMap<String, ProfileSyncData>();
		client = new BasicCoapClient();
	}

	public static ProfileSyncClient getInstance() {
		if (sProfileSyncClient == null) {
			sProfileSyncClient = new ProfileSyncClient();
		}
		return sProfileSyncClient;
	}

	@Override
	public void run() {
		Log.i("ProfileSync", "Starting synchronization...");
		isRunning = true;
		client.channelManager = BasicCoapChannelManager.getInstance();
		while (true) {
			if (ProfileMonitor.isMonitoringRunning()) {
				ArrayList<ProfileSyncData> syncList = generateSyncList();

				if (syncList.isEmpty()) {
					break;
				}
				if (Util.hasConnection(Caos.getContext())) {
					client.sendProfileSyncData(syncList);
				}
			}
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isRunning = false;
	}

	public void shutDown() {
		Log.i("ProfileSync", "Stopping profiling...");
		isRunning = false;
		Thread.currentThread().interrupt();
	}

	@SuppressLint("UseSparseArrays")
	private ArrayList<ProfileSyncData> generateSyncList() {
		Log.i("ProfileSync", "generating Sync List...");
		ArrayList<ProfileSyncData> resultList = new ArrayList<ProfileSyncData>();
		this.lastLocalExectime = ProfileMonitor.getLastLocalExectime();

		for (String methodId : this.lastLocalExectime.keySet()) {
			HashMap<Integer, Long> argExectimePair = new HashMap<Integer, Long>();
			argExectimePair.put(this.lastLocalExectime.get(methodId).getArgSize(),
					this.lastLocalExectime.get(methodId).getExecTime());
			for (int argsize : argExectimePair.keySet()) {
				long exectime = argExectimePair.get(argsize);
				if (knownLocalExectime.containsKey(methodId)) {
					if (knownLocalExectime.get(methodId).containsKey(argsize)) {
						long minThreshold = (long) (exectime * 0.9);
						long maxThreshold = (long) (exectime * 1.1);
						if (knownLocalExectime.get(methodId).get(argsize) < minThreshold
								|| knownLocalExectime.get(methodId).get(argsize) > maxThreshold) {
							// update the known value
							knownLocalExectime.get(methodId).put(argsize, exectime);
							resultList.add(lastLocalExectime.get(methodId));
						}
					} else {
						// create argsize on knownLocalExectime.get(methodId)
						// with exectime
						knownLocalExectime.get(methodId).put(argsize, exectime);
						resultList.add(lastLocalExectime.get(methodId));
					}
				} else {
					// create methodID key on knownLocalExectime and add argsize
					// and exectime. mark to send
					HashMap<Integer, Long> dados = new HashMap();
					dados.put(argsize, exectime);
					knownLocalExectime.put(methodId, dados);
					resultList.add(lastLocalExectime.get(methodId));
				}
			}
		}
		return resultList;
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendProfileSyncData(ArrayList<ProfileSyncData> syncList) {
			try {
				clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp),
						Ports.profileSync);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);

				coapRequest.setPayload(Util.wrap(syncList));

				clientChannel.sendMessage(coapRequest);
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
			System.out.println("Received response");
			ProfileMonitor.cleanLastLocalExectime();
		}
	}
}
