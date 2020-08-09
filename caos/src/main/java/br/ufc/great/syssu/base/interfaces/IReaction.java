/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

import android.os.RemoteException;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;

public interface IReaction {

	/**
	 * Set the id of the reaction
	 * @param id the id of the reaction.
	 */
	void setId(String id);

	/**
	 * Get the id of the reaction
	 * @return the id of the reaction.
	 */
	String getId();

	/**
	 * Get the pattern used to match tuples.
	 * @return the pattern used to match tuples.
	 */
	Pattern getPattern();
	
	/**
	 * Get the filter that uses Javascript.
	 * @return the javascript filter.
	 *
	 * @deprecated use java filters instead.
	 */
	String getJavaScriptFilter();
	
	/**
	 * Get the filter that uses Java.
	 * @return the java filter.
	 */
	IFilter getJavaFilter();

	/**
	 * Callback method that gives tuples that matches the query as soon as they are available.
	 * @param tuple the tuple that matches the pattern and pass the filter.
	 * @throws RemoteException if an exception occurs during the Android communication between process.
	 */
	void react(Tuple tuple) throws RemoteException;
}
