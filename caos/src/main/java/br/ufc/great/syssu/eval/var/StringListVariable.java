/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import java.util.ArrayList;
import java.util.List;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

/**
 * Created by Almada on 10/07/2015.
 */
public class StringListVariable extends Variable<String> {

	private int mPosition;

	public StringListVariable(String name, int position) {
		super(name);
		this.mPosition = position;
	}

	@Override
	public String getValuation(Tuple tuple) throws EvaluationException {
		List list = new ArrayList();
		String result = new String();
		
		if (tuple.getField(this.getName()) != null) {
			list = (List) tuple.getField(this.getName()).getValue();

			result = String.valueOf(list.get(mPosition));

			if (result == null) {
				throw new EvaluationException("The symbol \"" + this.toString() + "\" is not found in the assignment.");
			}
		}
		
		return result;
	}
}
