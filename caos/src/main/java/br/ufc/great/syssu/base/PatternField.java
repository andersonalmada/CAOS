/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

public class PatternField extends AbstractField {

    private String mName;
    private Object mValue;
    private String mType;
    private boolean mValueWildCard;
    private static String[] sWildCards =
        {"?", "?boolean", "?integer", "?float", "?string", "?array", "?object"};

    /**
     * Campo de um pattern
     * @param name Nome do campo
     * @param value Valor do campo
     */
    public PatternField(String name, Object value) {
    	verify(name, value);
        this.mName = name;
        this.mValue = value;
        this.mType = FieldUtils.getType(value);
        this.mValueWildCard = containsWildCard(value);
    }
    
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public String getType() {
        return mType;
    }

    /**
     * Verifica se tem o valor WildCard.
     * @return True se tiver, false caso contrario.
     */
    public boolean hasWildCardValue() {
        return mValueWildCard;
    }

    /**
     * Verifica se contem um valor WildCard
     * @param value Valor a ser verificado
     * @return True se tiver, false caso contrario
     */
    private boolean containsWildCard(Object value) {
        for (String string : sWildCards) {
            if (string.equals(value)) {
				return true;
			}
        }
        return false;
    }    
}
