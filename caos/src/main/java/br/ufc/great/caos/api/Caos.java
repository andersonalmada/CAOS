/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import br.ufc.great.caos.api.net.endpoint.service.DiscoveryClient;
import br.ufc.great.caos.api.offload.OffloadingMonitor;
import br.ufc.great.caos.data.DataManager;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IFilter;

@SuppressLint("NewApi")
public final class Caos {

	/**
	 * In the discovery service we put this ip as the cloudlet ip.
	 */
	public static String sCloudletIp;
	private final String mClassName = Caos.class.getName();
	private final static Caos INSTANCE = new Caos();
	private static Context sContext;
	private DiscoveryClient mDosCloudletMulticast;
	public static Object sClassToBeAnalised;
	public static final int DEFAULT_PORT = 5775;
	public static boolean coapForOffloading = false;
	private DataManager dataManager;

	public static Caos getInstance() {
		return INSTANCE;
	}

	public static Context getContext() {
		return sContext;
	}

	/**
	 * It starts the CAOS.
	 *
	 * @param classToBeAnalised
	 *            - class to be Analised, it could be any type of class
	 * @param context
	 *            - Application Context
	 */
	public void start(Object classToBeAnalised, Context context) {
		Caos.sContext = context;
		Caos.sClassToBeAnalised = classToBeAnalised;
		dataManager = new DataManager(classToBeAnalised);
		startDiscoveryService();
	}
	
	public void makeData() {
		dataManager.makeData();
	}
	
	public List<Tuple> filter(Pattern pattern, IFilter filter) {
		return DataManager.filter(pattern, filter, sContext);
	}

	private void startDiscoveryService() {
		Log.i(mClassName, "Starting discovery Service...");

		mDosCloudletMulticast = DiscoveryClient.getInstance();
		if (!mDosCloudletMulticast.isRunning()) {
			mDosCloudletMulticast.start();
		}
		startOffloadingMonitor();
	}

	/**
	 * The offloading monitor get all the offloadable methods.
	 * 
	 * @param offloadingService
	 *
	 * @param sClassToBeAnalised
	 *            - activity from the application
	 * @param sContext
	 *            - Application context
	 */
	public static void startOffloadingMonitor() {
		try {
			OffloadingMonitor.getInstance().injectObjects(sClassToBeAnalised, sContext);
		} catch (Exception e) {
			Log.e("OFFLOADING MONITOR", "Some problem during the offloading monitor");
		}
	}

	/**
	 * It stops the CAOS Controller.
	 */
	public void stop() {
		Log.d(mClassName, "Finish CAOS API Framework!");
		mDosCloudletMulticast.interrupt();
	}

}