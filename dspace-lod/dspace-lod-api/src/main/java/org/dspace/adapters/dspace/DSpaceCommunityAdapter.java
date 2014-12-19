/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.dspace;


import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.adapters.dspace.vocabularies.DC;
import org.dspace.adapters.dspace.vocabularies.DCTERMS;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.content.Bitstream;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

/**
 * DSpace Adapter Provides conversion from DSpace objects to DSpace RDF model.
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class DSpaceCommunityAdapter extends AbstractDSpaceEventAdapter
{

    @Override
    protected void install(Context ctx, DSpaceObject subject) throws Exception {

        this.create(subject);

        this.modifyMetadata(ctx, subject);


        // Remove all preexisting hasCommunity references to clean orphans before adding
        this.getProvider().getConnection().remove(createResource((Community)subject),DS.hasCommunity,null,DS.Relationship);

        // create relationships for subCommunities
        for(Community community : ((Community)subject).getSubcommunities())
        {
            this.add(ctx,subject,community);
        }

        // create relationships for subCollections
        for (Collection coll : ((Community)subject).getCollections())
        {
            this.add(ctx,subject,coll);
        }

        // create  subCommunities
        for(Community community : ((Community)subject).getSubcommunities())
        {
            try {
                provider.handle(ctx, new Event(Event.INSTALL, Constants.COMMUNITY, community.getID(), "Install Community"));
                this.getProvider().getConnection().commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // create subCollections
        for (Collection coll : ((Community)subject).getCollections())
        {
            try {
                provider.handle(ctx, new Event(Event.INSTALL, Constants.COLLECTION, coll.getID(), "Install Collection"));
                this.getProvider().getConnection().commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected Resource create(DSpaceObject object) throws RepositoryException {
        Resource aggregation = createResource((Community)object);
        handleStatement(aggregation, RDF.TYPE, DS.Community);
        return aggregation;
    }
    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {
        Resource aggregation = createResource((Community)subject);

        // remove metadata with context DescriptiveMetadata
        this.getProvider().getConnection().remove(aggregation,null,null,DS.DescriptiveMetadata);

        // add metadata
        handleStatement(aggregation,
                DC.identifier_, valueFactory.createLiteral("hdl:" + subject.getHandle(), DCTERMS.URI),DS.DescriptiveMetadata);

        // give it a title
        handleStatement(aggregation, DC.title_, subject.getName(),DS.DescriptiveMetadata);

        Community comm = (Community) subject;

        String shortDesc = comm.getMetadata("short_description");
        if (shortDesc != null && !shortDesc.trim().equals(""))
        {
            shortDesc = cleanHTML(shortDesc);
            handleStatement(
                    aggregation, DCTERMS.abstract_, shortDesc,DS.DescriptiveMetadata);
        }

        String intro = comm.getMetadata("introductory_text");
        if (intro != null && !intro.trim().equals("")){
            intro = cleanHTML(intro);
            handleStatement(
                    aggregation, DCTERMS.abstract_, intro,DS.DescriptiveMetadata);



            /* =================================================================
            * Dig around for a Logo Image.
            * =================================================================
            */
            Bitstream logo = comm.getLogo();
            if (logo != null)
            {
                handleStatement(
                        aggregation, DS.logo, createResource(logo),DS.DescriptiveMetadata);
            }
        }

    }

    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws Exception {

        /* =================================================================
        * List all the community -subcommunities and commuinity-collections relationships
        * =================================================================
        */

        if(object instanceof Community)
        {

            //parent has child
            handleStatement(
                    createResource((Community)subject),
                    DS.hasCommunity,
                    createResource((Community)object),DS.Relationship);

            //child is part of parent
            handleStatement(
                    createResource((Community)object),
                    DS.isPartOfCommunity,
                    createResource((Community)subject),DS.Relationship);
        }
        else
        {

            //parent has child
            handleStatement(
                    createResource((Community)subject),
                    DS.hasCollection,
                    createResource((Collection)object),DS.Relationship);

            //child is part of parent
            handleStatement(
                    createResource((Collection)object),
                    DS.isPartOfCommunity,
                    createResource((Community)subject),DS.Relationship);
        }
    }


    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Community;
    }



}
