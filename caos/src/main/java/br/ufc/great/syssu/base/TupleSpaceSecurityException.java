/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

/**
 * Security exception of tuple space.
 */
public class TupleSpaceSecurityException extends Exception {

    /**
     * Default Constructor.
     */
	public TupleSpaceSecurityException() {
		super();
	}

	/**
	 * Constructor using a string as the error message.
     *
	 * @param message the error message.
	 */
	public TupleSpaceSecurityException(String message) {
		super(message);
	}
	
}
