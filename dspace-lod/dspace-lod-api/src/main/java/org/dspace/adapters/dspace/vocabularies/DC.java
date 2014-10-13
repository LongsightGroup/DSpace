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

public class DC
{

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();
    
    public static final String NAMESPACE = "http://purl.org/dc/elements/1.1/";

    public static final URI NS = vf.createURI(NAMESPACE);

    public static final URI contributor_ = vf.createURI(NAMESPACE, "contributor");

    public static final URI coverage_ = vf.createURI(NAMESPACE, "coverage");
    
    public static final URI creator_ = vf.createURI(NAMESPACE, "creator");

    public static final URI date_ = vf.createURI(NAMESPACE, "date");

    public static final URI description_ = vf.createURI(NAMESPACE, "description");

    public static final URI format_ = vf.createURI(NAMESPACE, "format");

    public static final URI identifier_ = vf.createURI(NAMESPACE, "identifier");

    public static final URI language_ = vf.createURI(NAMESPACE, "language");

    public static final URI publisher_ = vf.createURI(NAMESPACE, "publisher");
    
    public static final URI relation_ = vf.createURI(NAMESPACE, "relation");
    
    public static final URI rights_ = vf.createURI(NAMESPACE, "rights");

    public static final URI source_ = vf.createURI(NAMESPACE, "source");

    public static final URI subject_ = vf.createURI(NAMESPACE, "subject");

    public static final URI title_ = vf.createURI(NAMESPACE, "title");

    public static final URI type_ = vf.createURI(NAMESPACE, "type");
    
    
    public static final Hashtable<String, URI> DSPACE_DC_2_DC = new Hashtable<String, URI>();
    
    static
    {
        DSPACE_DC_2_DC.put("contributor", DC.contributor_);
        DSPACE_DC_2_DC.put("contributor.advisor", DC.contributor_);
        DSPACE_DC_2_DC.put("contributor.author", DC.contributor_);
        DSPACE_DC_2_DC.put("contributor.editor", DC.contributor_);
        DSPACE_DC_2_DC.put("contributor.illustrator", DC.contributor_);
        DSPACE_DC_2_DC.put("contributor.other", DC.contributor_);
        DSPACE_DC_2_DC.put("coverage.spatial", DC.coverage_);
        DSPACE_DC_2_DC.put("coverage.temporal", DC.coverage_);
        DSPACE_DC_2_DC.put("creator", DC.creator_);
        DSPACE_DC_2_DC.put("date", DC.date_);
        DSPACE_DC_2_DC.put("date.accessioned", DC.date_);
        DSPACE_DC_2_DC.put("date.available", DC.date_);
        DSPACE_DC_2_DC.put("date.copyright", DC.date_);
        DSPACE_DC_2_DC.put("date.created", DC.date_);
        DSPACE_DC_2_DC.put("date.issued", DC.date_);
        DSPACE_DC_2_DC.put("date.submitted", DC.date_);
        DSPACE_DC_2_DC.put("identifier", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.citation", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.govdoc", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.isbn", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.issn", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.sici", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.ismn", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.other", DC.identifier_);
        DSPACE_DC_2_DC.put("identifier.uri", DC.identifier_);
        DSPACE_DC_2_DC.put("description", DC.description_);
        DSPACE_DC_2_DC.put("description.abstract", DC.description_);
        DSPACE_DC_2_DC.put("description.provenance", DC.description_);
        DSPACE_DC_2_DC.put("description.sponsorship", DC.description_);
        DSPACE_DC_2_DC.put("description.statementofresponsibility", DC.description_);
        DSPACE_DC_2_DC.put("description.tableofcontents", DC.description_);
        DSPACE_DC_2_DC.put("description.uri", DC.description_);
        DSPACE_DC_2_DC.put("format", DC.format_);
        DSPACE_DC_2_DC.put("format.extent", DC.format_);
        DSPACE_DC_2_DC.put("format.medium", DC.format_);
        DSPACE_DC_2_DC.put("format.mimetype", DC.format_);
        DSPACE_DC_2_DC.put("language", DC.language_);
        DSPACE_DC_2_DC.put("language.iso", DC.language_);
        DSPACE_DC_2_DC.put("publisher", DC.publisher_);
        DSPACE_DC_2_DC.put("relation", DC.relation_);
        DSPACE_DC_2_DC.put("relation.isformatof", DC.relation_);
        DSPACE_DC_2_DC.put("relation.ispartof", DC.relation_);
        DSPACE_DC_2_DC.put("relation.ispartofseries", DC.relation_);
        DSPACE_DC_2_DC.put("relation.haspart", DC.relation_);
        DSPACE_DC_2_DC.put("relation.isversionof", DC.relation_);
        DSPACE_DC_2_DC.put("relation.hasversion", DC.relation_);
        DSPACE_DC_2_DC.put("relation.isbasedon", DC.relation_);
        DSPACE_DC_2_DC.put("relation.isreferencedby", DC.relation_);
        DSPACE_DC_2_DC.put("relation.requires", DC.relation_);
        DSPACE_DC_2_DC.put("relation.replaces", DC.relation_);
        DSPACE_DC_2_DC.put("relation.isreplacedby", DC.relation_);
        DSPACE_DC_2_DC.put("relation.uri", DC.relation_);
        DSPACE_DC_2_DC.put("rights", DC.rights_);
        DSPACE_DC_2_DC.put("rights.uri", DC.rights_);
        DSPACE_DC_2_DC.put("source", DC.source_);
        DSPACE_DC_2_DC.put("source.uri", DC.source_);
        DSPACE_DC_2_DC.put("subject", DC.subject_);
        DSPACE_DC_2_DC.put("subject.classification", DC.subject_);
        DSPACE_DC_2_DC.put("subject.ddc", DC.subject_);
        DSPACE_DC_2_DC.put("subject.lcc", DC.subject_);
        DSPACE_DC_2_DC.put("subject.lcsh", DC.subject_);
        DSPACE_DC_2_DC.put("subject.mesh", DC.subject_);
        DSPACE_DC_2_DC.put("subject.other", DC.subject_);
        DSPACE_DC_2_DC.put("title", DC.title_);
        DSPACE_DC_2_DC.put("title.alternative", DC.title_);
        DSPACE_DC_2_DC.put("type", DC.type_);
        
        // special cases for crazy DSpace@MIT mappings
        DSPACE_DC_2_DC.put("audience.educationlevel", DCTERMS.audience_);
        DSPACE_DC_2_DC.put("subject.cip", DC.subject_);

    }

}