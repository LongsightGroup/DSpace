/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rdf.conversion;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.dspace.app.util.MetadataExposure;
import org.dspace.authority.model.*;
import org.dspace.content.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.rdf.RDFConfiguration;
import org.dspace.rdf.RDFUtil;
import org.dspace.rdf.vocabulary.SKOS;
import org.dspace.rdf.vocabulary.SKOSXL;
import org.dspace.services.ConfigurationService;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Mark Diggory
 */
public class AuthorityDSORelationsConverterPlugin
implements ConverterPlugin
{

    private static final Logger log = Logger.getLogger(AuthorityDSORelationsConverterPlugin.class);

    protected ConfigurationService configurationService;

    public AuthorityDSORelationsConverterPlugin()
    {

    }

    /**
     * @return A model containing the PREFIXES
     */
    protected Model getPrefixes(Context context) throws SQLException {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("skos","http://www.w3.org/2004/02/skos/core#");
        m.setNsPrefix("skosxl","http://www.w3.org/2008/05/skos-xl#");
        m.setNsPrefix("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        for(MetadataSchema schema : MetadataSchema.findAll(context))
        {
            m.setNsPrefix(schema.getName(), schema.getNamespace());
        }
        return m;
    }

    @Override
    public void setConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    @Override
    public Model convert(Context context, DSpaceObject dso)
            throws SQLException
    {

        switch(dso.getType())
        {
            case (Constants.SCHEME) :
            {
                return convertScheme(context, (Scheme) dso);
            }
            case (Constants.CONCEPT) :
            {
                return convertConcept(context, (Concept) dso);
            }
            case (Constants.TERM) :
            {
                return convertTerm(context, (Term) dso);
            }
        }
        return null;
    }

    public Model convertScheme(Context context, Scheme scheme)
            throws SQLException
    {
        Model m = ModelFactory.createDefaultModel();
        Model prefixes = this.getPrefixes(context);
        m.setNsPrefixes(prefixes);
        prefixes.close();

        String myId = generateURI(scheme);
        if (myId == null)
        {
            return null;
        }

        m.add(m.createResource(myId), RDF.type, SKOS.ConceptScheme);
        m.add(m.createResource(myId), DCTerms.created, m.createTypedLiteral(getCalendar(scheme.getCreated()), XSDDatatype.XSDdateTime));
        m.add(m.createResource(myId), DCTerms.modified, m.createTypedLiteral(getCalendar(scheme.getLastModified()), XSDDatatype.XSDdateTime));

        Model metamodel = convertMetadata(context, myId, scheme);
        if(metamodel != null)
            m.add(metamodel);

        //  ex:animalThesaurus rdf:type skos:ConceptScheme;
        //  dct:title "Simple animal thesaurus";
        //  dct:creator ex:antoineIsaac.

        if (m.isEmpty())
        {
            log.info("There were no public schemes.");
            m.close();
            return null;
        }
        return m;
    }

    /**
     *  @prefix skos:   <http://www.w3.org/2004/02/skos/core#> .
        @prefix skosxl: <http://www.w3.org/2008/05/skos-xl#> .
        @prefix :       <http://www.example.com/demo#> .
        @prefix rdf:    <http://www.w3.org/2000/01/rdf-schema#> .
        @prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .

        :concept234 rdf:type skos:Concept ;
          skosxl:prefLabel :label1 ;
          skosxl:prefLabel :label2 ;
          skosxl:prefLabel :label3 ;
          skosxl:altLabel  :label4 .

        :label1 rdf:type skosxl:Label ;
          :lastEdited "2011-02-05T10:21:00"^^xsd:dateTime ;
          skosxl:literalForm "Spirituosen"@de .

        :label2 rdf:type skosxl:Label ;
          :lastEdited "2011-02-05T10:28:00"^^xsd:dateTime ;
          :myCustomProperty 2.71828 ;
          skosxl:literalForm "spirits"@en-GB .

        :label3 rdf:type skosxl:Label ;
          :lastEdited "2011-02-05T10:34:00"^^xsd:dateTime ;
          skosxl:literalForm "liquor"@en-US .

        :label4 rdf:type skosxl:Label ;
          :lastEdited "2011-02-05T10:42:00"^^xsd:dateTime ;
          :myCustomProperty 3.1415 ;
          skosxl:literalForm "booze"@en-US .

     * @param context
     * @param concept
     * @return
     * @throws SQLException
     */
    public Model convertConcept(Context context, Concept concept)
            throws SQLException
    {

        Model m = ModelFactory.createDefaultModel();
        Model prefixes = this.getPrefixes(context);
        m.setNsPrefixes(prefixes);
        prefixes.close();

        String myId = generateURI(concept);
        if (myId == null)
        {
            return null;
        }

        //ex:mammals rdf:type skos:Concept;
        //  skos:inScheme ex:animalThesaurus
        Scheme scheme = concept.getScheme();
        if(scheme != null)
        {
            m.add(m.createResource(myId),
                 SKOS.inScheme,
                 m.createResource(generateURI(scheme)));
        }

        m.add(m.createResource(myId), DCTerms.created, m.createTypedLiteral(getCalendar(concept.getCreated()), XSDDatatype.XSDdateTime));
        m.add(m.createResource(myId), DCTerms.modified, m.createTypedLiteral(getCalendar(concept.getLastModified()), XSDDatatype.XSDdateTime));


        Model metamodel = convertMetadata(context, myId, concept);
        if(metamodel != null)
            m.add(metamodel);


        for (Concept2ConceptRole role : Concept2ConceptRole.findAll(context)) {
            try
            {
                Concept2Concept[] incomingRelations = Concept2Concept.findByParentAndRole(context, concept.getID(), role.getRelationID());
                if (incomingRelations == null) {
                    incomingRelations = new Concept2Concept[0];
                }

                Concept2Concept[] outgoingRelations = Concept2Concept.findByChildAndRole(context, concept.getID(), role.getRelationID());
                if (outgoingRelations == null) {
                    outgoingRelations = new Concept2Concept[0];
                }

                if (incomingRelations.length > 0 || outgoingRelations.length > 0) {

                    for(Concept2Concept incomingRelation : incomingRelations)
                    {
                        Concept outgoingConcept = Concept.find(context, incomingRelation.getOutgoingId());
                        m.add(
                                m.createResource(myId),
                                m.createProperty(generateProperty(role.getIncomingLabel())),
                                m.createResource(generateURI(outgoingConcept)));
                    }

                    for(Concept2Concept outgoingRelation : outgoingRelations)
                    {
                        Concept incomingConcept = Concept.find(context,outgoingRelation.getIncomingId());
                        m.add(
                                m.createResource(myId),
                                m.createProperty(generateProperty(role.getIncomingLabel())),
                                m.createResource(generateURI(incomingConcept)));
                    }
                }
            }
            catch (Exception e)
            {
                log.error(e.getMessage(),e);
            }
        }

        // add pref terms.
        for (Term term : concept.getPreferredTerms())
        {
            if (!RDFUtil.isPublicBoolean(context, term))
            {
                continue;
            }
            String id = RDFUtil.generateIdentifier(context, term);
            if (id == null)
            {
                continue;
            }

            m.add(m.createResource(myId), SKOSXL.prefLabel, m.createResource(id));
            m.add(m.createResource(myId), SKOS.prefLabel, term.getLiteralForm());
            convertTerm(context, term);

        }

        // add alt terms.
        for (Term term : concept.getAltTerms())
        {
            if (!RDFUtil.isPublicBoolean(context, term))
            {
                continue;
            }
            String id = RDFUtil.generateIdentifier(context, term);
            if (id == null)
            {
                continue;
            }

            m.add(m.createResource(myId), SKOSXL.altLabel, m.createResource(id));
            m.add(m.createResource(myId), SKOS.altLabel, term.getLiteralForm());
            convertTerm(context, term);
        }

        if (m.isEmpty())
        {
            m.close();
            return null;
        }
        return m;
    }



    public Model convertTerm(Context context, Term term)
            throws SQLException {
        Model m = ModelFactory.createDefaultModel();
        Model prefixes = this.getPrefixes(context);
        m.setNsPrefixes(prefixes);
        prefixes.close();

        String myId = generateURI(term);
        if (myId == null) {
            return null;
        }

        // :label4 rdf:type skosxl:Label ;
        // :lastEdited "2011-02-05T10:42:00"^^xsd:dateTime ;
        // :myCustomProperty 3.1415 ;
        // skosxl:literalForm "booze"@en-US .

        m.add(m.createResource(myId), RDF.type, SKOSXL.Label);

        Scheme scheme = null;
        Concept concept = null;

        if (term.getConcepts() != null && term.getConcepts().length < 0 && term.getConcepts()[0] != null)
        {
            concept = term.getConcepts()[0];
            scheme = concept.getScheme();
        }

        if(scheme != null)
        {
            m.add(m.createResource(myId),
                 SKOS.inScheme,
                 m.createResource(generateURI(scheme)));
        }

        m.add(m.createResource(myId),SKOSXL.literalForm, term.getLiteralForm());
        m.add(m.createResource(myId), DCTerms.created, m.createTypedLiteral(getCalendar(term.getCreated()), XSDDatatype.XSDdateTime));
        m.add(m.createResource(myId), DCTerms.modified, m.createTypedLiteral(getCalendar(term.getLastModified()), XSDDatatype.XSDdateTime));

        Model metamodel = convertMetadata(context, myId, term);
        if(metamodel != null)
            m.add(metamodel);

        if (m.isEmpty())
        {
            m.close();
            return null;
        }
        return m;
    }

    @Override
    public boolean supports(int type)
    {
        switch (type)
        {
            case (Constants.TERM) :
                return true;
            case (Constants.CONCEPT) :
                return true;
            case (Constants.SCHEME) :
                return true;
            default :
                return false;
        }
    }


    public String generateURI(Scheme scheme)
    {
        return RDFConfiguration.getDSpaceRDFModuleURI() + "/resource/scheme/uuid:" + scheme.getIdentifier();
    }

    public String generateURI(Concept concept)
    {
        return RDFConfiguration.getDSpaceRDFModuleURI() + "/resource/concept/uuid:" + concept.getIdentifier();
    }

    public String generateURI(Term term)
    {
        return RDFConfiguration.getDSpaceRDFModuleURI() + "/resource/term/uuid:" + term.getIdentifier();
    }

    private String generateProperty(String incomingLabel) {

        if("Broader".equals(incomingLabel))
            return SKOS.broader.getURI();

        if("Narrower".equals(incomingLabel))
            return SKOS.narrower.getURI();

        if("IsMember".equals(incomingLabel))
            return SKOS.memberList.getURI();

        if("HasMember".equals(incomingLabel))
            return SKOS.member.getURI();

        if("Equal".equals(incomingLabel))
            return SKOS.exactMatch.getURI();

        if("Related".equals(incomingLabel))
            return SKOS.related.getURI();

        return RDFConfiguration.getDSpaceRDFModuleURI() + "/" + incomingLabel;

    }


    public Model convertMetadata(Context ctx, String myId, DSpaceObject obj) throws SQLException {

        Model m = ModelFactory.createDefaultModel();
        Model prefixes = this.getPrefixes(ctx);
        m.setNsPrefixes(prefixes);

        prefixes.close();

        for(Metadatum metadatum : obj.getMetadata()) {
            if (!MetadataExposure.isHidden(ctx, metadatum.schema, metadatum.element, metadatum.qualifier)) {
                MetadataSchema schema = MetadataSchema.find(ctx, metadatum.schema);

                if (metadatum.value != null) {
                    m.add(
                            m.createResource(myId),
                            m.createProperty(schema.getNamespace(), metadatum.element),
                            metadatum.value.startsWith("http") ?
                                    m.createResource(metadatum.value) :
                                    m.createLiteral(metadatum.value));
                }
            }
        }

        if (m.isEmpty())
        {
            m.close();
            return null;
        }
        return m;
    }


    public String getCalendar(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return com.hp.hpl.jena.sparql.util.Utils.calendarToXSDDateTimeString(cal);

    }
}
