package org.dspace.authority.sparql;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
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
    private String searchFieldType, sortFieldType;

    public String getRecordQuery() {
        return recordQuery;
    }

    public void setRecordQuery(String recordQuery) {
        this.recordQuery = recordQuery;
    }

    @Override
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
    public String getSearchFieldType() {
        return searchFieldType;
    }

    public void setSearchFieldType(String searchFieldType) {
        this.searchFieldType = searchFieldType;
    }

    @Override
    public String getSortFieldType() {
        return sortFieldType;
    }

    public void setSortFieldType(String sortFieldType) {
        this.sortFieldType = sortFieldType;
    }


    /**
     *
     * Generates a a list of AuthorityValues with only minimal data returned
     * primarily used for term completion and lookup.
     *
     * @param filter
     * @param max
     * @return
     */
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

            List<AuthorityValue> authorities = new ArrayList<AuthorityValue>();
            Query query_result = QueryFactory.create(query);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(endpointUrl, query_result);
            ResultSet results = qExe.execSelect();
            while(results.hasNext())
            {
                QuerySolution row=results.nextSolution();

                SPARQLAuthorityValue authorityValue = new SPARQLAuthorityValue();
                authorityValue.setId(UUID.randomUUID().toString());
                authorityValue.setCreationDate(new Date());
                if(row.getLiteral("value") != null)
                    authorityValue.setValue(row.getLiteral("value").getString());
                else
                    authorityValue.setValue(row.getLiteral("s").getString());
                String uri = row.getResource("s").getURI();
                authorityValue.setSparql_id(uri);

                authorities.add(authorityValue);
            }

            return authorities;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<AuthorityValue>();
    }

    /**
     * Generates a single complete AuthorityValue containing the full set of properties
     * for the primary resource returned in the sparql query.
     *
     * @param id
     * @return
     */
    @Override
    public AuthorityValue queryAuthorityID(String id) {

        try {
            Context context = new Context();
            Scheme scheme = Scheme.findByIdentifier(context, schemeId);
            String query = scheme.getMetadata("authority.source.querytemplateonerecord");

            if(query == null || query.length() <= 0) {
                query = getRecordQuery();
            }

            query = query.replace("AC_USER_INPUT", id);

            Query query_result = QueryFactory.create(query);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(endpointUrl, query_result);
            ResultSet results = qExe.execSelect();

            SPARQLAuthorityValue authorityValue = new SPARQLAuthorityValue();

            Model model = ModelFactory.createDefaultModel();
            model.setNsPrefixes(query_result.getPrefixMapping().getNsPrefixMap());

            Resource s = model.createResource(id);

            int count = 0;
            while (results.hasNext()) {
                QuerySolution row = results.next();
                Resource r = row.getResource("p");
                String element = r.getLocalName();
                String prefix = query_result.getPrefixMapping().getNsURIPrefix(r.getNameSpace());
                if (prefix == null) {
                    log.error("Namespace: " + r.getNameSpace() + " does not have a defined prefix. Make sure this is present in the query.");
                    return null;
                }
                if (count == 0 || element.equals("label") || element.equals("title")) {
                    authorityValue.setValue(row.getLiteral("o").toString());
                }

                model.add(s,model.createProperty(row.getResource("p").getURI()),row.get("o"));

                count++;
            }

            authorityValue.setModel(model);
            authorityValue.setSparql_id(query);


            return authorityValue;

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

}