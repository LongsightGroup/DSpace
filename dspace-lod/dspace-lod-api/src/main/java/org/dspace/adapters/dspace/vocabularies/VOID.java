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


/**
 * User: minipillai @ atmire . com
 * Date: 1/17/14
 * Time: 1:30 PM
 */
public class VOID
{
    public static final ValueFactory vf = ValueFactoryImpl.getInstance();
    public static final String NAMESPACE = "http://rdfs.org/ns/void#";

    public static final URI dataset = vf.createURI(NAMESPACE, "Dataset");

    public static final URI title = vf.createURI(NAMESPACE, "Title");

    public static final URI description = vf.createURI(NAMESPACE, "Description");

    public static final URI creator = vf.createURI(NAMESPACE, "Creator");

    public static final URI homepage = vf.createURI(NAMESPACE, "Homepage");

    public static final URI sparqlEndPoint = vf.createURI(NAMESPACE, "SparqlEndPoint");
}

