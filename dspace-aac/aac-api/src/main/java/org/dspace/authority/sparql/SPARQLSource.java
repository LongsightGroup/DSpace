package org.dspace.authority.sparql;

import com.hp.hpl.jena.query.*;
import org.dspace.authority.AuthoritySource;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.model.AuthorityMetadataValue;
import org.dspace.authority.model.Scheme;
import org.dspace.content.authority.Choices;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User: mini @ atmire . com
 * Date: 2/17/15
 * Time: 1:09 PM
 */
public class SPARQLSource implements AuthoritySource {

    // contact URL from configuration
    private static String endpoint_url = null;

    private static String sparql_query = null;

    private String spring_query;
    private String sparql_query_one_record;


    public SPARQLSource(String url){
        this.endpoint_url = url;
    }

    public void setSpring_query(String spring_query) {
        this.spring_query = spring_query;
    }

    public String getSpring_query() {
        return spring_query;
    }

    @Override
    public List<AuthorityValue> queryAuthorities(String query, int max) {


        String metadataValue = null;
        String schemeId = ConfigurationManager.getProperty("solrauthority.searchscheme." + "dc_subject_vessel");
        try {
            Context context = new Context();
            Scheme scheme = Scheme.findByIdentifier(context, schemeId);
            AuthorityMetadataValue[] queryFromSchemeMetadata = scheme.getMetadata("authority","source","querytemplate",null);
            if(queryFromSchemeMetadata != null)
            {
                for(AuthorityMetadataValue metadataValues : queryFromSchemeMetadata)
                {
                    metadataValue = metadataValues.getValue();
                }
            }

            if(metadataValue == null || metadataValue.length() <= 0)
                sparql_query = getSpring_query();
            else
                sparql_query = metadataValue;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        query = query.substring(0,1).toUpperCase()+query.substring(1,query.length());
        sparql_query = sparql_query.replace("AC_USER_INPUT", query);

        if(query != null)
        {
            //sparql_query = prefix + "SELECT DISTINCT ?value WHERE { ?s a <http://linked.rvdata.us/vocab/resource/class/Cruise> . ?s <http://purl.org/dc/terms/title> ?value FILTER regex(?value, \'^"+query+"\') } ORDER BY ?s LIMIT 100";
            return queryEndPoint(endpoint_url, sparql_query);
        }

        return new ArrayList<AuthorityValue>();
    }

    @Override
    public AuthorityValue queryAuthorityID(String id) {

        //sparql_query =prefix + "SELECT DISTINCT * WHERE { ?s a <http://linked.rvdata.us/vocab/resource/class/Cruise> . ?s <http://purl.org/dc/terms/title> ?value  . ?s dcterms:identifier \'+id+\' } ORDER BY ?s LIMIT 100";
        //sparql_query = prefix+"SELECT DISTINCT ?s ?p ?o WHERE { BIND(<http://linked.rvdata.us/resource/cruise/AE0801> as ?s) <http://linked.rvdata.us/resource/cruise/AE0801> ?p ?o }";
        String metadataValue = null;
        String schemeId = ConfigurationManager.getProperty("solrauthority.searchscheme." + "dc_subject_vessel");
        try {
            Context context = new Context();
            Scheme scheme = Scheme.findByIdentifier(context, schemeId);
            AuthorityMetadataValue[] queryFromSchemeMetadata = scheme.getMetadata("authority","source","querytemplateonerecord",null);
            if(queryFromSchemeMetadata != null)
            {
                for(AuthorityMetadataValue metadataValues : queryFromSchemeMetadata)
                {
                    metadataValue = metadataValues.getValue();
                }
            }

            if(metadataValue == null || metadataValue.length() <= 0)
                sparql_query_one_record = getSpring_query();
            else
                sparql_query_one_record = metadataValue;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sparql_query_one_record = sparql_query_one_record.replace("AC_RESOURCE", id);
        return queryEndPointOneEntry(endpoint_url,sparql_query_one_record);

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
        QuerySolution row=results.nextSolution();
        AuthorityValue authorityValue = map(row);
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


}