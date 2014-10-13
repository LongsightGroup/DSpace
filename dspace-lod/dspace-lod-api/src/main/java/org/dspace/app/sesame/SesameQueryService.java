/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.sesame;

import org.dspace.content.DSpaceObject;
import org.openrdf.repository.RepositoryException;

/**
 * Created with IntelliJ IDEA.
 * User: mdiggory
 * Date: 1/6/14
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SesameQueryService {

    /**
     * Basic API call to retrieve
     * @param object
     * @return
     */
    public SesameView getView(DSpaceObject object) throws RepositoryException;

}
