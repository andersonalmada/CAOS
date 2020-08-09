/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

import br.ufc.great.syssu.base.FilterException;
import br.ufc.great.syssu.base.Query;

public interface IMatcheable {
	/**
	 * Query match
	 * @param query the query to be checked if match or not.
	 * @return true if match, false otherwise.
	 * @throws FilterException if some error occurs inside the filter.
	 */
    boolean matches(Query query) throws FilterException;
}
