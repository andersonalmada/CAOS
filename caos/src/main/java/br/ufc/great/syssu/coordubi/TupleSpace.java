/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.coordubi;

import java.util.LinkedHashMap;
import java.util.Map;

import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.interfaces.IDomain;
import br.ufc.great.syssu.base.interfaces.IDomainComposite;

public class TupleSpace implements IDomainComposite {

    private static TupleSpace sInstance;
    private Map<String, IDomain> mDomains;

    /**
     * Espaço de tuplas.
     */
    private TupleSpace() {
        mDomains = new LinkedHashMap<String, IDomain>();
    }

    /**
     * Retorna uma instancia do TupleSpace.
     * @return
     */
    public static TupleSpace getInstance() {
        if (sInstance == null) {
            sInstance = new TupleSpace();
        }
        return sInstance;
    }

    @Override
    public IDomain getDomain(String name) throws TupleSpaceException {
        IDomain domain = null;
        String[] s = name.split("\\.");

        if (s.length > 0) {
            String rootName = s[0];
            if (!mDomains.containsKey(rootName)) {
                domain = new Domain(rootName);
                mDomains.put(rootName, domain);
            } else {
                domain = mDomains.get(rootName);
            }

            for (int i = 1; i < s.length; i++) {
                domain = domain.getDomain(s[i]);
            }
        }

        return domain;
    }
}
