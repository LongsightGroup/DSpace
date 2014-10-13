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

public class DCTERMS
{

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();
    
    public static final String NAMESPACE = "http://purl.org/dc/terms/";

    public static final String NAMESPACE_LANG_EN = "http://id.loc.gov/vocabulary/iso639-1/en";

    public static final URI NS = vf.createURI(NAMESPACE);

    public static final URI DCMIType = vf.createURI(NAMESPACE, "DCMIType"); 
    public static final URI DDC = vf.createURI(NAMESPACE, "DDC"); 
    public static final URI IMT = vf.createURI(NAMESPACE, "IMT"); 
    public static final URI LCC = vf.createURI(NAMESPACE, "LCC"); 
    public static final URI LCSH = vf.createURI(NAMESPACE, "LCSH"); 
    public static final URI MESH = vf.createURI(NAMESPACE, "MESH"); 
    public static final URI NLM = vf.createURI(NAMESPACE, "NLM"); 
    public static final URI TGN = vf.createURI(NAMESPACE, "TGN"); 
    public static final URI UDC = vf.createURI(NAMESPACE, "UDC"); 
    
    public static final URI Box = vf.createURI(NAMESPACE, "Box"); 
    public static final URI ISO3166 = vf.createURI(NAMESPACE, "ISO3166"); 
    public static final URI ISO639_2 = vf.createURI(NAMESPACE, "ISO639-2"); 
    public static final URI ISO639_3 = vf.createURI(NAMESPACE, "ISO639-3"); 
    public static final URI Period = vf.createURI(NAMESPACE, "Period"); 
    public static final URI Point = vf.createURI(NAMESPACE, "Point"); 
    public static final URI RFC1766 = vf.createURI(NAMESPACE, "RFC1766"); 
    public static final URI RFC3066  = vf.createURI(NAMESPACE, "RFC3066"); 
    public static final URI RFC4646 = vf.createURI(NAMESPACE, "RFC4646"); 
    public static final URI URI = vf.createURI(NAMESPACE, "URI"); 
    public static final URI W3CDTF = vf.createURI(NAMESPACE, "W3CDTF"); 
    
    public static final URI abstract_ = vf.createURI(NAMESPACE, "abstract");

    public static final URI accessRights_ = vf.createURI(NAMESPACE, "accessRights");

    public static final URI accrualMethod_ = vf.createURI(NAMESPACE, "accrualMethod");

    public static final URI accrualPeriodicity_ = vf.createURI(NAMESPACE,"accrualPeriodicity");

    public static final URI accrualPolicy_ = vf.createURI(NAMESPACE, "accrualPolicy");

    public static final URI Agent = vf.createURI(NAMESPACE, "Agent");

    public static final URI AgentClass = vf.createURI(NAMESPACE, "AgentClass");

    public static final URI alternative_ = vf.createURI(NAMESPACE, "alternative");

    public static final URI audience_ = vf.createURI(NAMESPACE, "audience");

    public static final URI available_ = vf.createURI(NAMESPACE, "available");

    public static final URI bibliographicCitation = vf.createURI(NAMESPACE,"bibliographicCitation");

    public static final URI BibliographicResource = vf.createURI(NAMESPACE,"BibliographicResource");

    public static final URI conformsTo_ = vf.createURI(NAMESPACE, "conformsTo");

    public static final URI contributor_ = vf.createURI(NAMESPACE, "contributor");

    public static final URI coverage_ = vf.createURI(NAMESPACE, "coverage");

    public static final URI created_ = vf.createURI(NAMESPACE, "created");

    public static final URI creator_ = vf.createURI(NAMESPACE, "creator");

    public static final URI date_ = vf.createURI(NAMESPACE, "date");

    public static final URI dateAccepted_ = vf.createURI(NAMESPACE, "dateAccepted");

    public static final URI dateCopyrighted_ = vf.createURI(NAMESPACE,"dateCopyrighted");

    public static final URI dateSubmitted_ = vf.createURI(NAMESPACE, "dateSubmitted");

    public static final URI description_ = vf.createURI(NAMESPACE, "description");

    public static final URI educationLevel_ = vf.createURI(NAMESPACE, "educationLevel");

    public static final URI extent_ = vf.createURI(NAMESPACE, "extent");

    public static final URI FileFormat = vf.createURI(NAMESPACE, "FileFormat");

    public static final URI format_ = vf.createURI(NAMESPACE, "format");

    public static final URI Frequency = vf.createURI(NAMESPACE, "Frequency");

    public static final URI hasFormat_ = vf.createURI(NAMESPACE, "hasFormat");

    public static final URI hasPart_ = vf.createURI(NAMESPACE, "hasPart");

    public static final URI hasVersion_ = vf.createURI(NAMESPACE, "hasVersion");

    public static final URI identifier_ = vf.createURI(NAMESPACE, "identifier");

    public static final URI instructionalMethod_ = vf.createURI(NAMESPACE,"instructionalMethod");

