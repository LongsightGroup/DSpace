package org.dspace.authority.sparql;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import org.apache.log4j.Logger;
import org.dspace.authority.AuthoritySource;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.model.Scheme;
import org.dspace.content.authority.Choices;
import org.dspace.core.Context;

import java.util.*;

/**
 * User: mini @ atmire . com
 * Date: 2/17/15
 * Time: 1:09 PM
 */
public class SPARQLSource implements AuthoritySource {

    // log4j category
    private static final Logger log = Logger.getLogger(SPARQLSource.class);

    // contact URL from configuration
    private static String endpointUrl = null;
    private String termCompletionQuery;
    private String schemeId;
    private String recordQuery;

    public String getRecordQuery() {
        return recordQuery;
    }

    public void setRecordQuery(String recordQuery) {
        this.recordQuery = recordQuery;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public void setEndpointUrl(String endpoint_url) {
        this.endpointUrl = endpoint_url;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setTermCompletionQuery(String termCompletionQuery) {
        this.termCompletionQuery = termCompletionQuery;
    }

    public String getTermCompletionQuery() {
        return termCompletionQuery;
    }

    @Override
    public List<AuthorityValue> queryAuthorities(String filter, int max) {
        try {
            Context context = new Context();
            Scheme scheme = Scheme.findByIdentifier(context, schemeId);
            String query = scheme.getMetadata("authority.source.querytemplate");

            if(query == null || query.length() <= 0) {
                query = getTermCompletionQuery();
            }

            filter = filter.substring(0,1).toUpperCase()+filter.substring(1,filter.length());
            query = query.replace("AC_USER_INPUT", filter);

            return queryEndPoint(endpointUrl, query);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<AuthorityValue>();
    }

    @Override
    public AuthorityValue queryAuthorityID(String id) {

        try {
            Context context = new Context();
            Scheme scheme = Scheme.findByIdentifier(context, schemeId);
            String query = scheme.getMetadata("authority.source.querytemplateonerecord");

            if(query == null || query.length() <= 0) {
                query = getRecordQuery();
            }

            return queryEndPointOneEntry(endpointUrl, query);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public Choices getBestMatch(String field, String text, int collection, String locale)
    {

        return new Choices(Choices.CF_NOTFOUND);
    }

    public String getLabel(String field, String key, String locale)
    {
        return null;
    }

    private List<AuthorityValue> queryEndPoint(String endpoint, String query) {

        List<AuthorityValue> authorities = new ArrayList<AuthorityValue>();
        Query query_result = QueryFactory.create(query);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint, query_result);
        ResultSet results = qExe.execSelect();
        while(results.hasNext())
        {
            QuerySolution row=results.nextSolution();
            AuthorityValue authorityValue = map(row);
            authorities.add(authorityValue);
        }
        return authorities;
    }

    private AuthorityValue queryEndPointOneEntry(String endpoint, String query) {

        Query query_result = QueryFactory.create(query);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint, query_result);
        ResultSet results = qExe.execSelect();
        SPARQLAuthorityValue authorityValue = map2(results, query_result.getPrefixMapping());
        authorityValue.setSparql_id(query);
        return authorityValue;
    }



    private AuthorityValue map(QuerySolution row) {
        SPARQLAuthorityValue authorityValue = new SPARQLAuthorityValue();
        authorityValue.setId(UUID.randomUUID().toString());
        authorityValue.setCreationDate(new Date());
        if(row.getLiteral("value") != null)
            authorityValue.setValue(row.getLiteral("value").getString());
        else
            authorityValue.setValue(row.getLiteral("s").getString());
        String uri = row.getResource("s").getURI();
        authorityValue.setSparql_id(uri);
        return authorityValue;

    }

    private SPARQLAuthorityValue map2(ResultSet rs, PrefixMapping prefixMap) {

        SPARQLAuthorityValue authorityValue = new SPARQLAuthorityValue();
        authorityValue.getOtherMetadata();

        int count = 0;
        while (rs.hasNext()) {
            QuerySolution row = rs.next();
            Resource r = row.getResource("p");
            String element = r.getLocalName();
            String prefix = prefixMap.getNsURIPrefix(r.getNameSpace());
            if (prefix == null) {
                log.error("Namespace: " + r.getNameSpace() + " does not have a defined prefix. Make sure this is present in the query.");
                return null;
            }
            if (count == 0 || element.equals("label")) {
                authorityValue.setValue(row.getLiteral("o").toString());
            }
            authorityValue.addOtherMetadata("meta_" + prefix + "_" + element, row.get("o").toString());
            count++;
        }

        return authorityValue;
    }

}