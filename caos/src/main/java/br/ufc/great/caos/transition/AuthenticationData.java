/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

import br.ufc.great.caos.api.util.device.ApplicationBean;

/**
 * Data containing device information for the authentication proccess
 */
public class AuthenticationData implements Serializable {

	private static final long serialVersionUID = -9093437231208590588L;

	private String mMobileId;
	private String mProcessorCores;
	private String mProcessorType;
	private String mProcessorBogomips;
	private String mDeviceType;
	private String mTotalMemory;
	private String mMaxHeap;
	private ApplicationBean mApplication;
	private String deviceIp;

	public AuthenticationData() {
		// TODO Auto-generated constructor stub
	}

	public String getMobileId() {
		return mMobileId;
	}

	public void setMobileId(String mobileId) {
		this.mMobileId = mobileId;
	}

	public String getProcessorCores() {
		return mProcessorCores;
	}

	public void setProcessorCores(String processorCores) {
		this.mProcessorCores = processorCores;
	}

	public String getProcessorType() {
		return mProcessorType;
	}

	public void setProcessorType(String processorType) {
		this.mProcessorType = processorType;
	}

	public String getProcessorBogomips() {
		return mProcessorBogomips;
	}

	public void setProcessorBogomips(String processorBogomips) {
		this.mProcessorBogomips = processorBogomips;
	}

	public String getDeviceType() {
		return mDeviceType;
	}

	public void setDeviceType(String deviceType) {
		this.mDeviceType = deviceType;
	}

	public String getTotalMemory() {
		return mTotalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.mTotalMemory = totalMemory;
	}

	public String getMaxHeap() {
		return mMaxHeap;
	}

	public void setMaxHeap(String maxHeap) {
		this.mMaxHeap = maxHeap;
	}

	@Override
	public String toString() {
		return "[" + "deviceType=" + getDeviceType() + " mobileId=" + getMobileId() + "]";
	}

	public ApplicationBean getApplication() {
		return mApplication;
	}

	public void setApplication(ApplicationBean application) {
		this.mApplication = application;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}
}