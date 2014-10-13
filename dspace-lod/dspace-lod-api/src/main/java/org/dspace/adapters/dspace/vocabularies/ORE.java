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
 * 
 * <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 * xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
 * xmlns:owl="http://www.w3.org/2002/07/owl#"
 * xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
 * xmlns:dc="http://purl.org/dc/elements/1.1/"
 * xmlns:dcterms="http://purl.org/dc/terms/"
 * xmlns:dctype="http://purl.org/dc/dcmitype/"
 * xmlns:rdfg="http://www.w3.org/2004/03/trix/rdfg-1/"
 * xmlns:ore="http://www.openarchives.org/ore/terms/"
 * xml:base="http://www.openarchives.org/ore/terms/">
 * 
 * 
 * <rdf:Description rdf:about=""> <dcterms:title>The OAI ORE terms vocabulary</dcterms:title>
 * <rdfs:label>The OAI ORE terms vocabulary</rdfs:label> <rdfs:comment>The set
 * of terms provided by the OAI ORE initiative</rdfs:comment>
 * <dcterms:publisher rdf:parseType="Resource"><rdfs:label>The Open Archives
 * Initiative ORE Project</rdfs:label></dcterms:publisher>
 * 
 * <rdfs:seeAlso rdf:resource="http://www.openarchives.org/ore/toc" />
 * </rdf:Description>
 * 
 */
public class ORE
{

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();

    public static final String NAMESPACE = "http://www.openarchives.org/ore/terms/";

    public static final URI NS = vf.createURI(NAMESPACE);

    /**
     * <rdfs:Class rdf:about="ResourceMap"> <rdfs:label>Resource Map</rdfs:label>
     * <rdfs:comment>A description of an Aggregation according to the OAI-ORE
     * data model. Resource Maps are serialised to a machine readable format
     * according to the implementation guidelines as a Resource Map Document.
     * The Resource Map MAY contain type information and MAY contain
     * relationship information.</rdfs:comment> <rdfs:subClassOf
     * rdf:resource="&rdfgns;Graph" /> <rdfs:isDefinedBy
     * rdf:resource="&orens;"/> </rdfs:Class>
     */
    public static final URI ResourceMap = vf
            .createURI(NAMESPACE, "ResourceMap");

    /**
     * <rdfs:Class rdf:about="Aggregation"> <rdfs:label>Aggregation</rdfs:label>
     * <rdfs:comment>A set of related resources (Aggregated Resources), grouped
     * together such that the set can be treated as a single resource. This is
     * the entity described within the ORE interoperability framework by a
     * Resource Map.</rdfs:comment> <rdfs:subClassOf
     * rdf:resource="&dctypens;Collection" /> <rdfs:isDefinedBy
     * rdf:resource="&orens;"/> </rdfs:Class>
     */
    public static final URI Aggregation = vf
            .createURI(NAMESPACE, "Aggregation");

    /**
     * <rdf:Property rdf:about="aggregates"> <rdfs:label>Aggregates</rdfs:label>
     * <rdfs:comment>Aggregations, by definition, aggregate resources. This
     * relationship between the Aggregation and its Aggregated Resources is thus
     * more specific than a simple part/whole relationship, as expressed by
     * dcterms:hasPart for example.</rdfs:comment> <rdfs:subPropertyOf
     * rdf:resource="&dctermsns;hasPart" /> <rdfs:domain
     * rdf:resource="Aggregation" /> <owl:inverseOf
     * rdf:resource="isAggregatedBy" /> <rdfs:isDefinedBy
     * rdf:resource="&orens;"/>
     * 
     * </rdf:Property>
     */
    public static final URI aggregates = vf.createURI(NAMESPACE, "aggregates");

    /**
     * <rdf:Property rdf:about="isAggregatedBy"> <rdfs:label>Aggregates</rdfs:label>
     * <rdfs:comment>The inverse relationship of ore:aggregates,
     * ore:isAggregatedBy asserts that an Aggregated Resource is aggregated by
     * an Aggregation.</rdfs:comment> <rdfs:subPropertyOf
     * rdf:resource="&dctermsns;isPartOf" /> <rdfs:range
     * rdf:resource="Aggregation" /> <owl:inverseOf rdf:resource="aggregates" />
     * 
     * <rdfs:isDefinedBy rdf:resource="&orens;"/> </rdf:Property>
     */
    public static final URI isAggregatedBy = vf.createURI(NAMESPACE,
            "isAggregatedBy");

    /**
     * <rdf:Property rdf:about="describes"> <rdfs:label>Describes</rdfs:label>
     * <rdfs:comment>Resource Maps describe Aggregations in varying levels of
     * detail. This relationship asserts that the subject (a Resource Map)
     * describes the object (an Aggregation).</rdfs:comment> <rdfs:domain
     * rdf:resource="ResourceMap" /> <rdfs:range rdf:resource="Aggregation" />
     * 
     * <owl:inverseOf rdf:resource="isDescribedBy" /> <rdfs:isDefinedBy
     * rdf:resource="&orens;"/> </rdf:Property>
     */
    public static final URI describes = vf.createURI(NAMESPACE, "describes");

    /**
     * <!-- <rdf:Property rdf:about="isDescribedBy"> <rdfs:label>Describes</rdfs:label>
     * <rdfs:comment>The inverse relationship of ore:describes, in this case the
     * object of the relationship is the Resource Map and the subject is the
     * Aggregation which it describes.</rdfs:comment> <rdfs:domain
     * rdf:resource="Aggregation" /> <rdfs:range rdf:resource="ResourceMap" />
     * <owl:inverseOf rdf:resource="describes" /> <rdfs:isDefinedBy
     * rdf:resource="&orens;"/> </rdf:Property> -->
     */

    public static final URI isDescribedBy = vf.createURI(NAMESPACE,
            "isDescribedBy");

    /**
     * <rdf:Property rdf:about="analogousTo"> <rdfs:label>Describes</rdfs:label>
     * <rdfs:comment>The subject of this relationship MUST be an Aggregation.
     * This Aggregation should be considered an expression within the ORE
     * context of the object of the relationship, as it is broadly equivalent to
     * the resource. For example, the Aggregation may consist of the resources
     * which, together, make up a journal article which has a DOI assigned to
     * it. The Aggregation is not the article to which the DOI was assigned, but
     * is a proxy for it in some manner.</rdfs:comment>
     * 
     * <rdfs:domain rdf:resource="Aggregation" /> <rdfs:isDefinedBy
     * rdf:resource="&orens;"/> </rdf:Property>
     */
    public static final URI analogousTo = vf
            .createURI(NAMESPACE, "analogousTo");

}
