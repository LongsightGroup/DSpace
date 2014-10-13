/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.dspace;

import java.sql.SQLException;
import java.util.Date;

import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.adapters.dspace.vocabularies.DC;
import org.dspace.adapters.dspace.vocabularies.DCTERMS;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.content.*;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

/**
 * DSpace Adapter Provides conversion from DSpace objects to DSpace RDF model.
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class DSpaceCollectionAdapter extends AbstractDSpaceEventAdapter
{
    @Override
    protected void install(Context ctx, DSpaceObject subject) throws Exception {

        this.create(subject);

        this.modifyMetadata(ctx, subject);

        Collection collection = (Collection)subject;

        // create relationships for items
        try
        {
            ItemIterator iterator = collection.getItems();

            while(iterator.hasNext())
            {
                Item item = iterator.next();

                this.add(ctx,subject,item);

                item.decache();
            }
        }
        catch (SQLException e)
        {
            throw new RDFHandlerException(e.getMessage(),e);
        }

        // create items
        try
        {
            ItemIterator iter = collection.getItems();

            while(iter.hasNext())
            {
                Item item = iter.next();

                provider.handle(ctx, new Event(Event.INSTALL, Constants.ITEM, item.getID(), "Install Item"));
                this.getProvider().getConnection().commit();

                item.decache();
            }
        }
        catch (SQLException e)
        {
            throw new RDFHandlerException(e.getMessage(),e);
        }

    }
    @Override
    protected Resource create(DSpaceObject object) throws RepositoryException {
        Resource aggregation = createResource((Collection)object);
        handleStatement(aggregation, RDF.TYPE, DS.Collection);
        return aggregation;
    }

    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {

        Resource aggregation = createResource((Collection)subject);

        //remove metadata
        this.getProvider().getConnection().remove(aggregation,null,null,DS.DescriptiveMetadata);

        //add metadata
        Collection collection = (Collection) subject;

        handleStatement(aggregation, DC.identifier_,
                valueFactory.createLiteral("hdl:" + subject.getHandle(), DCTERMS.URI),DS.DescriptiveMetadata);

        // give it a title
        handleStatement(aggregation, DC.title_, subject.getName(),DS.DescriptiveMetadata);

        // describe type as resource map
        handleStatement(aggregation, DC.creator_,
                ConfigurationManager.getProperty("dspace.name"),DS.DescriptiveMetadata);

        String shortDesc = collection.getMetadata("short_description");
        if (shortDesc != null && !shortDesc.trim().equals(""))
        {
            shortDesc = cleanHTML(shortDesc);
            handleStatement(aggregation, DCTERMS.abstract_, shortDesc,DS.DescriptiveMetadata);
        }

        String intro = collection.getMetadata("introductory_text");
        if (intro != null && !intro.trim().equals("")){
            intro = cleanHTML(intro);
            handleStatement(aggregation, DCTERMS.abstract_, intro,DS.DescriptiveMetadata);
        }

        /* =================================================================
        * Dig around for a Logo Image.
        * =================================================================
        */
        Bitstream logo = collection.getLogo();
        if (logo != null)
        {
            handleStatement(aggregation, DS.logo, createResource(logo),DS.DescriptiveMetadata);
        }

        /* =================================================================
        * Time lastModified TODO: We need real timestamps for changes in db...
        * =================================================================
        */
        handleStatement(aggregation, DCTERMS.modified_, new Date());
    }


    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws Exception {

        /* =================================================================
        * List all the collection - items relationships
        * =================================================================
        */
        // collection has item
        handleStatement(
                createResource((Collection)subject),
                DS.hasItem,
                createResource((Item)object),DS.Relationship);

        //item has collection
        handleStatement(
                createResource((Item)object),
                DS.isPartOfCollection,
                createResource((Collection)subject),DS.Relationship);


    }



    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Collection;
    }


}
