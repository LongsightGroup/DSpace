/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.sesame;

import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.utils.DSpace;


/**
 * Class for updating search indices in discovery from content events.
 *
 * @author Mark Diggory (markd at atmire dot com)
 */
public class SesameEventConsumer implements Consumer {
    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(SesameEventConsumer.class);

    private SesameIndexingService indexer = null;

    public void initialize() throws Exception {

    }

    /**
     * Consume a content event -- just build the sets of objects to add (new) to
     * the index, update, and delete.
     *
     * @param ctx   DSpace context
     * @param event Content event
     */
    public void consume(Context ctx, Event event) throws Exception {
        if(indexer == null)
            indexer = new DSpace().getSingletonService(SesameIndexingService.class);

        if(indexer != null)
            indexer.handle(ctx,event);
    }

    /**
     * Process sets of objects to add, update, and delete in index. Correct for
     * interactions between the sets -- e.g. objects which were deleted do not
     * need to be added or updated, new objects don't also need an update, etc.
     */
    public void end(Context ctx) throws Exception {
        /*try{
            indexer.commit();
        }catch(Exception e)
        {
            indexer.rollback();
        }  */
    }

    public void finish(Context ctx) throws Exception {
        // No-op

    }

}
