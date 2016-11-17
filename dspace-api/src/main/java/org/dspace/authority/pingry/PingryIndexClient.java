/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.pingry;

import org.apache.commons.cli.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.AuthorityValueFinder;
import org.dspace.authority.AuthorityValueGenerator;
import org.dspace.authority.pingry.model.PingryPerson;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.dspace.content.Metadatum;

/**
 * Fix the pingry authority index.
 * 138;"Smith, Deborah";"";1;"will be generated::pingry::4642";600
 * Not in SOLR
 *
 * 142;"Ackerman, Charles W.";"";1;"a265c7f5-24be-4d0c-8443-31d46a3e85d1";600
 * SOLR  "id": "a265c7f5-24be-4d0c-8443-31d46a3e85d1",
         "field": "dc_subject_manager",
         "value": "Ackerman, Charles W.",
         "deleted": false,
         "creation_date": "2016-09-19T14:45:18.807Z",
         "last_modified_date": "2016-09-19T14:45:18.807Z",
         "authority_type": "pingry"
 *
 * 138;"Cooper, Aaron";"";1;"will be generated::pingry::20169523";600
 * Not in SOLR
 *
 * 141;"DeSanto, Meghan, 2003";"";1;"39a61050-f934-4193-b703-7c6cf8f53d4a";600
 * SOLR  "id": "39a61050-f934-4193-b703-7c6cf8f53d4a",
         "field": "dc_subject_captain",
         "value": "DeSanto, Meghan, 2003",
         "deleted": false,
         "creation_date": "2016-10-27T14:45:43.719Z",
         "last_modified_date": "2016-10-27T14:45:43.719Z",
         "authority_type": "pingry"
 *
 * The problem here is that SOLR no longer contains the remote identifier, the PingryID.
 * Solution is to ensure pingry schema requires pingryID to exist. Then roll through all entries, and query by name
 * the remote data provider and pray that there is one-and-only-one match for each.
 * As of 2016-10-28 there were 26020 authority entries in metadata. Solr has 25934
 *
 * db distinct text_value = 6115
 * db distinct authority = 25942
 */
public class PingryIndexClient {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(PingryIndexClient.class);

    private static PingrySource pingrySource = PingrySource.getPingrySource();

    protected PrintWriter print = null;

    private Context context;
    private List<String> selectedIDs;

    public PingryIndexClient(Context context) {
        print = new PrintWriter(System.out);
        this.context = context;
    }

    public static void main(String[] args) throws ParseException {

        Context c = null;
        try {
            c = new Context();

            PingryIndexClient PingryIndexClient = new PingryIndexClient(c);
            if (processArgs(args, PingryIndexClient) == 0) {
                System.exit(0);
            }
            PingryIndexClient.run();


            //Prep by nuking the old DB values for authority
            //BEGIN;
            //UPDATE metadatavalue set authority = 'will be generated::pingry::UNKNOWN' where metadatavalue.authority like '%-%';
            //COMMIT;


        } catch (SQLException e) {
            log.error("Error in UpdateAuthorities", e);
        } finally {
            if (c != null) {
                c.abort();
            }
        }

    }

    protected static int processArgs(String[] args, PingryIndexClient PingryIndexClient) throws ParseException {
        CommandLineParser parser = new PosixParser();
        Options options = createCommandLineOptions();
        CommandLine line = parser.parse(options, args);

        // help

        HelpFormatter helpFormatter = new HelpFormatter();
        if (line.hasOption("h")) {
            helpFormatter.printHelp("dsrun " + PingryIndexClient.class.getCanonicalName(), options);
            return 0;
        }

        // other arguments
        if (line.hasOption("i")) {
            PingryIndexClient.setSelectedIDs(line.getOptionValue("i"));
        }

        // print to std out
        PingryIndexClient.setPrint(new PrintWriter(System.out, true));

        return 1;
    }

    private void setSelectedIDs(String b) {
        this.selectedIDs = new ArrayList<String>();
        String[] orcids = b.split(",");
        for (String orcid : orcids) {
            this.selectedIDs.add(orcid.trim());
        }
    }

