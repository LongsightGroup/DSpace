/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.dspace.vocabularies;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class LW
{

    public static final String NAMESPACE = "http://simile.mit.edu/longwell/";
    
    private static ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final URI NS = vf.createURI(NAMESPACE);
    
    public static final URI textref = vf.createURI(NAMESPACE, "textref");

}
