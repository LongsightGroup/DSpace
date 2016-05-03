/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.pingry;

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
        PingryPerson pingryPerson = converter.convert(personDocument).get(0);
        return pingryPerson;
    }

    public List<PingryPerson> queryPerson(String name, int start, int rows) {
        String queryString = "/people?q=" + URLEncoder.encode(name);
        Document personDocument = restConnector.get(queryString);
        XMLtoPingryPerson converter = new XMLtoPingryPerson();
        return converter.convert(personDocument);
    }

    @Override
    public List<AuthorityValue> queryAuthorities(String text, int max) {
        List<PingryPerson> personList = queryPerson(text, 0, max);
        List<AuthorityValue> authorities = new ArrayList<AuthorityValue>();
        for (PingryPerson person : personList) {
            authorities.add(PingryPersonAuthorityValue.create(person));
        }
        return authorities;
    }

    @Override
    public AuthorityValue queryAuthorityID(String id) {
        PingryPerson pingryPerson = getPerson(id);
        return PingryPersonAuthorityValue.create(pingryPerson);
    }
}
