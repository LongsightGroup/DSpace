/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.sesame;

import org.dspace.adapters.RepositoryProvider;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.event.Event;

/**
 * Interface used for indexing dspaceobject into sesame
 *
 * @author Mark Diggory (markd at atmire dot com)
 * @author Mini Pillai (mini at atmire dot com)
 */
public interface SesameIndexingService {

    public RepositoryProvider[] getProviders();

    void handle(Context context, Event event) throws Exception;

    void indexContent(Context context, DSpaceObject dso,
                      boolean force) throws Exception;

    void unIndexContent(Context context, String handle) throws Exception;

    void createIndex(Context context) throws Exception;

    void updateIndex(Context context, boolean force);

    void cleanIndex(boolean force) throws Exception;

}
