/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.ctask.general;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Distributive;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Ensure that item has dc.rights = "Public Domain"
 *
 * @author Peter Dietz
 */
@Distributive
public class MetadataAddDCRightsPublicDomain extends AbstractCurationTask
{
    private static final Logger log = Logger.getLogger(MetadataAddDCRightsPublicDomain.class);

    int processedItems = 0;
    int changedItems = 0;
    int errors = 0;

    /**
     * Perform the curation task upon passed DSO
     *
     * @param dso the DSpace object
     * @throws java.io.IOException
     */
    @Override
    public int perform(DSpaceObject dso) throws IOException
    {
        processedItems=0;
        changedItems=0;
        errors=0;
        distribute(dso);
        setResult("Processed: " + processedItems + " and changed: " + changedItems + " errors: " + errors);
        return Curator.CURATE_SUCCESS;
    }
    
    @Override
    protected void performItem(Item item) throws SQLException, IOException {
        Metadatum[] values = item.getMetadataByMetadataString("dc.rights");
        boolean foundMatch = false;
        for (Metadatum value : values) {
            if (value.value.contentEquals("Public Domain")) {
                foundMatch = true;
            }
        }
        if (!foundMatch) {
            item.addMetadata("dc", "rights", null, null, "Public Domain");
            try {
                item.update();
                Curator.curationContext().commit();

                changedItems++;
            } catch (AuthorizeException authE) {
                log.error("caught exception: " + authE + " on item:" + item.getID());
                errors++;
            }
        }

        processedItems++;
    }
}
