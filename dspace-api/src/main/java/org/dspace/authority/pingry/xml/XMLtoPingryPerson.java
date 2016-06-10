/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.pingry.xml;

import org.apache.log4j.Logger;
import org.dspace.authority.orcid.xml.Converter;
import org.dspace.authority.pingry.model.PingryPerson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Dietz (Longsight)
 */
public class XMLtoPingryPerson extends Converter {

    private static Logger log = Logger.getLogger(XMLtoPingryPerson.class);

    protected String PINGRY_PERSON = "pingry-person";
    protected String CONSTITUENT_ID = "constituent-id";
    protected String FIRST_NAME = "first-name";
    protected String MIDDLE_NAME = "middle-name";
    protected String LAST_NAME = "last-name";
    protected String SUFFIX1 = "suffix-1";
    protected String SUFFIX2 = "suffix-2";
    protected String MAIDEN_NAME = "maiden-name";
    protected String NICKNAME = "nickname";
    protected String PRIMARY_EDUCATION_CLASS_OF_YEAR = "primary-education-class-of-year";

    public List<PingryPerson> convert(Document xml) {
        List<PingryPerson> result = new ArrayList<>();

        if(xml == null) {
            log.info("XML is null");
        }

        if(xml.getDocumentElement() == null) {
            log.info("XML.document is null");
        }

        xml.getDocumentElement().normalize();

        NodeList nodeList = xml.getElementsByTagName(PINGRY_PERSON);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                PingryPerson person = convertPingryPerson(node);
                result.add(person);
            }
        }

        return result;
    }

    private PingryPerson convertPingryPerson(Node node) {
        PingryPerson person = new PingryPerson();

        setConstituentID(node, person);
        setFirstName(node, person);
        setMiddleName(node, person);
        setLastName(node, person);
        setSuffix1(node, person);
        setSuffix2(node, person);
        setMaidenName(node, person);
        setNickname(node, person);
        setPrimaryEducationClassOfYear(node, person);

        return person;
    }

    protected void processError(Document xml)  {
        log.error("Error in XMLtoPingryPerson");
    }

    private String getElementValue(Node node, String field) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            return element.getElementsByTagName(field).item(0).getTextContent();
        } else {
            return null;
        }
    }

    private void setConstituentID(Node node, PingryPerson person) {
        person.setConstituentID(getElementValue(node, CONSTITUENT_ID));
    }

    private void setFirstName(Node node, PingryPerson person) {
        person.setFirstName(getElementValue(node, FIRST_NAME));
    }

    private void setMiddleName(Node node, PingryPerson person) {
        person.setMiddleName(getElementValue(node, MIDDLE_NAME));
    }

    private void setLastName(Node node, PingryPerson person) {
        person.setLastName(getElementValue(node, LAST_NAME));
    }

    private void setSuffix1(Node node, PingryPerson person) {
        person.setSuffix1(getElementValue(node, SUFFIX1));
    }

    private void setSuffix2(Node node, PingryPerson person) {
        person.setSuffix2(getElementValue(node, SUFFIX2));
    }

    private void setMaidenName(Node node, PingryPerson person) {
        person.setMaidenName(getElementValue(node, MAIDEN_NAME));
    }

    private void setNickname(Node node, PingryPerson person) {
        person.setNickname(getElementValue(node, NICKNAME));
    }

    private void setPrimaryEducationClassOfYear(Node node, PingryPerson person) {
        person.setPrimaryEducationClassOfYear(getElementValue(node, PRIMARY_EDUCATION_CLASS_OF_YEAR));
    }
}
