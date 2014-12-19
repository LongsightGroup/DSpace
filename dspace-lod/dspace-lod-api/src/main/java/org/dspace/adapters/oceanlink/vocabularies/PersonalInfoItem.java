/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.oceanlink.vocabularies;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * Utility Class for URI related to {@link http://schema.oceanlink.org/personal-info-item}
 *
 * @author Mini Pillai (mini at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class PersonalInfoItem {

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final String NAMESPACE = "http://schema.oceanlink.org/personal-info-item#";

    public static final URI PersonalInfoItem = vf.createURI(NAMESPACE, "PersonalInfoItem");

    public static final URI isPersonalInfoItemOf = vf.createURI(NAMESPACE, "isPersonalInfoItemOf");

    public static final URI hasPersonalInfoValue = vf.createURI(NAMESPACE, "hasPersonalInfoValue");

}
