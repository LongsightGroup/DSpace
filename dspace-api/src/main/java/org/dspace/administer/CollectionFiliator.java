/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.administer;

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.storage.rdbms.DatabaseManager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A command-line tool for changing collection parent community relationships. Takes object internal ID, or handle
 * 
 * @author rrodgers
 * @version $Revision$
 */

public class CollectionFiliator
{
    public static void main(String[] argv) throws Exception
    {
        // create an options object and populate it
        CommandLineParser parser = new PosixParser();

        Options options = new Options();

        options.addOption("c", "collection", true, "Collection Handle or internalID");
        options.addOption("o", "old", true, "Old/current parent community");
        options.addOption("n", "new", true, "New parent community");

        options.addOption("h", "help", false, "help");

        CommandLine line = parser.parse(options, argv);

        String collectionID = null;
        String oldCommunityID = null;
        String newCommunityID = null;

        if (line.hasOption('h')) {
            HelpFormatter myhelp = new HelpFormatter();
            myhelp.printHelp("CollectionFiliator\n", options);
            System.out.println("\nchange collection parent community: CollectionFiliator --collection 1234/20 --old 1234/1 --new 1234/2");
            System.exit(0);
        }

        if (line.hasOption('c')) {
            collectionID = line.getOptionValue('c');
        }

        if (line.hasOption('o')) {
            oldCommunityID = line.getOptionValue('o');
        }

        if(line.hasOption('n')) {
            newCommunityID = line.getOptionValue('n');
        }

        // now validate
        // must have a command set
        if (StringUtils.isEmpty(collectionID) || StringUtils.isEmpty(oldCommunityID) || StringUtils.isEmpty(newCommunityID)) {
            System.out.println("Error - must set collection, old, and new");
            System.exit(1);
        }

        CollectionFiliator filiator = new CollectionFiliator();
        Context c = new Context();

        c.setIgnoreAuthorization(true);

        try
        {
            // validate and resolve the parent and child IDs into commmunities
            Community oldCommunity = CommunityFiliator.resolveCommunity(c, oldCommunityID);
            Community newCommunity = CommunityFiliator.resolveCommunity(c, newCommunityID);
            Collection collection = resolveCollection(c, collectionID);

            if (oldCommunity == null)
            {
                System.out.println("Error, old community cannot be found: " + oldCommunityID);
                System.exit(1);
            }

            if (newCommunity == null)
            {
                System.out.println("Error, new community cannot be found: " + newCommunityID);
                System.exit(1);
            }

            if (collection == null)
            {
                System.out.println("Error, collection cannot be found: " + collectionID);
                System.exit(1);
            }

            filiator.changeParentCommunity(c, collection, oldCommunity, newCommunity);
        }
        catch (SQLException sqlE)
        {
            System.out.println("Error - SQL exception: " + sqlE.toString());
        }
        catch (AuthorizeException authE)
        {
            System.out.println("Error - Authorize exception: "
                    + authE.toString());
        }
        catch (IOException ioE)
        {
            System.out.println("Error - IO exception: " + ioE.toString());
        }
    }

    public void changeParentCommunity(Context context, Collection collection, Community oldCommunity, Community newCommunity)
            throws SQLException, AuthorizeException, IOException
    {
        if(collection.getParentObject().getID() != oldCommunity.getID()) {
            System.out.println("Old Community is not parent of collection, " + collection.getParentObject().getID() + " is parentID");
            System.exit(1);
        }

        for(Collection newCommunityCollection : newCommunity.getCollections()) {
            if(newCommunityCollection.getID() == collection.getID()) {
                System.out.println("Collection is already a child of new community.");
                System.exit(1);
            }
        }

        newCommunity.addCollection(collection);
        oldCommunity.removeCollection(collection);
        context.commit();

        System.out.println("Changed parent community for collection: " + collection.getID() + ". old:" + oldCommunity.getID() + ", new:" + newCommunity.getID());
    }

    public static Collection resolveCollection(Context c, String collectionID) throws SQLException {
        Collection collection = null;

        if (collectionID.indexOf('/') != -1)
        {
            // has a / must be a handle
            collection = (Collection) HandleManager.resolveToObject(c, collectionID);

            // ensure it's a collection
            if ((collection == null) || (collection.getType() != Constants.COLLECTION))
            {
                collection = null;
            }
        }
        else
        {
            collection = collection.find(c, Integer.parseInt(collectionID));
        }

        return collection;
    }
}
