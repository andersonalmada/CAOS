/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.util.device;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import br.ufc.great.caos.api.profile.ProfileMonitor;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.AuthenticationData;

/**
 * This class helps to get some device information.
 */
public final class Device extends AuthenticationData {

	private static final long serialVersionUID = -460165956096248530L;
	private Context mContext;

	public Device(Context context) {
		this.mContext = context;
		updateMobileId();
		updateDeviceType();
		updateCpuInfo();
		setTotalMemory(ProfileMonitor.sTotalMemory);
		setMaxHeap(ProfileMonitor.sMaxHeap);

		try {
			ApplicationBean applicationBean = new ApplicationBean(getApplicationVersion(), context.getPackageName());
			setApplication(applicationBean);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	public AuthenticationData getBean() {
		AuthenticationData deviceBean = new AuthenticationData();
		deviceBean.setMobileId(getMobileId());
		deviceBean.setProcessorCores(getProcessorCores());
		deviceBean.setProcessorType(getProcessorType());
		deviceBean.setProcessorBogomips(getProcessorBogomips());
		deviceBean.setDeviceType(getDeviceType());
		deviceBean.setTotalMemory(getTotalMemory());
		deviceBean.setMaxHeap(getMaxHeap());
		deviceBean.setApplication(getApplication());

		return deviceBean;
	}

	public void updateMobileId() {
		String deviceId = generateUniqueId();
		setMobileId(deviceId);
	}

	private String generateUniqueId() {
		return getDeviceId(mContext);
	}

	private void updateDeviceType() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			setDeviceType(model);
		} else {
			setDeviceType(manufacturer + " " + model);
		}
	}

	private void updateCpuInfo() {
		setProcessorCores(String.valueOf(Runtime.getRuntime().availableProcessors()));
		try {
			Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStream is = proc.getInputStream();
			String[] cpuInfoArray = Util.getStringFromInputStream(is).split(System.getProperty("line.separator"));
			for (int i = 0; i < cpuInfoArray.length; i++) {
				if (cpuInfoArray[i].contains("Processor")) {
					setProcessorType(cpuInfoArray[i].split(":")[1].trim());
				}
				if (cpuInfoArray[i].contains("BogoMIPS")) {
					setProcessorBogomips(cpuInfoArray[i].split(":")[1].trim());
					break;
				}
			}
		} catch (IOException e) {
			Log.e("ERROR", e.getMessage());
		}
	}

	/**
	 * @return version
	 * @throws NameNotFoundException
	 */
	public String getApplicationVersion() throws NameNotFoundException {
		PackageManager manager = mContext.getPackageManager();
		PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
		String version = info.versionName;
		return version;
	}

	private String getDeviceId(Context context) {

		String deviceId;

		if (android.os.Build.VERSION.SDK_INT >= 29) {
			deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		} else {
			final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (mTelephony.getDeviceId() != null) {
				deviceId = mTelephony.getDeviceId();
			} else {
				deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			}
		}

		return deviceId;
	}
}