/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;


/**
 * Exception of the tuple space.
 */
public class TupleSpaceException extends Exception {

	/**
	 * Constructor using a string as the error message.
     *
	 * @param string the error message.
	 */
    public TupleSpaceException(String string) {
        super(string);
    }

    /**
     * Constructor using a throwable as the error message.
     *
     * @param ex the error message.
     */
	public TupleSpaceException(Throwable ex) {
		super(ex);
	}
    
}
