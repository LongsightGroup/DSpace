package org.dspace.authority.config;

import org.dspace.authority.AuthorityValue;

import java.util.Map;

/**
 * User: lantian @ atmire . com
 * Date: 9/17/14
 * Time: 4:32 PM
 */
public class AuthorityTypeConfiguration {

    private Map choiceSelectFields;

    private AuthorityValue type;

    private String searchFieldType, sortFieldType;

    public String getSearchFieldType() {
        return searchFieldType;
    }

    public void setSearchFieldType(String searchFieldType) {
        this.searchFieldType = searchFieldType;
    }

    public String getSortFieldType() {
        return sortFieldType;
    }

    public void setSortFieldType(String sortFieldType) {
        this.sortFieldType = sortFieldType;
    }

    public void setChoiceSelectFields(Map choiceSelectFields) {
        this.choiceSelectFields = choiceSelectFields;
    }

    public Map getChoiceSelectFields() {
        return choiceSelectFields;
    }

    public void setType(AuthorityValue type) {
        this.type = type;
    }

    public AuthorityValue getType() {
        return type;
    }
}
