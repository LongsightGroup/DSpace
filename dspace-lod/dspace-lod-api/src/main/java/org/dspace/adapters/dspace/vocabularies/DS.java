/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.dspace.vocabularies;

import java.util.Hashtable;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class DS
{

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final String NAMESPACE = "http://purl.org/dspace/model#";
    
    public static final URI NS = vf.createURI(NAMESPACE);

    /** DSpace Resource Types */

    public static final URI Bitstream = vf.createURI(NAMESPACE, "Bitstream");
    
    public static final URI Bundle = vf.createURI(NAMESPACE, "Bundle");

    public static final URI Collection = vf.createURI(NAMESPACE, "Collection");
    
    public static final URI Community= vf.createURI(NAMESPACE, "Community");
    
    public static final URI EPerson = vf.createURI(NAMESPACE, "EPerson");
    
    public static final URI Group = vf.createURI(NAMESPACE, "Group");

    public static final URI Item = vf.createURI(NAMESPACE, "Item");

    public static final URI Object = vf.createURI(NAMESPACE, "DSpaceObject");
    
    public static final URI Policy = vf.createURI(NAMESPACE, "Policy");
    
    public static final URI Site = vf.createURI(NAMESPACE, "Site");

    public static final URI BitstreamFormat = vf.createURI(NAMESPACE, "BitstreamFormat");

    public static final URI DescriptiveMetadata = vf.createURI(NAMESPACE, "DescriptiveMetadata");

    public static final URI Relationship = vf.createURI(NAMESPACE, "Relationship");

    /** Structural Properties */

    public static final URI isPartOfSite = vf.createURI(NAMESPACE, "isPartOfSite");

    public static final URI isPartOfCommunity = vf.createURI(NAMESPACE, "isPartOfCommunity");

    public static final URI isPartOfCollection = vf.createURI(NAMESPACE, "isPartOfCollection");

    public static final URI isPartOfItem = vf.createURI(NAMESPACE, "isPartOfItem");
    
    public static final URI isPartOfBundle = vf.createURI(NAMESPACE, "isPartOfBundle");
    
    public static final URI hasCommunity = vf.createURI(NAMESPACE, "hasCommunity");

    public static final URI hasCollection = vf.createURI(NAMESPACE, "hasCollection");
    
    public static final URI hasItem = vf.createURI(NAMESPACE, "hasItem");
    
    public static final URI hasBundle = vf.createURI(NAMESPACE, "hasBundle");

    public static final URI hasBitstream = vf.createURI(NAMESPACE, "hasBitstream");
    
    public static final URI hasBitstreamFormat = vf.createURI(NAMESPACE, "hasBitstreamFormat");
    
    /** preoperties on Communities and Collections */
    
    public static final URI logo = vf.createURI(NAMESPACE, "logo");
    
    /** Bitstream format properties */

    public static final URI support = vf.createURI(NAMESPACE, "support");
    
    public static final URI fileExtension = vf.createURI(NAMESPACE, "fileExtension");

    public static final URI mimeType = vf.createURI(NAMESPACE, "mimeType");
    
    /** Bitstream properties */
    
    public static final URI messageDigest = vf.createURI(NAMESPACE, "messageDigest");
    
    public static final URI messageDigestAlgorithm = vf.createURI(NAMESPACE, "messageDigestAlgorithm");
    
    public static final URI messageDigestOriginator = vf.createURI(NAMESPACE, "messageDigestOriginator");
    
    public static final URI size = vf.createURI(NAMESPACE, "size");

    public static final URI bundle = vf.createURI(NAMESPACE, "bundle");
    
    /** EPerson properties */
    
    public static final URI language = vf.createURI(NAMESPACE, "language");
    
    
    /** Metadata Remappings */
    
    public static URI advisor = vf.createURI(NAMESPACE, "advisor");
    
    public static URI author = vf.createURI(NAMESPACE, "author");

    public static URI editor = vf.createURI(NAMESPACE, "editor");

    public static URI illustrator = vf.createURI(NAMESPACE, "illustrator");
    
    public static URI sponsorship = vf.createURI(NAMESPACE, "illustrator");
    
    public static URI citation = vf.createURI(NAMESPACE, "citation");
    
    public static URI govdoc = vf.createURI(NAMESPACE, "govdoc");
    
    public static URI isbn = vf.createURI(NAMESPACE, "isbn");
    
    public static URI issn = vf.createURI(NAMESPACE, "issn");
    
    public static URI sici = vf.createURI(NAMESPACE, "sici");
    
    public static URI ismn = vf.createURI(NAMESPACE, "ismn");
    		
    
    /** Mapping for DSpace metadata element/qualifiers to DS namespace */
    
    public static final Hashtable<String, URI> DSPACE_MAPPING = new Hashtable<String, URI>();
    
    static
    {
        DSPACE_MAPPING.put("contributor.advisor", advisor);
        DSPACE_MAPPING.put("contributor.author", author);
        DSPACE_MAPPING.put("contributor.editor", editor);
        DSPACE_MAPPING.put("contributor.illustrator", illustrator);
        DSPACE_MAPPING.put("contributor.other", DCTERMS.contributor_);
        
        DSPACE_MAPPING.put("identifier.citation", citation);
        DSPACE_MAPPING.put("identifier.govdoc", govdoc);
        DSPACE_MAPPING.put("identifier.isbn", isbn);
        DSPACE_MAPPING.put("identifier.issn", issn);
        DSPACE_MAPPING.put("identifier.sici", sici);
        DSPACE_MAPPING.put("identifier.ismn", ismn);
        DSPACE_MAPPING.put("identifier.other", DCTERMS.identifier_);
        DSPACE_MAPPING.put("identifier.uri", DCTERMS.identifier_);

        DSPACE_MAPPING.put("description.sponsorship", sponsorship);
        DSPACE_MAPPING.put("description.uri", DCTERMS.description_);
        DSPACE_MAPPING.put("format.mimetype", mimeType);
        
        DSPACE_MAPPING.put("language.iso", DCTERMS.language_);
        DSPACE_MAPPING.put("relation.uri", DCTERMS.relation_);
        DSPACE_MAPPING.put("rights.uri", DCTERMS.rights_);
        DSPACE_MAPPING.put("source.uri", DCTERMS.source_);
        DSPACE_MAPPING.put("subject.classification", DCTERMS.subject_);
        DSPACE_MAPPING.put("subject.ddc", DCTERMS.subject_);
        DSPACE_MAPPING.put("subject.lcc", DCTERMS.subject_);
        DSPACE_MAPPING.put("subject.lcsh", DCTERMS.subject_);
        DSPACE_MAPPING.put("subject.mesh", DCTERMS.subject_);
        DSPACE_MAPPING.put("subject.other", DCTERMS.subject_);
        DSPACE_MAPPING.put("subject.cip", DCTERMS.subject_);
    }

}

