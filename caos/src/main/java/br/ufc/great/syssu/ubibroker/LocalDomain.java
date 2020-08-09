/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.ubibroker;

import java.util.List;

import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;
import br.ufc.great.syssu.base.interfaces.IDomain;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.ILocalDomain;
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.great.syssu.coordubi.TupleSpace;

public class LocalDomain implements ILocalDomain {

	private ILocalDomain mCentreDomain;

    private String mName;
    private LocalUbiBroker mUbiBroker;

    LocalDomain(String domainName, LocalUbiBroker ubiBroker) throws TupleSpaceException {
        this.mName = domainName;
        this.mUbiBroker = ubiBroker;

    	mCentreDomain = (ILocalDomain)TupleSpace.getInstance().getDomain(getName());
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void put(Tuple tuple, String key) throws TupleSpaceException {
		try {
			tuple.setTimeToLive(tuple.getTimeToLive());
			mCentreDomain.put(tuple, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
    }

    @Override
    public List<Tuple> read(Pattern pattern, String restriction, String key) 
    		throws TupleSpaceException {
        List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.read(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public List<Tuple> readSync(Pattern pattern, String restriction, String key, long timeout)
    	throws TupleSpaceException {
        List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.readSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public Tuple readOne(Pattern pattern, String restriction, String key) 
    		throws TupleSpaceException {
        Tuple tuple = null;
        try {
        	tuple = mCentreDomain.readOne(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;

    }

    @Override
    public Tuple readOneSync(Pattern pattern, String restriction, String key, long timeout)
    	throws TupleSpaceException {
    	Tuple tuple = null;
        try {
        	tuple = mCentreDomain.readOneSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;
    }

    @Override
    public List<Tuple> take(Pattern pattern, String restriction, String key) 
    		throws TupleSpaceException {
    	List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.take(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public List<Tuple> takeSync(Pattern pattern, String restriction, String key, long timeout)
    	throws TupleSpaceException {
    	List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.takeSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public Tuple takeOne(Pattern pattern, String restriction, String key) 
    		throws TupleSpaceException {
    	Tuple tuple = null;
        try {
        	tuple = mCentreDomain.takeOne(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;
    }

    @Override
    public Tuple takeOneSync(Pattern pattern, String restriction, String key, long timeout)
    	throws TupleSpaceException {
    	Tuple tuple = null;
        try {
        	tuple = mCentreDomain.takeOneSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;
    }

    public IDomain getDomain(String name) throws TupleSpaceException {
        return new LocalDomain(this.mName + "." + name, mUbiBroker);
    }

    @Override
    public String subscribe(IReaction reaction, String event, String key) 
    		throws TupleSpaceException {
    	String s = null;
    	try {
			 s = mCentreDomain.subscribe(reaction, event, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
          return s;
    }

    @Override
	public void unsubscribe(String reactionId, String key) throws TupleSpaceException {
    	try {
			mCentreDomain.unsubscribe(reactionId, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
	}
    
    
    /*
     * Using Java Filters
     */

    @Override
    public List<Tuple> read(Pattern pattern, IFilter restriction, String key) 
    		throws TupleSpaceException {
        List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.read(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public List<Tuple> readSync(Pattern pattern, IFilter restriction, String key, long timeout)
    	throws TupleSpaceException {
        List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.readSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public Tuple readOne(Pattern pattern, IFilter restriction, String key) 
    		throws TupleSpaceException {
        Tuple tuple = null;
        try {
        	tuple = mCentreDomain.readOne(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;

    }

    @Override
    public Tuple readOneSync(Pattern pattern, IFilter restriction, String key, long timeout)
    	throws TupleSpaceException {
    	Tuple tuple = null;
        try {
        	tuple = mCentreDomain.readOneSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;
    }

    @Override
    public List<Tuple> take(Pattern pattern, IFilter restriction, String key) 
    		throws TupleSpaceException {
    	List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.take(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public List<Tuple> takeSync(Pattern pattern, IFilter restriction, String key, long timeout)
    	throws TupleSpaceException {
    	List<Tuple> tuples = null;
        try {
        	tuples = mCentreDomain.takeSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuples;
    }

    @Override
    public Tuple takeOne(Pattern pattern, IFilter restriction, String key) 
    		throws TupleSpaceException {
    	Tuple tuple = null;
        try {
        	tuple = mCentreDomain.takeOne(pattern, restriction, key);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;
    }

    @Override
    public Tuple takeOneSync(Pattern pattern, IFilter restriction, String key, long timeout)
    	throws TupleSpaceException {
    	Tuple tuple = null;
        try {
        	tuple = mCentreDomain.takeOneSync(pattern, restriction, key, timeout);
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
		return tuple;
    }
}
