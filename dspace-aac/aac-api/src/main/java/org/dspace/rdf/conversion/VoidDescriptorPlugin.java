package org.dspace.rdf.conversion;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.rdf.RDFUtil;
import org.dspace.services.ConfigurationService;

import java.sql.SQLException;

/**
 * Created by mdiggory on 4/6/16.
 */
public class VoidDescriptorPlugin implements ConverterPlugin
{

    protected ConfigurationService configurationService;

    @Override
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public Model convert(Context context, DSpaceObject dso) throws SQLException, AuthorizeException
    {

        String myId = RDFUtil.generateIdentifier(context, dso);
        if (myId == null)
        {
            return null;
        }

        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("rdf",RDF.getURI());
        m.setNsPrefix("foaf", FOAF.getURI());
        m.setNsPrefix("void", "http://rdfs.org/ns/void#");

        m.add(
                m.createResource(myId),
                RDF.type,
                m.createResource("http://rdfs.org/ns/void#Dataset")
        );

        m.add(
                m.createResource(myId),
                FOAF.homepage,
                m.createResource(configurationService.getProperty("dspace.url"))
        );

        if(configurationService.getProperty("rdf.public.sparql.endpoint") != null) {
            m.add(
                    m.createResource(myId),
                    m.createProperty("http://rdfs.org/ns/void#", "sparqlEndpoint"),
                    m.createResource(configurationService.getProperty("rdf.public.sparql.endpoint"))
            );
        }

        return m;

    }

    @Override
    public boolean supports(int type) {
        if(Constants.SITE == type)
        {
            return true;
        }
        return false;
    }
}
