/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.pingry;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.AuthorityValueGenerator;
import org.dspace.authority.pingry.model.PingryPerson;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Peter Dietz (Longsight)
 */
public class PingryPersonAuthorityValue extends AuthorityValue {
    private static Logger log = Logger.getLogger(PingryPersonAuthorityValue.class);

    private String constituentID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix1;
    private String suffix2;
    private String maidenName;
    private String nickname;
    private String primaryEducationClassOfYear;

    private boolean update; // used in setValues

    public PingryPersonAuthorityValue() {
    }

    public PingryPersonAuthorityValue(SolrDocument document) {
        super(document);
    }

    public String getName() {
        String name = "";
        if (StringUtils.isNotBlank(lastName)) {
            name = lastName;
            if (StringUtils.isNotBlank(firstName)) {
                name += ", ";
            }
        }
        if (StringUtils.isNotBlank(firstName)) {
            name += firstName;
        }
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            String[] split = name.split(",");
            if (split.length > 0) {
                setLastName(split[0].trim());
                if (split.length > 1) {
                    setFirstName(split[1].trim());
                }
            }
        }
        if (!StringUtils.equals(getValue(), name)) {
            setValue(name);
        }
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setName(value);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getConstituentID() {
        return constituentID;
    }

    public void setConstituentID(String constituentID) {
        this.constituentID = constituentID;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSuffix1() {
        return suffix1;
    }

    public void setSuffix1(String suffix1) {
        this.suffix1 = suffix1;
    }

    public String getSuffix2() {
        return suffix2;
    }

    public void setSuffix2(String suffix2) {
        this.suffix2 = suffix2;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPrimaryEducationClassOfYear() {
        return primaryEducationClassOfYear;
    }

    public void setPrimaryEducationClassOfYear(String primaryEducationClassOfYear) {
        this.primaryEducationClassOfYear = primaryEducationClassOfYear;
    }

    @Override
    public SolrInputDocument getSolrInputDocument() {
        SolrInputDocument doc = super.getSolrInputDocument();
        if(StringUtils.isNotBlank(getConstituentID())) {
            doc.addField("constituent_id", getConstituentID());
        }
        if (StringUtils.isNotBlank(getFirstName())) {
            doc.addField("first_name", getFirstName());
        }
        if(StringUtils.isNotBlank(getMiddleName())) {
            doc.addField("middle_name", getMiddleName());
        }
        if (StringUtils.isNotBlank(getLastName())) {
            doc.addField("last_name", getLastName());
        }
        if (StringUtils.isNotBlank(getSuffix1())) {
            doc.addField("suffix_1", getSuffix1());
        }
        if (StringUtils.isNotBlank(getSuffix2())) {
            doc.addField("suffix_2", getSuffix2());
        }
        if (StringUtils.isNotBlank(getMaidenName())) {
            doc.addField("maiden_name", getMaidenName());
        }
        if (StringUtils.isNotBlank(getNickname())) {
            doc.addField("nickname", getNickname());
        }
        if (StringUtils.isNotBlank(getPrimaryEducationClassOfYear())) {
            doc.addField("primary_education_class_of_year", getPrimaryEducationClassOfYear());
        }
        return doc;
    }

    public static PingryPersonAuthorityValue create() {
        PingryPersonAuthorityValue pingryAuthorityValue = new PingryPersonAuthorityValue();
        pingryAuthorityValue.setId(UUID.randomUUID().toString());
        pingryAuthorityValue.updateLastModifiedDate();
        pingryAuthorityValue.setCreationDate(new Date());
        return pingryAuthorityValue;
    }

    /**
     * Create an authority based on a given orcid bio
     */
    public static PingryPersonAuthorityValue create(PingryPerson person) {
        PingryPersonAuthorityValue personAuthorityValue = PingryPersonAuthorityValue.create();
        personAuthorityValue.setValues(person);
        return personAuthorityValue;
    }

    @Override
    public void setValues(SolrDocument document) {
        super.setValues(document);
        this.constituentID = ObjectUtils.toString(document.getFieldValue("constituent_id"));
        this.firstName = ObjectUtils.toString(document.getFieldValue("first_name"));
        this.middleName = ObjectUtils.toString(document.getFieldValue("middle_name"));
        this.lastName = ObjectUtils.toString(document.getFieldValue("last_name"));
        this.suffix1 = ObjectUtils.toString(document.getFieldValue("suffix_1"));
        this.suffix2 = ObjectUtils.toString(document.getFieldValue("suffix_2"));
        this.maidenName = ObjectUtils.toString(document.getFieldValue("maiden_name"));
        this.nickname = ObjectUtils.toString(document.getFieldValue("nickname"));
        this.primaryEducationClassOfYear = ObjectUtils.toString(document.getFieldValue("primary_education_class_of_year"));
    }

    public boolean setValues(PingryPerson person) {
        if (updateValue(person.getConstituentID(), getConstituentID())) {
            setConstituentID(person.getConstituentID());
        }

        if (updateValue(person.getFirstName(), getFirstName())) {
            setFirstName(person.getFirstName());
        }

        if (updateValue(person.getMiddleName(), getMiddleName())) {
            setMiddleName(person.getMiddleName());
        }

        if (updateValue(person.getLastName(), getLastName())) {
            setLastName(person.getLastName());
        }

        if (updateValue(person.getSuffix1(), getSuffix1())) {
            setSuffix1(person.getSuffix1());
        }

        if (updateValue(person.getSuffix2(), getSuffix2())) {
            setSuffix2(person.getSuffix2());
        }

        if (updateValue(person.getMaidenName(), getMaidenName())) {
            setMaidenName(person.getMaidenName());
        }

        if (updateValue(person.getNickname(), getNickname())) {
            setNickname(person.getNickname());
        }

        if (updateValue(person.getPrimaryEducationClassOfYear(), getPrimaryEducationClassOfYear())) {
            setPrimaryEducationClassOfYear(person.getPrimaryEducationClassOfYear());
        }

        setValue(getName());

        if (update) {
            update();
        }
        boolean result = update;
        update = false;
        return result;
    }

    private boolean updateValue(String incoming, String resident) {
        boolean update = StringUtils.isNotBlank(incoming) && !incoming.equals(resident);
        if (update) {
            this.update = true;
        }
        return update;
    }


    @Override
    public Map<String, String> choiceSelectMap() {
        Map<String, String> map = super.choiceSelectMap();

        if (StringUtils.isNotBlank(getConstituentID())) {
            map.put("constituent_id", getConstituentID());
        } else {
            map.put("constituent_id", "/");
        }

        if (StringUtils.isNotBlank(getFirstName())) {
            map.put("first-name", getFirstName());
        } else {
            map.put("first-name", "/");
        }

        if (StringUtils.isNotBlank(getMiddleName())) {
            map.put("middle_name", getMiddleName());
        } else {
            map.put("middle_name", "/");
        }

        if (StringUtils.isNotBlank(getLastName())) {
            map.put("last-name", getLastName());
        } else {
            map.put("last-name", "/");
        }

        if (StringUtils.isNotBlank(getSuffix1())) {
            map.put("suffix_1", getSuffix1());
        } else {
            map.put("suffix_1", "/");
        }

        if (StringUtils.isNotBlank(getSuffix2())) {
            map.put("suffix_2", getSuffix2());
        } else {
            map.put("suffix_2", "/");
        }

        if (StringUtils.isNotBlank(getMaidenName())) {
            map.put("maiden_name", getMaidenName());
        } else {
            map.put("maiden_name", "/");
        }

        if (StringUtils.isNotBlank(getNickname())) {
            map.put("nickname", getNickname());
        } else {
            map.put("nickname", "/");
        }

        if (StringUtils.isNotBlank(getPrimaryEducationClassOfYear())) {
            map.put("primary_education_class_of_year", getPrimaryEducationClassOfYear());
        } else {
            map.put("primary_education_class_of_year", "/");
        }

        //Extra?
        map.put("pingry", getConstituentID());

        return map;
    }

    @Override
    public String getAuthorityType() {
        return "pingry";
    }

    @Override
    public String generateString() {
        String generateString = AuthorityValueGenerator.GENERATE + getAuthorityType() + AuthorityValueGenerator.SPLIT;
        if (StringUtils.isNotBlank(getConstituentID())) {
            generateString += getConstituentID();
        }
        return generateString;
    }

    @Override
    public AuthorityValue newInstance(String info) {
        AuthorityValue authorityValue = null;
        if(StringUtils.isNotBlank(info)) {
            PingrySource pingrySource = PingrySource.getPingrySource();
            authorityValue = pingrySource.queryAuthorityID(info);
        } else {
            authorityValue = PingryPersonAuthorityValue.create();
        }
        return authorityValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PingryPersonAuthorityValue that = (PingryPersonAuthorityValue) o;

        if (constituentID != null ? !constituentID.equals(that.constituentID) : that.constituentID != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return constituentID != null ? constituentID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PingryPersonAuthorityValue{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                "} " + super.toString();
    }

    public boolean hasTheSameInformationAs(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if(!super.hasTheSameInformationAs(o)){
            return false;
        }

        PingryPersonAuthorityValue that = (PingryPersonAuthorityValue) o;

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) {
            return false;
        }
        //todo more checks

        return true;
    }
}
