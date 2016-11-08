/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.indexer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.dspace.authority.AuthorityValue;
import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.kernel.ServiceManager;
import org.dspace.utils.DSpace;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class AuthorityIndexClient {

    private static Logger log = Logger.getLogger(AuthorityIndexClient.class);

    public static void main(String[] args) throws Exception {
        // create an options object and populate it
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("c", "clean", false, "clean solr before reindexing");
        options.addOption("h", "help", false, "help");
        options.addOption("l", "limit", true, "optional limit to # to process");
        CommandLine line = parser.parse(options, args);

        //Populate our solr
        Context context = new ContextNoCaching();
        context.turnOffAuthorisationSystem();
        ServiceManager serviceManager = getServiceManager();

        AuthorityIndexingService indexingService = serviceManager.getServiceByName(AuthorityIndexingService.class.getName(),AuthorityIndexingService.class);
        List<AuthorityIndexerInterface> indexers = serviceManager.getServicesByType(AuthorityIndexerInterface.class);

        if(!isConfigurationValid(indexingService, indexers)){
            System.out.println("Cannot index authority values since the configuration isn't valid. Check dspace logs for more information.");
            return;
        }

        if(line.hasOption('c')) {
            log.info("Cleaning the old index");
            System.out.println("Cleaning the old index");
            indexingService.cleanIndex();
        } else {
            log.info("Not cleaning SOLR before reindex");
        }

        System.out.println("Retrieving and writing data");
        log.info("Retrieving all data");

        int count=0;
        int limit = 500;
        if(line.hasOption('l')) {
            if(isInteger(line.getOptionValue('l'))){
                limit = Integer.parseInt(line.getOptionValue('l'));
            }
        }


        int hit=0;
        int miss=0;

        //Get all our values from the input forms
        Map<String, AuthorityValue> toIndexValues = new HashMap<>();
        for (AuthorityIndexerInterface indexerInterface : indexers) {
            log.info("Initialize " + indexerInterface.getClass().getName());
            System.out.println("Initialize " + indexerInterface.getClass().getName());
            indexerInterface.init(context, true);


            System.out.println(". = authority value found, _ means no match");

            while (indexerInterface.hasMore() && count < limit) {
                AuthorityValue authorityValue = indexerInterface.nextValue();

                if(count % 100 == 0) {
                    System.out.println("");
                    System.out.print(count + " ");
                }

                if(authorityValue != null && StringUtils.isNotBlank(authorityValue.getValue())){
                    toIndexValues.put(authorityValue.getId(), authorityValue);
                    System.out.print(".");
                    hit++;
                } else {
                    System.out.print("_");
                    miss++;
                }
                count++;

            }
            //Close up
            indexerInterface.close();
        }

        System.out.println("");
        System.out.println("Count: " + count + " hit:" + hit + " miss:" + miss);


        log.info("Writing");
        System.out.println("Writing");
        int wrote=0;
        for(String id: toIndexValues.keySet()) {
            indexingService.indexContent(toIndexValues.get(id), true);
            context.commit();
            indexingService.commit();
            System.out.print("+");
            wrote++;
        }

        System.out.println("");
        System.out.println("Wrote: " + wrote);

        //In the end commit our server
        context.commit();
        indexingService.commit();
        context.abort();
        System.out.println("All done !");
        log.info("All done !");
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void indexItem(Context context, Item item){
        ServiceManager serviceManager = getServiceManager();

        AuthorityIndexingService indexingService = serviceManager.getServiceByName(AuthorityIndexingService.class.getName(),AuthorityIndexingService.class);
        List<AuthorityIndexerInterface> indexers = serviceManager.getServicesByType(AuthorityIndexerInterface.class);

        if(!isConfigurationValid(indexingService, indexers)){
            //Cannot index, configuration not valid
            return;
        }

        for (AuthorityIndexerInterface indexerInterface : indexers) {

            indexerInterface.init(context , item);
            while (indexerInterface.hasMore()) {
                AuthorityValue authorityValue = indexerInterface.nextValue();
                if(authorityValue != null)
                    indexingService.indexContent(authorityValue, true);
            }
            //Close up
            indexerInterface.close();
        }
        //Commit to our server
        indexingService.commit();
    }

    private static ServiceManager getServiceManager() {
        //Retrieve our service
        DSpace dspace = new DSpace();
        return dspace.getServiceManager();
    }

    private static class ContextNoCaching extends Context
    {

        public ContextNoCaching() throws SQLException {
            super();
        }

        @Override
        public void cache(Object o, int id) {
            //Do not cache any object
        }
    }

    private static boolean isConfigurationValid(AuthorityIndexingService indexingService, List<AuthorityIndexerInterface> indexers){
        if(!indexingService.isConfiguredProperly()){
            return false;
        }

        for (AuthorityIndexerInterface indexerInterface : indexers) {
            if(!indexerInterface.isConfiguredProperly()){
                return false;
            }
        }
        return true;
    }

}