    public static final URI isFormatOf_ = vf.createURI(NAMESPACE, "isFormatOf");

    public static final URI isPartOf_ = vf.createURI(NAMESPACE, "isPartOf");

    public static final URI isPartOfSeries_ = vf.createURI(NAMESPACE, "isPartOfSeries");

    public static final URI isReferencedBy_ = vf.createURI(NAMESPACE, "isReferencedBy");

    public static final URI isReplacedBy_ = vf.createURI(NAMESPACE, "isReplacedBy");

    public static final URI isRequiredBy_ = vf.createURI(NAMESPACE, "isRequiredBy");

    public static final URI issued_ = vf.createURI(NAMESPACE, "issued");

    public static final URI isVersionOf_ = vf.createURI(NAMESPACE, "isVersionOf");

    public static final URI Jurisdiction = vf.createURI(NAMESPACE, "Jurisdiction");

    public static final URI language_ = vf.createURI(NAMESPACE, "language");

    public static final URI license_ = vf.createURI(NAMESPACE, "license");

    public static final URI LicenseDocument = vf.createURI(NAMESPACE,"LicenseDocument");

    public static final URI LinguisticSystem = vf.createURI(NAMESPACE,"LinguisticSystem");

    public static final URI Location = vf.createURI(NAMESPACE, "Location");

    public static final URI LocationPeriodOrJurisdiction = vf.createURI(NAMESPACE,"LocationPeriodOrJurisdiction");

    public static final URI mediator_ = vf.createURI(NAMESPACE, "mediator");

    public static final URI MediaType = vf.createURI(NAMESPACE, "MediaType");

    public static final URI MediaTypeOrExtent = vf.createURI(NAMESPACE,"MediaTypeOrExtent");

    public static final URI medium_ = vf.createURI(NAMESPACE, "medium");

    public static final URI MethodOfAccrual = vf.createURI(NAMESPACE,"MethodOfAccrual");

    public static final URI MethodOfInstruction = vf.createURI(NAMESPACE,"MethodOfInstruction");

    public static final URI modified_ = vf.createURI(NAMESPACE, "modified");

    public static final URI PeriodOfTime = vf.createURI(NAMESPACE, "PeriodOfTime");

    public static final URI PhysicalMedium = vf.createURI(NAMESPACE, "PhysicalMedium");

    public static final URI PhysicalResource = vf.createURI(NAMESPACE,"PhysicalResource");

    public static final URI Policy = vf.createURI(NAMESPACE, "Policy");

    public static final URI provenance_ = vf.createURI(NAMESPACE, "provenance");

    public static final URI ProvenanceStatement = vf.createURI(NAMESPACE,"ProvenanceStatement");

    public static final URI publisher_ = vf.createURI(NAMESPACE, "publisher");

    public static final URI references_ = vf.createURI(NAMESPACE, "references");

    public static final URI relation_ = vf.createURI(NAMESPACE, "relation");

    public static final URI replaces_ = vf.createURI(NAMESPACE, "replaces");

    public static final URI requires_ = vf.createURI(NAMESPACE, "requires");

    public static final URI rights_ = vf.createURI(NAMESPACE, "rights");

    public static final URI rightsHolder_ = vf.createURI(NAMESPACE, "rightsHolder");

    public static final URI RightsStatement = vf.createURI(NAMESPACE,"RightsStatement");

    public static final URI SizeOrDuration = vf.createURI(NAMESPACE, "SizeOrDuration");

    public static final URI source_ = vf.createURI(NAMESPACE, "source");

    public static final URI spatial_ = vf.createURI(NAMESPACE, "spatial");

    public static final URI Standard = vf.createURI(NAMESPACE, "Standard");

    public static final URI subject_ = vf.createURI(NAMESPACE, "subject");

    public static final URI tableOfContents_ = vf.createURI(NAMESPACE,"tableOfContents");

    public static final URI temporal_ = vf.createURI(NAMESPACE, "temporal");

    public static final URI title_ = vf.createURI(NAMESPACE, "title");

    public static final URI type_ = vf.createURI(NAMESPACE, "type");

    public static final URI valid_ = vf.createURI(NAMESPACE, "valid");
    
    
    public static final Hashtable<String, URI> DSPACE_DC_2_DCTERMS = new Hashtable<String, URI>();
    
