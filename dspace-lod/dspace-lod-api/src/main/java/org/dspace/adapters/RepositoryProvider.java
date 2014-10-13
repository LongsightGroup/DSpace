/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters;


import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public interface RepositoryProvider {

    void handle(Context context, Event event) throws Exception;

    void clean(boolean force) throws Exception;

    RepositoryConnection getConnection() throws RepositoryException;

}
