/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.indexer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.AuthorityValueFinder;
import org.dspace.authority.AuthorityValueGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authority.pingry.PingryIndexClient;
import org.dspace.authority.pingry.PingryPersonAuthorityValue;
import org.dspace.authority.pingry.PingrySource;
import org.dspace.authority.pingry.model.PingryPerson;
import org.dspace.content.Metadatum;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;

/**
 * DSpaceAuthorityIndexer is used in IndexClient, which is called by the AuthorityConsumer and the indexing-script.
 * <p/>
 * An instance of DSpaceAuthorityIndexer is bound to a list of items.
 * This can be one item or all items too depending on the init() method.
 * <p/>
 * DSpaceAuthorityIndexer lets you iterate over each metadata value
 * for each metadata field defined in dspace.cfg with 'authority.author.indexer.field'
 * for each item in the list.
 * <p/>
 * <p/>
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class DSpaceAuthorityIndexer implements AuthorityIndexerInterface, InitializingBean {

    private static final Logger log = Logger.getLogger(DSpaceAuthorityIndexer.class);

    private ItemIterator itemIterator;
    private Item currentItem;
    /**
     * The list of metadata fields which are to be indexed *
     */
    private List<String> metadataFields;
    private int currentFieldIndex;
    private int currentMetadataIndex;
    private boolean useCache;
    private Map<String, AuthorityValue> cache;
    private AuthorityValue nextValue;
    private Context context;
    private AuthorityValueFinder authorityValueFinder;

    private static int touchedItems = 0;

    @Autowired(required = true)
    protected ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        int counter = 1;
        String field;
        metadataFields = new ArrayList<String>();
        while ((field = configurationService.getProperty("authority.author.indexer.field." + counter)) != null) {
            metadataFields.add(field);
            counter++;
        }
    }


    public void init(Context context, Item item) {
        touchedItems = 0;

        ArrayList<Integer> itemList = new ArrayList<Integer>();
        itemList.add(item.getID());
        this.itemIterator = new ItemIterator(context, itemList);
        try {
            currentItem = this.itemIterator.next();
        } catch (SQLException e) {
            log.error("Error while retrieving an item in the metadata indexer");
        }
        initialize(context);
    }

    public void init(Context context) {
        init(context, false);
    }

    public void init(Context context, boolean useCache) {
        touchedItems = 0;

        try {
            this.itemIterator = Item.findAllOldestFirst(context);
            currentItem = this.itemIterator.next();
        } catch (SQLException e) {
            log.error("Error while retrieving all items in the metadata indexer");
        }
        initialize(context);
        this.useCache = useCache;
    }

    private void initialize(Context context) {
        this.context = context;
        this.authorityValueFinder = new AuthorityValueFinder();

        currentFieldIndex = 0;
        currentMetadataIndex = 0;
        useCache = false;
        cache = new HashMap<>();
    }

    public AuthorityValue nextValue() {
        return nextValue;
    }


    public boolean hasMore() {
        //recursive
        //if i have data for you give it, or give next metadata field, or go to next item

        if (currentItem == null) {
            log.info("No item, ending");
            return false;
        }

        // 1. iterate over the metadata values
        String metadataField = metadataFields.get(currentFieldIndex);
        Metadatum[] values = currentItem.getMetadataByMetadataString(metadataField);
        if (currentMetadataIndex < values.length) {
            prepareNextValue(metadataField, values[currentMetadataIndex]);
            currentMetadataIndex++;
            return true;
        } else {
            // 2. iterate over the metadata fields
            if ((currentFieldIndex + 1) < metadataFields.size()) {
                currentFieldIndex++;
                //Reset our current metadata index since we are moving to another field
                currentMetadataIndex = 0;
                return hasMore();
            } else {
                // 3. iterate over the items
                try {
                    if (itemIterator.hasNext()) {
                        currentItem = itemIterator.next();
                        touchedItems++;
                        log.info("Next Item: " + currentItem.getID() + " itemCount:" + touchedItems);
                        //Reset our current field index
                        currentFieldIndex = 0;
                        //Reset our current metadata index
                        currentMetadataIndex = 0;
                    } else {
                        log.info("Next Item: THERE ARE NO MORE ITEMS IN ITERATOR " + " itemCount:" + touchedItems);
                        currentItem = null;
                    }
                    return hasMore();
                } catch (SQLException e) {
                    currentItem = null;
                    log.error("Error while retrieving next item in the author indexer",e);
                    System.out.println("Error hasMore: " + e.getMessage());
                    return false;
                }
            }
        }
    }

    /**
     * This method looks at the authority of a metadata.
     * If the authority can be found in solr, that value is reused.
     * Otherwise a new authority value will be generated that will be indexed in solr.
     * If the authority starts with AuthorityValueGenerator.GENERATE, a specific type of AuthorityValue will be generated.
     * Depending on the type this may involve querying an external REST service
     *
     * @param metadataField Is one of the fields defined in dspace.cfg to be indexed.
     * @param value         Is one of the values of the given metadataField in one of the items being indexed.
     */
    private void prepareNextValue(String metadataField, Metadatum value) {

        nextValue = null;

        String content = value.value;
        String authorityKey = value.authority;

        //We only want to update our item IF our UUID is not present or if we need to generate one.
        boolean requiresItemUpdate = StringUtils.isBlank(authorityKey) || StringUtils.startsWith(authorityKey, AuthorityValueGenerator.GENERATE);

        log.info("prepareNextValue(" + content + ", " + authorityKey + ") -- " + metadataField + " requiresItemUpdate: " + requiresItemUpdate);

        if (StringUtils.isNotBlank(authorityKey) && !authorityKey.startsWith(AuthorityValueGenerator.GENERATE)) {
            // !uid.startsWith(AuthorityValueGenerator.GENERATE) is not strictly necessary here but it prevents exceptions in solr
            // "fffd4b8d-f7cd-4120-a3aa-b5e4ed3edfcd"
            nextValue = authorityValueFinder.findByUID(context, authorityKey);
            log.info("got value from UID(" + authorityKey + ")");
        }
        if (nextValue == null && StringUtils.isBlank(authorityKey) && useCache) {
            // A metadata without authority is being indexed
            // If there is an exact match in the cache, reuse it rather than adding a new one.
            AuthorityValue cachedAuthorityValue = cache.get(content);
            if (cachedAuthorityValue != null) {
                nextValue = cachedAuthorityValue;
            }
            log.info("got value from cache");
        }

        if(authorityKey.contains("UNKNOWN")) {
            //lookup from name
            log.debug("lookup person: " + content);
            PingryPerson pingryPerson = PingryIndexClient.lookupPingryPersonFromName(content);
            if(pingryPerson != null) {
                PingryPersonAuthorityValue pingryPersonAuthorityValue = PingryPersonAuthorityValue.create(pingryPerson);
                nextValue = pingryPersonAuthorityValue;
            } else {
                log.info("Person not found from name");
            }
        }

        //prepareNextValue(Balmir, Jordan, will be generated::pingry::20271480) -- dc.subject.teamMember requiresItemUpdate: true
        if(StringUtils.isNotBlank(authorityKey) && authorityKey.startsWith(AuthorityValueGenerator.GENERATE) && !authorityKey.contains("UNKNOWN") && nextValue == null) {
            //completing submission, where there is an authority record, "will be generated::pingry::810019"
            String[] split = StringUtils.split(authorityKey, AuthorityValueGenerator.SPLIT);
            String type = null, authorityID = null;
            if (split.length > 0) {
                type = split[1];
                if (split.length > 1) {
                    authorityID = split[2];
                }
            }

            if(StringUtils.isNotBlank(authorityID)) {
                PingrySource pingrySource = PingrySource.getPingrySource();
                PingryPerson pingryPerson = pingrySource.getPerson(authorityID);

                if(pingryPerson != null) {
                    PingryPersonAuthorityValue pingryPersonAuthorityValue = PingryPersonAuthorityValue.create(pingryPerson);
                    nextValue = pingryPersonAuthorityValue;
                }
            }
        }


        if (nextValue == null) {
            log.info("generate new for: " + authorityKey);
            nextValue = AuthorityValueGenerator.generate(context, authorityKey, content, "pingryperson");
            if(nextValue instanceof PingryPersonAuthorityValue) {
                log.debug("FirstName: " + ((PingryPersonAuthorityValue) nextValue).getFirstName());
            }
        }

        //Update the item
        if (nextValue != null && requiresItemUpdate) {
            if(StringUtils.isNotBlank(nextValue.getValue())) {
                nextValue.updateItem(currentItem, value);
                try {
                    //saves it
                    currentItem.update();
                } catch (Exception e) {
                    log.error("Error creating a metadatavalue's authority", e);
                }
            }
        }
        if (useCache) {
            cache.put(content, nextValue);
        }

        log.debug("nextValue just set:" + nextValue);
    }

    public void close() {
        log.info("closing indexer");
        if(itemIterator != null) {
            itemIterator.close();
            itemIterator = null;
        }
        cache.clear();
    }

    public boolean isConfiguredProperly() {
        boolean isConfiguredProperly = true;
        if(CollectionUtils.isEmpty(metadataFields)){
            log.warn("Authority indexer not properly configured, no metadata fields configured for indexing. Check the \"authority.author.indexer.field\" properties.");
            isConfiguredProperly = false;
        }
        return isConfiguredProperly;
    }
}
