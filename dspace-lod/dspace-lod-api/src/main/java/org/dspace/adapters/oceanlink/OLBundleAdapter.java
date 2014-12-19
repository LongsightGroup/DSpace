/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.oceanlink;

import org.apache.log4j.Logger;
import org.dspace.adapters.oceanlink.vocabularies.RepositoryObject;
import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import java.sql.SQLException;


/**
 * OceanLink  Adapter Provides conversion from DSpace objects to OceanLink RDF model.
 *
 * @author Mini Pillai (mini at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class OLBundleAdapter extends AbstractDSpaceEventAdapter {

    private static Logger log = Logger.getLogger(OLBundleAdapter.class);

    @Override
    public void install(Context ctx, DSpaceObject subject) throws Exception {
        if(checkPermission(ctx,subject)) {

            //NOT HANDLING BUNDLE

            /*this.create(subject);

            this.modifyMetadata(ctx, subject);

            //create bundle-bitstream relation
            for(Bitstream bits : ((Bundle)subject).getBitstreams())
            {
                this.add(ctx,subject,bits);
            }  */

            // create bitstreams
            for(Bitstream bits : ((Bundle)subject).getBitstreams())
            {
                try {
                    provider.handle(ctx, new Event(Event.INSTALL, Constants.BITSTREAM, bits.getID(), "Install Bitstream"));
                    this.getProvider().getConnection().commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Resource create(DSpaceObject object) throws RDFHandlerException, RepositoryException {
        return null;
    }
    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {

        // May be wise to set bundle name on bitstreams here someday
    }
    @Override
    protected void add(Context ctx,DSpaceObject subject, DSpaceObject object) throws RepositoryException,SQLException,RDFHandlerException {

        // Here subject is bistream. We need relation between item-bitsream.
        Item item  = null;
        Bitstream bitstream = (Bitstream) object;
        try {

            item = (Item)bitstream.getParentObject();

        } catch (SQLException e) {
            e.printStackTrace();
        }


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
                createResource(item,bitstream),
                RepositoryObject.isPartOf,
                createResource(item),DS.Relationship);
    }


    @Override
    protected void remove(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException{

        Bundle bundle = (Bundle) subject;
        Item item = null;
        try {
            item = (Item) bundle.getParentObject();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bitstream bitstream = (Bitstream) object;

        // Remove a item as subject
        this.getProvider().getConnection().remove( createResource(item), RepositoryObject.contains,createResource(item, bitstream),null);

        // Remove a item as object
        this.getProvider().getConnection().remove(createResource(item, bitstream), RepositoryObject.isPartOf, createResource(item),null);

    }


    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Bundle;
    }


}
