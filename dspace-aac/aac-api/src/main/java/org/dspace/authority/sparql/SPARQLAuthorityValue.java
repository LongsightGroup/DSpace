package org.dspace.authority.sparql;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.dspace.authority.AuthoritySource;
import org.dspace.authority.AuthorityTypes;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.AuthorityValueGenerator;
import org.dspace.utils.DSpace;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * User: mini @ atmire . com
 * Date: 3/18/15
 * Time: 1:33 PM
 */
public class SPARQLAuthorityValue extends AuthorityValue {

    private static final String TYPE = "sparql";

    public static final String SPARQLID = "meta_sparql_id";

    private String sparql_id;

    private boolean update; // used in setValues(Bio bio)

    public String getSparql_id() {
        return sparql_id;
    }

    public void setSparql_id(String sparql_id) {
        this.sparql_id = sparql_id;
    }

    @Override
    public AuthorityValue newInstance(String field) {
        if (StringUtils.isNotBlank(field)) {
            AuthorityTypes types = new DSpace().getServiceManager().getServiceByName("AuthorityTypes", AuthorityTypes.class);
            AuthoritySource source = types.getExternalSources().get(field);
            return source.queryAuthorityID(null);
        } else {
            SPARQLAuthorityValue sparqlAuthorityValue = new SPARQLAuthorityValue();
            sparqlAuthorityValue.setId(UUID.randomUUID().toString());
            sparqlAuthorityValue.updateLastModifiedDate();
            sparqlAuthorityValue.setCreationDate(new Date());
            return sparqlAuthorityValue;
        }
    }

    @Override
    public String getAuthorityType() {
        return TYPE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SPARQLAuthorityValue that = (SPARQLAuthorityValue) o;

        if (sparql_id != null ? !sparql_id.equals(that.sparql_id) : that.sparql_id != null) {
            return false;
        }

        return true;
    }

    @Override
    public SolrInputDocument getSolrInputDocument() {
        SolrInputDocument doc = super.getSolrInputDocument();
        if (StringUtils.isNotBlank(getSparql_id())) {
            doc.addField(SPARQLID, getSparql_id());
        }
        return doc;
    }

    @Override
    public int hashCode() {
        return sparql_id != null ? sparql_id.hashCode() : 0;
    }

    public boolean hasTheSameInformationAs(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.hasTheSameInformationAs(o)) {
            return false;
        }

        SPARQLAuthorityValue that = (SPARQLAuthorityValue) o;

        if (sparql_id != null ? !sparql_id.equals(that.sparql_id) : that.sparql_id != null) {
            return false;
        }

        return true;
    }


    @Override
    public Map<String, String> choiceSelectMap() {
        Map<String, String> map = super.choiceSelectMap();
        map.put(TYPE, getSparql_id());
        return map;
    }


    @Override
    public String generateString() {
        String generateString = AuthorityValueGenerator.GENERATE + getAuthorityType() + AuthorityValueGenerator.SPLIT;
        if (StringUtils.isNotBlank(getSparql_id())) {
            generateString += getSparql_id();
        }
        return generateString;
    }
}