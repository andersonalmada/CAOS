/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a pattern used to select specific tuples.
 */
public class Pattern extends AbstractFieldCollection<PatternField> implements Parcelable {

	/**
	 * Default Constructor.
	 */
	public Pattern() {
		super();
	}

	@Override
	public PatternField createField(String name, Object value) {
		return new PatternField(name, value);
	}

    @Override
	public void writeToParcel(Parcel parcel, int flags) {
		
		parcel.writeInt(size());
		
		for (int i = 0; i < size(); i++) {
			PatternField pf = getField(i);
			
			if (pf.getType().equals("?boolean")) {
				parcel.writeString("?boolean");
				parcel.writeString(pf.getName());
				parcel.writeString(pf.getValue().toString());
	        } else if (pf.getType().equals("?integer")) {
				parcel.writeString("?integer");
				parcel.writeString(pf.getName());
				parcel.writeInt((Integer)pf.getValue());
	        } else if (pf.getType().equals("?float")) {
	        	parcel.writeString("?float");
				parcel.writeString(pf.getName());
				parcel.writeFloat((Float)pf.getValue());
	        } else if (pf.getType().equals("?string")) {
	        	parcel.writeString("?string");
				parcel.writeString(pf.getName());
				parcel.writeString(pf.getValue().toString());
	        } else if (pf.getType().equals("?object")) {
	        	parcel.writeString("?object");
				parcel.writeString(pf.getName());
				parcel.writeParcelable((Tuple)pf.getValue(), flags);
	        } else if (pf.getType().equals("?array")) { 
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>)pf.getValue();
	        	String[] array = new String[list.size()];
	        	
	        	for ( int j = 0; j < list.size(); j++ ) {
					array[j] = list.get(j);
				}
	        	
	        	parcel.writeString("?array");
				parcel.writeString(pf.getName());
				parcel.writeInt(array.length);
				parcel.writeStringArray(array);
	        }	
		}
	}

    @Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Pattern> 
		CREATOR = new Parcelable.Creator<Pattern>() {
		
		public Pattern createFromParcel(Parcel parcel) {
			Pattern p = new Pattern();
			
			int size = parcel.readInt();
			
			String type;
			String name;
			boolean bValue;
			int iValue;
			float fValue;
			String sValue;
			Tuple tValue;
			String[] lValue;

			for (int i = 0; i < size; i++) {
				type = parcel.readString();
				name = parcel.readString();
				
				if (type.equals("?boolean")) {
					bValue = new Boolean(parcel.readString());
					p = (Pattern) p.addField(name, bValue);
		        } else if (type.equals("?integer")) {
		        	iValue = parcel.readInt();
					p = (Pattern) p.addField(name, iValue);
		        } else if (type.equals("?float")) {
		        	fValue = parcel.readFloat();
					p = (Pattern) p.addField(name, fValue);
		        } else if (type.equals("?string")) {
		        	sValue = parcel.readString();
					p = (Pattern) p.addField(name, sValue);
		        } else if (type.equals("?object")) {
		        	tValue = (Tuple)parcel.readParcelable(
		        			Pattern.class.getClassLoader());
					p = (Pattern) p.addField(name, tValue);
		        } else if (type.equals("?array")) {
		        	lValue = new String[parcel.readInt()];
		        	parcel.readStringArray(lValue);
		        	p = (Pattern) p.addField(name, Arrays.asList(lValue));
		        }	
			}
			
			return p;
		}

		public Pattern[] newArray(int size) {
			return new Pattern[size];
		}
	};
}
