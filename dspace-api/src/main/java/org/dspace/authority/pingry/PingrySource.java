/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.pingry;

import org.apache.commons.lang.StringUtils;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.pingry.model.PingryPerson;
import org.dspace.authority.pingry.xml.XMLtoPingryPerson;
import org.dspace.authority.rest.RestSource;
import org.apache.log4j.Logger;
import org.dspace.utils.DSpace;
import org.w3c.dom.Document;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter Dietz (Longsight)
 */
public class PingrySource extends RestSource {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(PingrySource.class);

    private static PingrySource pingrySource;

    public static PingrySource getPingrySource() {
        if (pingrySource == null) {
            pingrySource = new DSpace().getServiceManager().getServiceByName("PingrySource", PingrySource.class);
        }
        return pingrySource;
    }

    private PingrySource(String url) {
        super(url);
    }

    public PingryPerson getPerson(String id) {
        String queryString = "/person?q=" + id;
        Document personDocument = restConnector.get(queryString);
        XMLtoPingryPerson converter = new XMLtoPingryPerson();
        return converter.convert(personDocument).get(0);
    }

    public List<PingryPerson> queryPerson(String name, int start, int rows) {
        String queryString = "/people?q=" + URLEncoder.encode(name);
        Document personDocument = restConnector.get(queryString);
        XMLtoPingryPerson converter = new XMLtoPingryPerson();
        return converter.convert(personDocument);
    }

    /**
     * Find a best match from an input string that could contain encoded information that could be search in PPDB
     *
     * Last (Maiden), First Middle, Suffix, (Nickname), Graduation
     * "Budd, Alexandra Ulrika, (Alex), 2006"
     * "Pounder, Lindsay Caroline, 2006"
     * "Simon, Scott, 2003"
     * "Cozin, Mark"
     *
     * @param name
     * @return
     */
    public PingryPerson getBestMatch(String name) {
        if(name.contains(",")) {
            String[] namePieces = name.split(",");
            String first = null;
            String last = null;
            String year = null;
            //0 = last, 1 = first middle, 2 = suffix, 3 = nickname, 4 = year
            //0 = last, 1 = first middle, 3 = year
            //0 = last, 1 = first
            if(namePieces.length > 1) {
                last = namePieces[0].trim();

                //Alexandra Ulrika
                //Lindsay Caroline
                //John Van R.
                String[] firstMiddlePieces = StringUtils.split(namePieces[1].trim());
                first = firstMiddlePieces[0];
            }

            if(namePieces.length > 2) {
                String possibleYear = namePieces[namePieces.length-1].trim();
                if(isInteger(possibleYear)) {
                    year = possibleYear;
                }
            }

            String queryString = "/people2?f=" + URLEncoder.encode(first) + "&l=" + URLEncoder.encode(last);
            if(StringUtils.isNotBlank(year)) {
                queryString += "&y=" + URLEncoder.encode(year);
            }
            Document personDocument = restConnector.get(queryString);
            XMLtoPingryPerson converter = new XMLtoPingryPerson();
            List<PingryPerson> peopleList = converter.convert(personDocument);
            if(peopleList.size() > 0) {
                return peopleList.get(0);
            } else {
                log.info("no hit");
            }


        } else {
            log.info("No commas");
        }

        return null;
    }

    @Override
    public List<AuthorityValue> queryAuthorities(String text, int max) {
        List<PingryPerson> personList = queryPerson(text, 0, max);
        List<AuthorityValue> authorities = new ArrayList<AuthorityValue>();
        for (PingryPerson person : personList) {
            authorities.add(PingryPersonAuthorityValue.create(person));

            if(authorities.size() >= max) {
                break;
            }
        }
        return authorities;
    }

    @Override
    public AuthorityValue queryAuthorityID(String id) {
        PingryPerson pingryPerson = getPerson(id);
        return PingryPersonAuthorityValue.create(pingryPerson);
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
