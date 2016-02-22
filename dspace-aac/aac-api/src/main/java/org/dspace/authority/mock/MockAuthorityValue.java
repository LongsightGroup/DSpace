package org.dspace.authority.mock;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.AuthorityValueGenerator;
import org.dspace.utils.DSpace;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * User: mini @ atmire . com
 * Date: 3/19/15
 * Time: 11:50 AM
 */
public class MockAuthorityValue extends AuthorityValue {



    private static final String TYPE = "mock";

    public static final String MOCKID = "meta_mock_id";

    private String mock_id;

    private boolean update; // used in setValues(Bio bio)

    public String getMock_id() {
        return mock_id;
    }

    public void setMock_id(String mock_id) {
        this.mock_id = mock_id;
    }

    @Override
    public AuthorityValue newInstance(String info) {
        if (StringUtils.isNotBlank(info)) {
            MockSource source = new DSpace().getSingletonService(MockSource.class);
            return source.queryAuthorityID(info);
        } else {
            MockAuthorityValue sparqlAuthorityValue = new MockAuthorityValue();
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

        MockAuthorityValue that = (MockAuthorityValue) o;

        if (mock_id != null ? !mock_id.equals(that.mock_id) : that.mock_id != null) {
            return false;
        }

        return true;
    }

    @Override
    public SolrInputDocument getSolrInputDocument() {
        SolrInputDocument doc = super.getSolrInputDocument();
        if (StringUtils.isNotBlank(getMock_id())) {
            doc.addField(MOCKID, getMock_id());
        }
        return doc;
    }

    @Override
    public int hashCode() {
        return mock_id != null ? mock_id.hashCode() : 0;
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

        MockAuthorityValue that = (MockAuthorityValue) o;

        if (mock_id != null ? !mock_id.equals(that.mock_id) : that.mock_id != null) {
            return false;
        }

        return true;
    }


    @Override
    public Map<String, String> choiceSelectMap() {
        Map<String, String> map = super.choiceSelectMap();
        map.put(TYPE, getMock_id());
        return map;
    }


    @Override
    public String generateString() {
        String generateString = AuthorityValueGenerator.GENERATE + getAuthorityType() + AuthorityValueGenerator.SPLIT;
        if (StringUtils.isNotBlank(getMock_id())) {
            generateString += getMock_id();
        }
        return generateString;
    }
}
