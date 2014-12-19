/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.springmvc.rdf;


import info.aduna.xml.XMLUtil;
import org.dspace.app.sesame.SesameIndexingService;
import org.dspace.utils.DSpace;
import org.openrdf.http.server.ProtocolUtil;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.helpers.XMLWriterSettings;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * User: mini @ atmire . com
 * Date: 2/6/14
 * Time: 11:27 AM
 */
public class LoDView implements View {


    public LoDView()
    {

    }

    public String getContentType()
    {

        return null;
    }


    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        SesameIndexingService sesameIndexingService = new DSpace().getSingletonService(SesameIndexingService.class);

        // TODO : This should get parameterized off the the /data service, only one provider supported now.
        RepositoryConnection connection = sesameIndexingService.getProviders()[0].getConnection();

        String query = (String) model.get("query");

        try
        {
            RDFWriterFactory rdfWriterFactory = ProtocolUtil.getAcceptableService(request, response,
                    RDFWriterRegistry.getInstance());

            RDFFormat rdfFormat = rdfWriterFactory.getRDFFormat();

            RDFWriter writer = null;

            response.setStatus(200);

            if(!rdfFormat.equals(RDFFormat.RDFXML))
            {
                String mimeType = rdfFormat.getDefaultMIMEType();
                if (rdfFormat.hasCharset()) {
                    Charset charset = rdfFormat.getCharset();
                    mimeType += "; charset=" + charset.name();
                }
                response.setContentType(mimeType);

                writer = rdfWriterFactory.getWriter(response.getOutputStream());
            }
            else
            {
                response.setContentType("text/xml");
                writer = new MyRDFXMLWriter(response.getOutputStream(), request);
            }

            try {

                if(connection != null)
                {
                    try {
                        connection.prepareGraphQuery(QueryLanguage.SPARQL,query).evaluate(writer);
                    } catch (QueryEvaluationException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            finally {
                response.getOutputStream().close();
            }

        }catch (Exception e) {
            e.printStackTrace();

        }
    }


    public class MyRDFXMLWriter extends RDFXMLWriter {

        HttpServletRequest request;

        public MyRDFXMLWriter(OutputStream out, HttpServletRequest request) {
            super(out);
            this.request = request;
        }

        @Override
        protected void writeHeader() throws IOException {

            try {
                // This export format needs the RDF namespace to be defined, add a
                // prefix for it if there isn't one yet.
                setNamespace("rdf", RDF.NAMESPACE);

                if (getWriterConfig().get(XMLWriterSettings.INCLUDE_XML_PI)) {
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                }

                String xslUrl =  request.getContextPath() + "/static/lod/rdf2html.xsl" ;

                writer.write("<?xml-stylesheet type=\"text/xsl\" href=\""+xslUrl+"\"?>\n");

                if (getWriterConfig().get(XMLWriterSettings.INCLUDE_ROOT_RDF_TAG)) {
                    writeStartOfStartTag(RDF.NAMESPACE, "RDF");

                    if (defaultNamespace != null) {
                        writeNewLine();
                        writeIndent();
                        writer.write("xmlns=\"");
                        writer.write(XMLUtil.escapeDoubleQuotedAttValue(defaultNamespace));
                        writer.write("\"");
                    }

                    for (Map.Entry<String, String> entry : namespaceTable.entrySet()) {
                        String name = entry.getKey();
                        String prefix = entry.getValue();

                        writeNewLine();
                        writeIndent();
                        writer.write("xmlns:");
                        writer.write(prefix);
                        writer.write("=\"");
                        writer.write(XMLUtil.escapeDoubleQuotedAttValue(name));
                        writer.write("\"");
                    }

                    writeEndOfStartTag();
                }

                writeNewLine();
            }
            finally {
                headerWritten = true;
            }
        }
    }
}