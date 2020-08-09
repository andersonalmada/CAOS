/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval;

public class EvaluationException extends Exception {

	private static final long serialVersionUID = 1L;

	public EvaluationException() {
		super();
	}

	public EvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvaluationException(String message) {
		super(message);
	}

	public EvaluationException(Throwable cause) {
		super(cause);
	}

}