    protected static Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "help");
        options.addOption("i", "id", true, "Import and/or update specific solr records with the given ids (comma-separated)");
        return options;
    }


    public void run() {
        // This implementation could be very heavy on the REST service.
        // Use with care or make it more efficient.

        AuthorityValueFinder authorityValueFinder = new AuthorityValueFinder();
        List<AuthorityValue> authorities;

        if (selectedIDs != null && !selectedIDs.isEmpty()) {
            authorities = new ArrayList<AuthorityValue>();
            for (String selectedID : selectedIDs) {
                AuthorityValue byUID = authorityValueFinder.findByUID(context, selectedID);
                authorities.add(byUID);
            }
        } else {
            authorities = authorityValueFinder.findAll(context);
        }



        if (authorities != null) {
            print.println(authorities.size() + " authorities found.");
            int match = 0;
            int miss = 0;
            for (AuthorityValue authority : authorities) {

                print.println("AuthorityType: "  + authority.getAuthorityType() + " for id:" + authority.getId() + ", field:" + authority.getField() + ", value:" + authority.getValue());
                print.println(authority.toString());
                if(Objects.equals(authority.getAuthorityType(), "pingry")) {
                    lookupPingryPersonFromName(authority.getValue());
                    ///////
                }
            }

            print.println("Complete. match:" + match + " miss:" + miss);
        }
    }

    public static PingryPerson lookupPingryPersonFromName(String name) {
        //First try to use the bestMatch lookup
        PingryPerson person = pingrySource.getBestMatch(name);
        if(person != null) {
            return person;
        } else {
            //Fallback to open search
            //Search this solr authority record in the PPDB
            List<AuthorityValue> searchResults = pingrySource.queryAuthorities(name, 5);

            //TODO safety check, one-and-only-one match
            if (!searchResults.isEmpty()) {
                PingryPersonAuthorityValue pingrySearchAuthorityValue = (PingryPersonAuthorityValue) searchResults.get(0);
                log.debug("Search Result: constituentID: " + pingrySearchAuthorityValue.getConstituentID() + " name: " + pingrySearchAuthorityValue.getName());

                if (pingrySearchAuthorityValue.getName().equals(name)) {
                    String pingryID = pingrySearchAuthorityValue.getConstituentID();
                    log.debug("WE HAVE A MATCH!!! " + "UUID:" + pingrySearchAuthorityValue.getId() + " NAME: " + pingrySearchAuthorityValue.getName() + " PingryID: " + pingryID);

                    //Look this entry up by Pingry ID
                    PingryPerson pingryPerson = pingrySource.getPerson(pingryID);
                    log.debug(pingryPerson.toString());
                    return pingryPerson;

                } else {
                    log.info("Miss: " + name  + "difference is [" + StringUtils.difference(pingrySearchAuthorityValue.getName(), name) +"]");
                }
            }

            log.info("No search result");
            return null;
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static PingryPerson lookupPingryPersonFromPingryID(String id) {
        List<PingryPerson> peopleList = pingrySource.queryPerson(id, 0, 5);
        for(PingryPerson pingryPerson : peopleList) {
            if(ObjectUtils.equals(pingryPerson.getConstituentID(), id)) {
                log.info("lookup from PingryID matches: " + id);
                return pingryPerson;
            }
        }

        return null;
    }


  /*  protected void followUp(AuthorityValue authority) {
        print.println("Updated: " + authority.getValue() + " - " + authority.getId());

        boolean updateItems = ConfigurationManager.getBooleanProperty("solrauthority", "auto-update-items");
        if (updateItems) {
            updateItems(authority);
        }
    }*/

   /* protected void updateItems(AuthorityValue authority) {
        try {
            ItemIterator itemIterator = Item.findByMetadataFieldAuthority(context, authority.getField(), authority.getId());
            while (itemIterator.hasNext()) {
                Item next = itemIterator.next();
                List<Metadatum> metadata = next.getMetadata(authority.getField(), authority.getId());
                authority.updateItem(next, metadata.get(0)); //should be only one
                List<Metadatum> metadataAfter = next.getMetadata(authority.getField(), authority.getId());
                if (!metadata.get(0).value.equals(metadataAfter.get(0).value)) {
                    print.println("Updated item with handle " + next.getHandle());
                }
            }
        } catch (Exception e) {
            log.error("Error updating item", e);
            print.println("Error updating item. " + Arrays.toString(e.getStackTrace()));
        }
    }*/


    public PrintWriter getPrint() {
        return print;
    }

    public void setPrint(PrintWriter print) {
        this.print = print;
    }
}
