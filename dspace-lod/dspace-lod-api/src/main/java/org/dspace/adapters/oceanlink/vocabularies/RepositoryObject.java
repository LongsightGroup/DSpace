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

public class RepositoryObject
{

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final String NAMESPACE = "http://schema.oceanlink.org/repository-object#";

    public static final URI belongsTo = vf.createURI(NAMESPACE, "belongsTo");

    public static final URI hasRepositoryObjectTitle = vf.createURI(NAMESPACE, "hasRepositoryObjectTitle");

    public static final URI hasAbstract = vf.createURI(NAMESPACE, "hasAbstract");

    public static final URI cites = vf.createURI(NAMESPACE, "cites");

    public static final URI compiles = vf.createURI(NAMESPACE, "compiles");

    public static final URI contains = vf.createURI(NAMESPACE, "contains");

    public static final URI continues = vf.createURI(NAMESPACE, "continues");

    public static final URI documents = vf.createURI(NAMESPACE, "documents");

    public static final URI hasFirstOriginator = vf.createURI(NAMESPACE, "hasFirstOriginator");

    public static final URI hasMetadata = vf.createURI(NAMESPACE, "hasMetadata");

    public static final URI hasOriginator = vf.createURI(NAMESPACE, "hasOriginator");

    public static final URI hasOriginatorList = vf.createURI(NAMESPACE, "hasOriginatorList");

    public static final URI hasPart = vf.createURI(NAMESPACE, "hasPart");

    public static final URI hasRepositoryObjectID = vf.createURI(NAMESPACE, "hasRepositoryObjectID");

    public static final URI hasRepositoryObjectIDScheme = vf.createURI(NAMESPACE, "hasRepositoryObjectIDScheme");

    public static final URI hasRepositoryObjectRelationTo = vf.createURI(NAMESPACE, "hasRepositoryObjectRelationTo");

    public static final URI isCitedBy = vf.createURI(NAMESPACE, "isCitedBy");

    public static final URI isCompiledBy = vf.createURI(NAMESPACE, "isCompiledBy");

    public static final URI isContainedBy = vf.createURI(NAMESPACE, "isContainedBy");

    public static final URI isContinuedBy = vf.createURI(NAMESPACE, "isContinuedBy");

    public static final URI isCreatedOn = vf.createURI(NAMESPACE, "isCreatedOn");

    public static final URI isDocumentedBy = vf.createURI(NAMESPACE, "isDocumentedBy");

    public static final URI isIdenticalTo = vf.createURI(NAMESPACE, "isIdenticalTo");

    public static final URI isIssuedOn = vf.createURI(NAMESPACE, "isIssuedOn");

    public static final URI isMetadataFor = vf.createURI(NAMESPACE, "isMetadataFor");

    public static final URI isNewVersionOf = vf.createURI(NAMESPACE, "isNewVersionOf");

    public static final URI isOriginalFormOf = vf.createURI(NAMESPACE, "isOriginalFormOf");

    public static final URI isPartOf = vf.createURI(NAMESPACE, "isPartOf");

    public static final URI isPreviousVersionOf = vf.createURI(NAMESPACE, "isPreviousVersionOf");

    public static final URI isReferencedBy = vf.createURI(NAMESPACE, "isReferencedBy");

    public static final URI isReleasedOn = vf.createURI(NAMESPACE, "isReleasedOn");

    public static final URI isSupplementTo = vf.createURI(NAMESPACE, "isSupplementTo");

    public static final URI isSupplementedBy = vf.createURI(NAMESPACE, "isSupplementedBy");

    public static final URI isVariantFormOf = vf.createURI(NAMESPACE, "isVariantFormOf");

    public static final URI nextOriginator = vf.createURI(NAMESPACE, "nextOriginator");

    public static final URI originatesFrom = vf.createURI(NAMESPACE, "originatesFrom");

    public static final URI references = vf.createURI(NAMESPACE, "references");

    public static final URI rollifiedRepositoryObject = vf.createURI(NAMESPACE, "rollifiedRepositoryObject");

    public static final URI Repository = vf.createURI("http://schema.oceanlink.org/repository#", "Repository");

    public static final URI Originator = vf.createURI(NAMESPACE, "Originator");

    public static final URI OriginatorList = vf.createURI(NAMESPACE, "OriginatorList");

    public static final URI RepositoryObject = vf.createURI(NAMESPACE, "RepositoryObject");

    public static final URI RepositoryObjectID = vf.createURI(NAMESPACE, "RepositoryObjectID");

    public static final URI RepositoryObjectIDScheme = vf.createURI(NAMESPACE, "RepositoryObjectIDScheme");

    public static final URI Instant = vf.createURI("http://www.w3.org/2006/time#", "Instant");

}