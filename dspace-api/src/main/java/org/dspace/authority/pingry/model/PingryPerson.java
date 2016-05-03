/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.authority.pingry.model;

/**
 *
 * @author Peter Dietz (Longsight)
 */
public class PingryPerson {
    protected String constituentID;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String suffix1;
    protected String suffix2;
    protected String maidenName;
    protected String nickname;
    protected String primaryEducationClassOfYear;

    public PingryPerson() {
    }

    @Override
    public String toString() {
        return "PingryPerson{" +
                "constituentID='" + constituentID + '\'' +
                ", firstName=" + firstName +
                ", middleName=" + middleName +
                ", lastName=" + lastName +
                ", suffix1=" + suffix1 +
                ", suffix2=" + suffix2 +
                ", maidenName=" + maidenName +
                ", nickname=" + nickname +
                ", primaryEducationClassOfYear=" + primaryEducationClassOfYear +
                '}';
    }

    public String getConstituentID() {
        return constituentID;
    }

    public void setConstituentID(String constituentID) {
        this.constituentID = constituentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
