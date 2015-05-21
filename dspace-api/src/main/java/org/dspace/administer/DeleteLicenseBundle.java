package org.dspace.administer;

import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.Context;

/**
 * Utility to delete all the license bundles and license bitstreams from DSpace Items.
 * Solves a single use-case where an institution wants to do away with DSpace system license
 */
public class DeleteLicenseBundle {


    public static void main(String[] argv) throws Exception {
        System.out.println("Deleting all LICENSE bundle's and license bitstreams");
        Context context = new Context();
        context.turnOffAuthorisationSystem();
        ItemIterator itemIterator = Item.findAll(context);

        int count = 0;
        while (itemIterator.hasNext())
        {
            Item item = itemIterator.next();
            item.removeDSpaceLicense();
            count++;
        }
        context.commit();
        System.out.println("Processed " + count + " items.");
        context.restoreAuthSystemState();
    }
}
