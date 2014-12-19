/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.oceanlink;

import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;

/**
 * OceanLink  Adapter Provides conversion from DSpace objects to OceanLink RDF model.
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class OLCommunityAdapter extends AbstractDSpaceEventAdapter
{

    @Override
    protected void install(Context ctx, DSpaceObject subject) throws Exception {

        //this.create(subject);

        //this.modifyMetadata(ctx, subject);


        // Remove all preexisting hasCommunity references to clean orphans before adding
        //this.getConnection().remove(createResource(subject), DS.hasCommunity,null,DS.Relationship);

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
        return null;

    }
    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {


    }

    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws Exception {


    }

    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Community;
    }

}
