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

public class File
{

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final String NAMESPACE = "http://schema.oceanlink.org/file#";

    public static final URI hasFileAttribute = vf.createURI(NAMESPACE, "hasFileAttribute");

    public static final URI hasFileAttributeKind = vf.createURI(NAMESPACE, "hasFileAttributeKind");

    public static final URI hasFileAttributeUnit = vf.createURI(NAMESPACE, "hasFileAttributeUnit");

    public static final URI hasFileAttributeValue = vf.createURI(NAMESPACE, "hasFileAttributeValue");

    public static final URI File = vf.createURI(NAMESPACE, "File");

    public static final URI FileAttribute = vf.createURI(NAMESPACE, "FileAttribute");

    public static final URI FileAttributeKind = vf.createURI(NAMESPACE, "FileAttributeKind");

    public static final URI FileAttributeUnit = vf.createURI(NAMESPACE, "FileAttributeUnit");

    public static final URI FileAttributeValue = vf.createURI(NAMESPACE, "FileAttributeValue");
}