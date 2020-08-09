/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.var.BooleanVariable;
import br.ufc.great.syssu.eval.var.Variable;

public class Evaluator {

	public static boolean eval(Tuple tuple, Expression expression) throws EvaluationException {
		if (expression instanceof BooleanVariable) {
			return ((BooleanVariable) expression).getValuation(tuple);
		}
		
		if(expression == null) {
			return true;
		}

		switch (expression.getOperator()) {
		case TIMESTAMP:
			return eval(tuple, expression.getLeftSide()) && 
					eval(tuple, expression.getRightSide());
			
		case MATCH:
			return eval(tuple, expression.getLeftSide()) && 
					eval(tuple, expression.getRightSide());
		
		case AND:
			return eval(tuple, expression.getLeftSide()) && 
					eval(tuple, expression.getRightSide());
		case OR:
			return eval(tuple, expression.getLeftSide()) || 
					eval(tuple, expression.getRightSide());

		case NOT:
			return !eval(tuple, expression.getLeftSide());

		case EQ:
			Object obj = ((Variable< ? >) expression.getLeftSide()).getValuation(tuple);
			return obj.equals(((Variable< ? >) expression.getRightSide()).
					getValuation(tuple));

		case LT:
			return checkLT(tuple, (Variable< ? >) expression.getLeftSide(), 
					(Variable< ? >) expression.getRightSide());

		case GT:
			return checkGT(tuple, (Variable< ? >) expression.getLeftSide(), 
					(Variable< ? >) expression.getRightSide()); 

		case LTEQ:
			return checkLTEQ(tuple, (Variable< ? >) expression.getLeftSide(), 
					(Variable< ? >) expression.getRightSide());

		case GTEQ:
			return checkGTEQ(tuple, (Variable< ? >) expression.getLeftSide(), 
					(Variable< ? >) expression.getRightSide());

		default:
			throw new EvaluationException("Invalid operator: " + expression.toString());
		}
	}
	
	public static String clearExpression(Expression exp) {
		return exp.toString().replace("\"", "");
	}

	private static boolean checkLT(Tuple tuple, Variable< ? > left, Variable< ? > rigth) 
			throws EvaluationException {
		Number numLeft = (Number) left.getValuation(tuple);
		Number numRight = (Number) rigth.getValuation(tuple);
		if (isInteger(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.intValue() < numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.intValue() < numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.intValue() < numRight.doubleValue();
			}
		}

		if (isFloat(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.floatValue() < numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.floatValue() < numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.floatValue() < numRight.doubleValue();
			}
		}

		if (isDouble(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.doubleValue() < numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.doubleValue() < numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.doubleValue() < numRight.doubleValue();
			}
		}
		return false;
	}

	private static boolean checkLTEQ(Tuple tuple, Variable< ? > left, Variable< ? > rigth) 
			throws EvaluationException {
		Number numLeft = (Number) left.getValuation(tuple);
		Number numRight = (Number) rigth.getValuation(tuple);
		if (isInteger(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.intValue() <= numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.intValue() <= numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.intValue() <= numRight.doubleValue();
			}
		}

		if (isFloat(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.floatValue() <= numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.floatValue() <= numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.floatValue() <= numRight.doubleValue();
			}
		}

		if (isDouble(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.doubleValue() <= numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.doubleValue() <= numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.doubleValue() <= numRight.doubleValue();
			}
		}
		return false;
	}

	private static boolean checkGT(Tuple tuple, Variable< ? > left, Variable< ? > rigth) 
			throws EvaluationException {
		Number numLeft = (Number) left.getValuation(tuple);
		Number numRight = (Number) rigth.getValuation(tuple);
		if (isInteger(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.intValue() > numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.intValue() > numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.intValue() > numRight.doubleValue();
			}
		}

		if (isFloat(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.floatValue() > numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.floatValue() > numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.floatValue() > numRight.doubleValue();
			}
		}

		if (isDouble(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.doubleValue() > numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.doubleValue() > numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.doubleValue() > numRight.doubleValue();
			}
		}
		return false;
	}

	private static boolean checkGTEQ(Tuple tuple, Variable< ? > left, Variable< ? > rigth) 
			throws EvaluationException {
		Number numLeft = (Number) left.getValuation(tuple);
		Number numRight = (Number) rigth.getValuation(tuple);
		if (isInteger(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.intValue() >= numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.intValue() >= numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.intValue() >= numRight.doubleValue();
			}
		}

		if (isFloat(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.floatValue() >= numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.floatValue() >= numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.floatValue() >= numRight.doubleValue();
			}
		}

		if (isDouble(numLeft)) {
			if (isInteger(numRight)) {
				return numLeft.doubleValue() >= numRight.intValue();
			} else if (isFloat(numRight)) {
				return numLeft.doubleValue() >= numRight.floatValue();
			} else if (isDouble(numRight)) {
				return numLeft.doubleValue() >= numRight.doubleValue();
			}
		}
		return false;
	}

	private static boolean isInteger(Number number) {
		return number instanceof Integer;
	}

	private static boolean isFloat(Number number) {
		return number instanceof Float;
	}

	private static boolean isDouble(Number number) {
		return number instanceof Double;
	}
}