/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.sesame;

import org.apache.log4j.Logger;
import org.dspace.adapters.RepositoryProvider;
import org.dspace.content.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: mdiggory
 * Date: 1/5/14
 * Time: 9:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class SesameIndexingServiceImpl implements SesameIndexingService {

    private static final Logger log = Logger.getLogger(SesameIndexingServiceImpl.class);

    public RepositoryProvider[] getProviders() {
        return providers;
    }

    @Autowired
    @Required
    public void setProviders(RepositoryProvider[] providers) {
        this.providers = providers;
    }

    private RepositoryProvider[] providers;

    @Override
    public void handle(Context context, Event event) throws Exception {

        for(RepositoryProvider provider : providers)
        {
            provider.handle(context, event);
        }
    }

    @Override
    public void indexContent(Context context, DSpaceObject dso, boolean force) throws Exception {

        for(RepositoryProvider provider : providers)
        {
            provider.handle(context, new Event(Event.INSTALL, dso.getType(), dso.getID(), "Index DSpaceObject"));
        }
    }

    @Override
    public void unIndexContent(Context context, String handle) throws SQLException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createIndex(Context context) throws Exception {
        for(RepositoryProvider provider : providers)
        {

            provider.handle(context, new Event(Event.INSTALL, Constants.SITE,  0, "Create Index"));
        }
    }

    @Override
    public void updateIndex(Context context, boolean force) {
        //To change body of implemented methods use File | Settings | File Templates.
        {
            try {
                ItemIterator items = null;
                try {
                    for (items = Item.findAllUnfiltered(context); items.hasNext();)
                    {
                        Item item = items.next();
                        indexContent(context, item, force);
                        item.decache();
                    }
                } finally {
                    if (items != null)
                    {
                        items.close();
                    }
                }

                Collection[] collections = Collection.findAll(context);
                for (Collection collection : collections)
                {
                    indexContent(context, collection, force);
                    context.removeCached(collection, collection.getID());

                }

                Community[] communities = Community.findAll(context);
                for (Community community : communities)
                {
                    indexContent(context, community, force);
                    context.removeCached(community, community.getID());
                }



            } catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void cleanIndex(boolean force) throws Exception {
        for(RepositoryProvider provider : providers)
        {
            provider.clean(force);
        }
    }

}
