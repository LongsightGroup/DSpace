/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */


package org.dspace.adapters.dspace;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.adapters.dspace.vocabularies.DC;
import org.dspace.adapters.dspace.vocabularies.DCTERMS;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DCValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataSchema;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

/**
 * DSpace Adapter Provides conversion from DSpace objects to DSpace RDF model.
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class DSpaceItemAdapter extends AbstractDSpaceEventAdapter
{

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
        handleStatement(r, RDF.TYPE, DS.Item);
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
                if(isUrl)
                {
                    handleStatement(dsItem, predicate, valueFactory.createLiteral(dc.value,DCTERMS.URI),DS.DescriptiveMetadata);
                }
                else
                {
                    handleStatement(dsItem, predicate, dc.value,DS.DescriptiveMetadata);
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
                    handleStatement(dsItem, predicate, dc.value,DS.DescriptiveMetadata);
                }
                catch (SQLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }


    }
    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException {

        Item item = (Item)subject;

        for(Bitstream bitstream : ((Bundle)object).getBitstreams())
        {
            /* =================================================================
            * List all the item - bitstream relationships
            * =================================================================
            */
            // item has bitstream
            handleStatement(
                    createResource(item),
                    DS.hasBitstream,
                    createResource(item,bitstream),DS.Relationship);

            //bitstream has item
            handleStatement(
                    createResource(item, bitstream),
                    DS.isPartOfItem,
                    createResource(item), DS.Relationship);
        }

    }
    @Override
    protected void remove(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException {

        Resource item = createResource((Item)subject);
        for(Bitstream bits : ((Bundle)object).getBitstreams())
        {

            Resource bitstream = createResource(bits);

            // Remove a item as subject
            this.getProvider().getConnection().remove(item,DS.hasBitstream,bitstream,null);

            // Remove a item as object
            this.getProvider().getConnection().remove(bitstream,DS.isPartOfItem,item,null);
        }

    }


    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Item;
    }



}


