/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.administrative;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.cocoon.servlet.multipart.PartOnDisk;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.app.itemimport.ItemImport;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.handle.HandleManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Utility methods to processes BatchImport actions. These methods are used
 * exclusively from the administrative flow scripts.
 *
 * @author Peter Dietz
 */

public class FlowBatchImportUtils {

    /**
     * Language Strings
     */
    private static final Message T_upload_successful = new Message("default", "xmlui.administrative.batchimport.flow.upload_successful");
    private static final Message T_upload_failed = new Message("default", "xmlui.administrative.batchimport.flow.upload_failed");
    private static final Message T_import_successful = new Message("default", "xmlui.administrative.batchimport.flow.import_successful");
    private static final Message T_import_failed = new Message("default", "xmlui.administrative.batchimport.flow.import_failed");
    private static final Message T_no_changes = new Message("default", "xmlui.administrative.batchimport.general.no_changes");
    private static final Message T_failed_no_collection = new Message("default", "xmlui.administrative.batchimport.flow.failed_no_collection");

    // Other variables
    private static Logger log = Logger.getLogger(FlowBatchImportUtils.class);

    public static FlowResult processBatchImport(Context context, Request request) throws SQLException, AuthorizeException, IOException, Exception {

        FlowResult result = new FlowResult();
        result.setContinue(false);

        String zipFile = (String) request.getSession().getAttribute("zip");

        if (zipFile != null) {
            // Commit the changes
            context.commit();
            request.getSession().removeAttribute("zipFile");

            log.debug(LogManager.getHeader(context, "batchimport", " items changed"));

            //TODO: I don't think this section actually does anything.
            if (true) {
                result.setContinue(true);
                result.setOutcome(true);
                result.setMessage(T_import_successful);
            } else {
                result.setContinue(false);
                result.setOutcome(false);
                result.setMessage(T_no_changes);
            }
        } else {
            result.setContinue(false);
            result.setOutcome(false);
            result.setMessage(T_import_failed);
            log.debug(LogManager.getHeader(context, "batchimport", "Changes cancelled"));
        }

        return result;
    }

    public static FlowResult processUploadZIP(Context context, Request request) throws SQLException, AuthorizeException, IOException, Exception {
        FlowResult result = new FlowResult();
        result.setContinue(false);

        Object object = null;

        if (request.get("file") != null) {
            object = request.get("file");
        }

        Part filePart = null;
        File file = null;

        if (object instanceof Part) {
            filePart = (Part) object;
            file = ((PartOnDisk) filePart).getFile();
        }

        if (filePart != null && filePart.getSize() > 0) {
            String name = filePart.getUploadName();

            while (name.indexOf('/') > -1) {
                name = name.substring(name.indexOf('/') + 1);
            }

            while (name.indexOf('\\') > -1) {
                name = name.substring(name.indexOf('\\') + 1);
            }

            // Process CSV without import
            ItemImport itemImport = new ItemImport();

            String collectionHandle = String.valueOf(request.get("collectionHandle"));
            if (StringUtils.isEmpty(collectionHandle) || !collectionHandle.contains("/")) {
                //fail
                log.error("UIBatchImport failed due to no collection.");
                result.setContinue(false);
                result.setOutcome(false);
                result.setMessage(T_failed_no_collection);
                return result;
            }

            Collection collection = (Collection) HandleManager.resolveToObject(context, collectionHandle);
            Collection[] collections = new Collection[1];
            collections[0] = collection;

            File tempWorkDir = new File(itemImport.getTempWorkDir());
            File mapFile = File.createTempFile("batch-", ".map", tempWorkDir);

            log.info("Attempt UIBatchImport to collection: " + collection.getName()
                                         + ", zip: " + file.getName()
                                         + ", map: "+ mapFile.getAbsolutePath());

            /*
             // equivalent command-line would be:
             import -a -e <email> -c <collection/handle> -s <parent-dir-of-zip> -z <filename-of-zip> -m <mapfile> --template

             -c,--collection <arg>   destination collection(s) Handle or database ID
             -e,--eperson <arg>      email of eperson doing importing
             -m,--mapfile <arg>      mapfile items in mapfile
             -n,--notify             if sending submissions through the workflow, send
                                     notification emails
             -p,--template           apply template
             -q,--quiet              don't display metadata

             -s,--source <arg>       source of items (directory)
             -t,--test               test run - do not actually import items
             -w,--workflow           send submission through collection's workflow
             -z,--zip <arg>          name of zip file

             //Control
                  -a,--add                add items to DSpace
                  -R,--resume             resume a failed import (add only)
             */

            String sourceBatchDir = ItemImport.unzip(file);

            itemImport.addItems(context, collections, sourceBatchDir, mapFile.getAbsolutePath(), true);

            // Success!
            // Set session and request attributes
            result.setContinue(true);
            result.setOutcome(true);
            result.setMessage(T_upload_successful);
            result.setCharacters(FileUtils.readFileToString(mapFile));

            log.info("Success! UIBatchImport to collection: " + collection.getName()
                    + ", zip: " + file.getName()
                    + ", map: "+ mapFile.getAbsolutePath());
        } else {
            //No ZIP File, or upload failed
            result.setContinue(false);
            result.setOutcome(false);
            result.setMessage(T_upload_failed);
        }

        return result;
    }
}
