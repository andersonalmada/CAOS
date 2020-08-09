/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.ubibroker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.interfaces.IDomain;
import br.ufc.great.syssu.base.interfaces.IReaction;

public class LocalUbiBroker {

    private List<IReaction> mReactions;

    private LocalUbiBroker() throws IOException {
        this.mReactions = new ArrayList<IReaction>();
    }

    public static LocalUbiBroker createUbibroker() 
    		throws IOException {
        LocalUbiBroker instance = new LocalUbiBroker();
        return instance;
    }
    
    public IDomain getDomain(String name) throws TupleSpaceException {
        return new LocalDomain(name, this);
    }

    void addReaction(IReaction reaction) throws TupleSpaceException {
        mReactions.add(reaction);
    }
}
