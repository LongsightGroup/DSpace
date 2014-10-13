/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.oceanlink;

import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.content.*;
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
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class OLCollectionAdapter extends AbstractDSpaceEventAdapter
{
    @Override
    protected void install(Context ctx, DSpaceObject subject) throws Exception {

        //this.create(subject);

        //this.modifyMetadata(ctx, subject);

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

        return null;
    }

    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {


    }


    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws Exception {


    }

    @Override
    protected void remove(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException, SQLException {


    }

    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Collection;
    }

}
