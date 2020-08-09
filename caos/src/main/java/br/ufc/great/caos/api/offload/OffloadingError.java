/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.offload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent the errors that may occur during the offloading process.
 * If the device receive an OffloadingError object, it will perform local execution instead.
 */
public class OffloadingError implements Serializable {

	private static final long serialVersionUID = 726614134768436477L;
	
	private List<String> mErrors;
	
	public OffloadingError(List<String> errors) {
	    this.mErrors = errors;
	}
	
	public OffloadingError() {
		mErrors = new ArrayList<String>();
	}
	
	public void add(String errorMessage) {
		mErrors.add(errorMessage);
	}
	
	@Override
	public String toString() {
		String result = "OFFLOADING ERROR\n";
		for (String msg : mErrors) {
			result += msg + "\n";
		}
		return result;
	}
}
