/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.eval.EvaluationException;
import br.ufc.great.syssu.eval.Evaluator;

/**
 * Class used to apply the filters to tuples.
 */
public class TupleFilter {
	
	/**
	 * Apply the java filter to the tuple.
     *
	 * @param tuple the tuple to be filtered.
	 * @param filter the java filter.
	 * @return true if the tuple pass the filter, false otherwise.
	 */
	public static boolean doFilter(Tuple tuple, IFilter filter) {
		if (tuple != null) {
			if (filter != null) {					
				try {
					return Evaluator.eval(tuple, filter.localFilter());
				} catch (EvaluationException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
}
