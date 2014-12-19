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
 * User: mini @ atmire . com
 * Date: 8/27/14
 * Time: 3:05 PM
 */
public class AgentRole {

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final String NAMESPACE = "http://schema.oceanlink.org/agent-role#";

    public static final URI AgentRole = vf.createURI(NAMESPACE, "AgentRole");

    public static final URI Author = vf.createURI(NAMESPACE, "Author");

    public static final URI isAgentRoleIn = vf.createURI(NAMESPACE, "isAgentRoleIn");

    public static final URI isPerformedBy = vf.createURI(NAMESPACE, "isPerformedBy");

    public static final URI hasAgentRoleType = vf.createURI(NAMESPACE, "hasAgentRoleType");

}