    static
    {
        DSPACE_DC_2_DCTERMS.put("contributor", DCTERMS.contributor_);
        DSPACE_DC_2_DCTERMS.put("contributor.advisor", DCTERMS.contributor_);
        DSPACE_DC_2_DCTERMS.put("contributor.author", DCTERMS.contributor_);
        DSPACE_DC_2_DCTERMS.put("contributor.editor", DCTERMS.contributor_);
        DSPACE_DC_2_DCTERMS.put("contributor.illustrator", DCTERMS.contributor_);
        DSPACE_DC_2_DCTERMS.put("contributor.other", DCTERMS.contributor_);
        DSPACE_DC_2_DCTERMS.put("coverage.spatial", DCTERMS.spatial_);
        DSPACE_DC_2_DCTERMS.put("coverage.temporal", DCTERMS.temporal_);
        DSPACE_DC_2_DCTERMS.put("creator", DCTERMS.creator_);
        DSPACE_DC_2_DCTERMS.put("date", DCTERMS.date_);
        DSPACE_DC_2_DCTERMS.put("date.accessioned", DCTERMS.dateAccepted_);
        DSPACE_DC_2_DCTERMS.put("date.available", DCTERMS.available_);
        DSPACE_DC_2_DCTERMS.put("date.copyright", DCTERMS.dateCopyrighted_);
        DSPACE_DC_2_DCTERMS.put("date.created", DCTERMS.created_);
        DSPACE_DC_2_DCTERMS.put("date.issued", DCTERMS.issued_);
        DSPACE_DC_2_DCTERMS.put("date.submitted", DCTERMS.dateSubmitted_);
        DSPACE_DC_2_DCTERMS.put("identifier", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.citation", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.govdoc", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.isbn", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.issn", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.sici", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.ismn", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.other", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("identifier.uri", DCTERMS.identifier_);
        DSPACE_DC_2_DCTERMS.put("description", DCTERMS.description_);
        DSPACE_DC_2_DCTERMS.put("description.abstract", DCTERMS.abstract_);
        DSPACE_DC_2_DCTERMS.put("description.provenance", DCTERMS.provenance_);
        DSPACE_DC_2_DCTERMS.put("description.sponsorship", DCTERMS.description_);
        DSPACE_DC_2_DCTERMS.put("description.statementofresponsibility", DCTERMS.rightsHolder_);
        DSPACE_DC_2_DCTERMS.put("description.tableofcontents", DCTERMS.tableOfContents_);
        DSPACE_DC_2_DCTERMS.put("description.uri", DCTERMS.description_);
        DSPACE_DC_2_DCTERMS.put("format", DCTERMS.format_);
        DSPACE_DC_2_DCTERMS.put("format.extent", DCTERMS.extent_);
        DSPACE_DC_2_DCTERMS.put("format.medium", DCTERMS.medium_);
        DSPACE_DC_2_DCTERMS.put("format.mimetype", DCTERMS.format_);
        DSPACE_DC_2_DCTERMS.put("language", DCTERMS.language_);
        DSPACE_DC_2_DCTERMS.put("language.iso", DCTERMS.language_);
        DSPACE_DC_2_DCTERMS.put("publisher", DCTERMS.publisher_);
        DSPACE_DC_2_DCTERMS.put("relation", DCTERMS.relation_);
        DSPACE_DC_2_DCTERMS.put("relation.isformatof", DCTERMS.isFormatOf_);
        DSPACE_DC_2_DCTERMS.put("relation.ispartof", DCTERMS.isPartOf_);
        DSPACE_DC_2_DCTERMS.put("relation.ispartofseries", DCTERMS.isPartOfSeries_);
        DSPACE_DC_2_DCTERMS.put("relation.haspart", DCTERMS.hasPart_);
        DSPACE_DC_2_DCTERMS.put("relation.isversionof", DCTERMS.isVersionOf_);
        DSPACE_DC_2_DCTERMS.put("relation.hasversion", DCTERMS.hasVersion_);
        DSPACE_DC_2_DCTERMS.put("relation.isbasedon", DCTERMS.relation_);
        DSPACE_DC_2_DCTERMS.put("relation.isreferencedby", DCTERMS.isReferencedBy_);
        DSPACE_DC_2_DCTERMS.put("relation.requires", DCTERMS.requires_);
        DSPACE_DC_2_DCTERMS.put("relation.replaces", DCTERMS.replaces_);
        DSPACE_DC_2_DCTERMS.put("relation.isreplacedby", DCTERMS.isReplacedBy_);
        DSPACE_DC_2_DCTERMS.put("relation.uri", DCTERMS.relation_);
        DSPACE_DC_2_DCTERMS.put("rights", DCTERMS.rights_);
        DSPACE_DC_2_DCTERMS.put("rights.uri", DCTERMS.rights_);
        DSPACE_DC_2_DCTERMS.put("source", DCTERMS.source_);
        DSPACE_DC_2_DCTERMS.put("source.uri", DCTERMS.source_);
        DSPACE_DC_2_DCTERMS.put("subject", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("subject.classification", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("subject.ddc", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("subject.lcc", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("subject.lcsh", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("subject.mesh", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("subject.other", DCTERMS.subject_);
        DSPACE_DC_2_DCTERMS.put("title", DCTERMS.title_);
        DSPACE_DC_2_DCTERMS.put("title.alternative", DCTERMS.alternative_);
        DSPACE_DC_2_DCTERMS.put("type", DCTERMS.type_);
        DSPACE_DC_2_DCTERMS.put("audience.educationlevel", DCTERMS.educationLevel_);
        DSPACE_DC_2_DCTERMS.put("subject.cip", DCTERMS.subject_);

    }

}