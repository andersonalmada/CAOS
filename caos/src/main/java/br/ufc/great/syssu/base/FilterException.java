/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

/**
 * Filter exception.
 */
public class FilterException extends Exception {

	/**
	 * Default Constructor.
     *
	 * @param message the error message.
	 */
    public FilterException(String message) {
        super(message);
    }
}
