/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

public class NumberVariable extends Variable<Number> {

	public NumberVariable(String name) {
		super(name);
	}

	@Override
	public Number getValuation(Tuple tuple) throws EvaluationException {
		Number result = 0;

		if (tuple.getField(this.getName()) != null) {
			result = Double.parseDouble(tuple.getField(this.getName()).getValue().toString());
			
			if (result == null) {
				throw new EvaluationException("The symbol \"" + this.toString() + "\" is not found in the assignment.");
			}

		} 
		
		return result;
	}

}
