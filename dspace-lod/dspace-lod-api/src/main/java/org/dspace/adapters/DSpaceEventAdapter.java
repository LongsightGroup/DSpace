/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.xml.sax.SAXException;

import java.sql.SQLException;

public interface DSpaceEventAdapter
{

    /**
     * Generic RDF generation for DSpaceObject, should be overridden in upstream
     * implementations.
     *
     * @param event
     * @throws org.xml.sax.SAXException
     */
    public void handle(Context context, Event event) throws Exception, SQLException;

    boolean handlerFor(DSpaceObject dso);

    void setProvider(RepositoryProvider repositoryProvider);

    public void handleNamespaces() throws RepositoryException;

    public void setBaseUri(String baseUri);

    public String getBaseUri();
}