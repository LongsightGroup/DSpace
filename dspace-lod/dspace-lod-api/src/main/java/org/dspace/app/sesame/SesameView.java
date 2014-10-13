/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.sesame;

import info.aduna.iteration.Iterations;
import org.dspace.adapters.AdapterSupport;
import org.dspace.content.DSpaceObject;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * A Sesame view provides an aggregate list types and relationshsips that should be returned.
 *
 * For Example, a DSpace Item View will include rdf statements for various aggregate relationshsips:
 * (Collection, Item, Bundle, Bitstream, BitstreamFormat)
 *
 * These do not need to be complete records for the data.
 *
 * User: mdiggory
 * Date: 1/6/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SesameView {

    DSpaceObject object;

    RepositoryConnection connection;

    public SesameView(DSpaceObject object, RepositoryConnection connection)
    {
         this.object = object;
         this.connection = connection;
    }

    public Model returnAsModel() throws RepositoryException {

        // This may be a bit long winded, a single SPARQL query could probibly be created that does
        // all the work in its entirety

        // First get results for Item
        RepositoryResult<Statement> statements = connection.getStatements(
                new AdapterSupport().createResource(object), null, null, true);

        Model model = Iterations.addAll(statements, new LinkedHashModel());

        //TODO Add In Other Resources (dependent on DSO and Model)

        return model;

    }
}
