/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

public class BooleanVariable extends Variable<Boolean> {

	public BooleanVariable(String name) {
		super(name);
	}

	public Boolean getValuation(Tuple tuple) throws EvaluationException {
		Boolean result = false;

		if (tuple.getField(this.getName()) != null) {
			result = Boolean.parseBoolean(tuple.getField(this.getName()).getValue().toString());
			if (result == null) {
				throw new EvaluationException("The symbol \"" + this.toString() + "\" is not found in the assignment.");
			}
		}

		return result;
	}
}
