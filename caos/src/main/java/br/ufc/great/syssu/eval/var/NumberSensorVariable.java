/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

/**
 * Created by Almada on 10/07/2015.
 */
public class NumberSensorVariable extends Variable<Number> {

	public NumberSensorVariable(String name) {
		super(name);
	}

	@Override
	public Number getValuation(Tuple tuple) throws EvaluationException {
		Number result = 0;

		if (tuple.getField("sensor") != null) {
			Tuple tuple2 = (Tuple) tuple.getField("sensor").getValue();
			
			if(tuple2.getField(this.getName()) == null) {
				return result;
			}
			
			result = Double.parseDouble(tuple2.getField(this.getName()).getValue().toString());

			if (result == null) {
				throw new EvaluationException("The symbol \"" + this.toString() + "\" is not found in the assignment.");
			}
		} 
		
		return result;
	}
}
