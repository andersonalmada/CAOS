/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import br.ufc.great.syssu.base.interfaces.IMatcheable;

/**
 * Class that represents the contextual information.
 */
public class Tuple extends AbstractFieldCollection<TupleField> implements 
	IMatcheable, Parcelable, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private long mTimeToLive;
	private long mPutTime;
	
	@Override
    public Object clone() {
        try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }
	
	/**
	 * Default Constructor.
	 */
	public Tuple() {
		super();
	}

	/**
	 * Constructor with time to live.
     *
	 * @param timeToLive the time to live of this tuple.
	 */
	public Tuple(long timeToLive) {
		super();
		setTimeToLive(timeToLive);
	}

	@Override
	public TupleField createField(String name, Object value) {
		return new TupleField(name, value);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean matches(Query query) throws FilterException {
		if (query == null || query.getPattern() == null  || 
				query.getPattern().isEmpty() || this.isEmpty()) {
			return true;
		}		

        return associatesAll(query.getPattern()) && TupleFilter.doFilter(
        		this, query.getJavaFilter());	
	}

	/**
	 * Get the time to live of this tuple.
     *
	 * @return the time to live of this tuple.
	 */
	public long getTimeToLive() {
		return mTimeToLive;
	}

	/**
	 * Set the time to live of this tuple.
     *
	 * @param timeToLive the time to live of this tuple.
	 */
	public void setTimeToLive(long timeToLive) {
		if (timeToLive < 0) {
			throw new InvalidParameterException("Negative time");
		}
		this.mTimeToLive = timeToLive;
	}

	/**
	 * Get the put time of this tuple.
     *
	 * @return the put time of this tuple.
	 */
	public long getPutTime() {
		return mPutTime;
	}

    /**
     * Set the put time of this tuple.
     *
     * @return the put time of this tuple.
     */
	public void setPutTime(long putTime) {
		if (putTime < 0) {
			throw new InvalidParameterException("Negative time");
		}
		this.mPutTime = putTime;
	}

	/**
	 * Check if the tuple is alive.
     *
	 * @return true if the tuple is alive, false otherwise.
	 */
	public boolean isAlive() {
		return mTimeToLive == 0 || System.currentTimeMillis() - mPutTime < mTimeToLive;
	}

	/**
	 * Associates all the tuple fields.
     *
	 * @param pattern the pattern to associate with the tuple fields.
	 * @return true if all field matches, false otherwise.
	 */
	private boolean associatesAll(Pattern pattern) {
		boolean matches = true;

		for (PatternField pField : pattern) {
			matches = (matches) ? associatesOne(pField) : false;
		}
		return matches;
	}

	/**
	 * Associate one single field of the tuple.
     *
	 * @param pField the field of the pattern to be associated.
	 * @return true if the fields matches, false otherwise.
	 */
	private boolean associatesOne(PatternField pField) {
		for (TupleField tField : this) {
			if (tField.associates(pField)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		
		parcel.writeInt(size());
		
		for (int i = 0; i < size(); i++) {
			TupleField tf = getField(i);
			
			if (tf.getType().equals("?boolean")) {
				parcel.writeString("?boolean");
				parcel.writeString(tf.getName());
				parcel.writeString(tf.getValue().toString());
	        } else if (tf.getType().equals("?integer")) {
				parcel.writeString("?integer");
				parcel.writeString(tf.getName());
				parcel.writeInt((Integer)tf.getValue());
	        } else if (tf.getType().equals("?float")) {
	        	parcel.writeString("?float");
				parcel.writeString(tf.getName());
				parcel.writeFloat((Float)tf.getValue());
	        } else if (tf.getType().equals("?string")) {
	        	parcel.writeString("?string");
				parcel.writeString(tf.getName());
				parcel.writeString(tf.getValue().toString());
	        } else if (tf.getType().equals("?object")) {
	        	parcel.writeString("?object");
				parcel.writeString(tf.getName());
				parcel.writeParcelable((Tuple)tf.getValue(), flags);
	        } else if (tf.getType().equals("?array")) {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>)tf.getValue();
				Object[] array = new String[list.size()];
	        	
	        	for (int j = 0; j < list.size(); j++) {
					array[j] = list.get(j);
				}
	        	
	        	parcel.writeString("?array");
				parcel.writeString(tf.getName());
				parcel.writeArray(array);
	        }	
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Tuple> CREATOR = new Parcelable.Creator<Tuple>() {
		public Tuple createFromParcel(Parcel parcel) {
			Tuple t = new Tuple();
			
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
					t = (Tuple) t.addField(name, bValue);
		        } else if (type.equals("?integer")) { 
		        	iValue = parcel.readInt();
					t = (Tuple) t.addField(name, iValue);
		        } else if (type.equals("?float")) {
		        	fValue = parcel.readFloat();
					t = (Tuple) t.addField(name, fValue);
		        } else if (type.equals("?string")) {
		        	sValue = parcel.readString();
					t = (Tuple) t.addField(name, sValue);
		        } else if (type.equals("?object")) {
		        	tValue = (Tuple)parcel.readParcelable(Tuple.class.getClassLoader());
					t = (Tuple) t.addField(name, tValue);
		        } else if (type.equals("?array")) { // Suporta apenas array de Strings
		        	lValue = new String[parcel.readInt()];
		        	parcel.readStringArray(lValue);
		        	t = (Tuple) t.addField(name, Arrays.asList(lValue));
		        }	
				
			}
			
			return t;
		}

		public Tuple[] newArray(int size) {
			return new Tuple[size];
		}
	};
}
