/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.oceanlink;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.dspace.adapters.oceanlink.vocabularies.*;
import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.adapters.dspace.vocabularies.DC;
import org.dspace.adapters.dspace.vocabularies.DCTERMS;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.adapters.dspace.vocabularies.ISO639_1;
import org.dspace.content.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;


/**
 * OceanLink Item Adapter Provides conversion from DSpace Items to OceanLink RDF model.
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class OLItemAdapter extends AbstractDSpaceEventAdapter
{
    private static final Logger log = Logger.getLogger(OLItemAdapter.class);

    @Override
    protected void install(Context ctx, DSpaceObject subject) throws Exception {
       if(checkPermission(ctx,subject)) {
            this.create(subject);

            this.modifyMetadata(ctx, subject);

            //add relationships for item-bundle
            for(Bundle bundle : ((Item)subject).getBundles())
            {
                this.add(ctx,subject,bundle);
            }

            //create bundles
            for(Bundle bndl : ((Item)subject).getBundles())
            {
                try {
                    provider.handle(ctx, new Event(Event.INSTALL, Constants.BUNDLE, bndl.getID(), "Install Bundle"));
                    this.getProvider().getConnection().commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
       }
    }

    @Override
    protected Resource create(DSpaceObject object) throws RDFHandlerException, RepositoryException {
        Resource r = this.createResource((Item)object);
        handleStatement(r, RDF.TYPE, RepositoryObject.RepositoryObject);
        return r;
    }

    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {
        Resource aggregation = createResource((Item)subject);

        /*
            * =================================================================
            * REMOVE RDF with context as DS.DescriptiveMetadata
            * =================================================================
            */

        this.getProvider().getConnection().remove(aggregation,null,null,DS.DescriptiveMetadata);

        /*
        * =================================================================
        * ADDING RDF for metadata
        * =================================================================
        */

        Resource dsItem = this.create(subject);

        for (DCValue dc : ((Item)subject).getMetadata(Item.ANY, Item.ANY, Item.ANY,
                Item.ANY))
        {

            /**
             * if in dc and a recognizable real dc term, otherwise its in
             * our DS space... (maybe make a fake space for these)
             */
            if (dc.schema.equals("dc")
                    && (dc.element.equals("format") || dc.element
                    .equals("extent")))
            {
                // suppress these elements
            }
            else if (dc.schema.equals("dc")
                    && !dc.element.equals("creator"))
            {

                URI predicate = null;

                boolean isUrl = false;

                try
                {
                    URL url = new URL(dc.value);
                    isUrl = true;
                }
                catch (MalformedURLException e)
                {
                }
                /*
                * Ok, we want dc elements namespace for unqualified dc and
                * dcterms namespace for qualified, otherwise, if its
                * non-standard put it into DSpace Namespace for now.
                */
                if (dc.qualifier != null)
                {
                    predicate = DCTERMS.DSPACE_DC_2_DCTERMS.get(dc.element+ "." + dc.qualifier);

                    if (predicate == null)
                    {
                        /*
                        * This happens if people make up unqualified elements or
                        * non-standard qualifiers.
                        */
                        predicate = valueFactory.createURI(DS.NAMESPACE, dc.qualifier);
                    }
                } else if (predicate == null)
                {
                    predicate = DC.DSPACE_DC_2_DC.get(dc.element);
                }


                /** Finally output the statement */
                if (dc.qualifier != null) {
                    if (dc.schema.equals("dc")
                            && (dc.element.equals("description") && dc.qualifier
                            .equals("provenance")))
                    {
                        continue;
                        // suppress these elements
                    }
                }
                if(predicate != null && dc != null && dc.value != null){
                    if(isUrl)
                    {
                        handleStatement(dsItem, predicate, valueFactory.createLiteral(dc.value,DCTERMS.URI),DS.DescriptiveMetadata);
                    }
                    else if((predicate.equals(DC.language_) || predicate.equals(DCTERMS.language_)) && ISO639_1.getURIForString(dc.value) != null)
                    {
                        handleStatement(dsItem, predicate, ISO639_1.getURIForString(dc.value),DS.DescriptiveMetadata);
                    }
                    else
                    {

                        if (dc.schema.equals("dc")&& (dc.element.equals("title"))){
                            handleStatement(dsItem, RepositoryObject.hasRepositoryObjectTitle, dc.value, DS.DescriptiveMetadata);
                        }
                        else if ((dc.qualifier != null) && dc.schema.equals("dc")&& (dc.element.equals("description") && dc.qualifier.equals("abstract"))){
                            handleStatement(dsItem,  RepositoryObject.hasAbstract, dc.value, DS.DescriptiveMetadata);
                        }
                        else
                            handleStatement(dsItem, predicate, dc.value,DS.DescriptiveMetadata);
                    }
                }
                else if(dc == null){
                    log.error("Item ID" + subject.getID() + ":DC object is null");
                }
                else if(dc.value == null)
                {
                    log.error("Item ID" + subject.getID() + ":" + dc.schema + "." + dc.element + dc.qualifier + ":value is null");
                }
                else if(predicate == null)
                {
                    log.error("Item ID" + subject.getID() + ":" + dc.schema + "." + dc.element + dc.qualifier + ":matching predicate is null");
                }
            }
            else if (!dc.schema.equals("dc"))
            {

                String lookup = dc.element;

                if (dc.qualifier != null)
                    lookup += "." + dc.qualifier;

                MetadataSchema schema;
                try
                {
                    schema = MetadataSchema.find(ctx, dc.schema);

                    URI predicate = valueFactory.createURI(schema
                            .getNamespace(), lookup);

                    /** Finally output the statement */
                    handleStatement(dsItem, predicate, dc.value);
                }
                catch (SQLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }


        for (DCValue dc : ((Item)subject).getMetadata("dc", "contributor", "author", Item.ANY))
        {
            // Person Entity
            URI person = valueFactory.createURI(dsItem.stringValue() + "#person-" + DigestUtils.md5Hex(dc.value) );
            handleStatement(person, RDF.TYPE, Person.Person, DS.DescriptiveMetadata);
            handleStatement(dsItem, RepositoryObject.hasOriginator, person, DS.DescriptiveMetadata);

            // PersonName Entity
            URI personName = valueFactory.createURI(dsItem.stringValue() + "#personname-" + DigestUtils.md5Hex(dc.value) );
            handleStatement(personName, RDF.TYPE, PersonName.PersonName, DS.DescriptiveMetadata);
            handleStatement(personName, PersonName.fullNameAsString, dc.value, DS.DescriptiveMetadata);

            //PersonalInfoItem Entity
            URI personalInfoItem = valueFactory.createURI(dsItem.stringValue() + "#person-info-item-" + DigestUtils.md5Hex(dc.value) );
            handleStatement(personalInfoItem, RDF.TYPE, PersonalInfoItem.PersonalInfoItem, DS.DescriptiveMetadata);
            handleStatement(personalInfoItem, PersonalInfoItem.isPersonalInfoItemOf, person, DS.DescriptiveMetadata);
            handleStatement(personalInfoItem, PersonalInfoItem.hasPersonalInfoValue, personName, DS.DescriptiveMetadata);
            handleStatement(person, Person.hasPersonalInfoItem, personalInfoItem, DS.DescriptiveMetadata);

            //AgentRole Entity
            URI agentRole = valueFactory.createURI(dsItem.stringValue() + "#agent-role-" + DigestUtils.md5Hex(dc.value) );
            handleStatement(agentRole, RDF.TYPE, AgentRole.AgentRole, DS.DescriptiveMetadata);
            handleStatement(agentRole, AgentRole.isPerformedBy, person, DS.DescriptiveMetadata);
            handleStatement(agentRole, AgentRole.isAgentRoleIn, dsItem, DS.DescriptiveMetadata);
            handleStatement(agentRole, AgentRole.hasAgentRoleType, AgentRole.Author, DS.DescriptiveMetadata);
            handleStatement(person, Person.performsAgentRole, agentRole, DS.DescriptiveMetadata);

        }



    }
    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException, SQLException {

        Item item = (Item)subject;
        DSpaceObject site = Site.find(ctx, 0);

        // item is part of site
        handleStatement(
                createResource((Item)subject),
                RepositoryObject.belongsTo,
                createResource((Site)site),DS.Relationship);

        for(Bitstream bitstream : ((Bundle)object).getBitstreams())
        {
            /* =================================================================
            * List all the item - bitstream relationships
            * =================================================================
            */
            // item has bitstream
            handleStatement(
                    createResource(item),
                    RepositoryObject.contains,
                    createResource(item,bitstream),DS.Relationship);

            //bitstream has item
            handleStatement(
                    createResource(item, bitstream),
                    RepositoryObject.isContainedBy,
                    createResource(item), DS.Relationship);
        }

    }
    @Override
    protected void remove(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException, SQLException {

        Resource item = createResource((Item)subject);

        //item belogs to site

        DSpaceObject site = Site.find(ctx, 0);

        this.getProvider().getConnection().remove(createResource((Item)subject), RepositoryObject.belongsTo, createResource((Site)site),null);

        for(Bitstream bits : ((Bundle)object).getBitstreams())
        {

            Resource bitstream = createResource(bits);

            // Remove a item as subject
            this.getProvider().getConnection().remove(item, RepositoryObject.contains,bitstream,null);

            // Remove a item as object
            this.getProvider().getConnection().remove(bitstream, RepositoryObject.isContainedBy,item,null);
        }

    }

    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Item;
    }


}


