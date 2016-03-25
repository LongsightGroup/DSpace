/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rdf.providing;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authority.model.Concept;
import org.dspace.authority.model.Scheme;
import org.dspace.authority.model.Term;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.rdf.RDFUtil;
import org.dspace.utils.DSpace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 *
 * @author Pascal-Nicolas Becker (dspace -at- pascal -hyphen- becker -dot- de)
 */
public class DataProviderServlet extends HttpServlet {

    protected static final String DEFAULT_LANG = "TURTLE";
    
    private static final Logger log = Logger.getLogger(DataProviderServlet.class);
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        // set all incoming encoding to UTF-8
        request.setCharacterEncoding("UTF-8");

        // we expect either a path containing only the language information
        // or a path in the form /handle/<prefix>/<suffix>[/language].
        String lang = this.detectLanguage(request);
        String cType = this.detectContentType(request, lang);
        String pathInfo = request.getPathInfo();
        
        log.debug("lang = " + lang + ", cType = " + cType + " and pathInfo: " + pathInfo);
        if (StringUtils.isEmpty(pathInfo) || StringUtils.countMatches(pathInfo, "/") < 2)
        {
            String dspaceURI = 
                    (new DSpace()).getConfigurationService().getProperty("dspace.url");
            this.serveNamedGraph(dspaceURI, lang, cType, response, null);
            return;
        }
        
        // remove trailing slash of the path info and split it.
        String[] path = request.getPathInfo().substring(1).split("/");
        // if we have 2 slashes or less, we sent repository information (see above)
        assert path.length >= 2;

        // Parse the requested path
        String bitstreamURI = null;
        Context context = null;
        DSpaceObject dso = null;
        String identifier = null;
        try {
            context = new Context(Context.READ_ONLY);
            if (request.getPathInfo().contains("bitstream/handle")) {
                // For Bitstream
                dso = HandleManager.resolveToObject(context, path[2] + "/" + path[3]);
                bitstreamURI = new DSpace().getConfigurationService().getProperty("dspace.url") + "/rdf/resource/" +
                        path[2] + "/" + path[3] + "/" + path[4] + "/" + path[5];
            } else if (request.getPathInfo().contains("scheme") || request.getPathInfo().contains("concept") || request.getPathInfo().contains("term")) {
                // For AuthorityObject
                String uuid = path[1].replace("uuid:", "");
                switch (path[0]) {
                    case "scheme":
                        dso = Scheme.findByIdentifier(context, uuid);
                        break;
                    case "concept":
                        // This method should only ever find 1 concept per uuid
                        dso = Concept.findByIdentifier(context, uuid).get(0);
                        break;
                    case "term":
                        // This method should only ever find 1 term per uuid
                        // There should only ever be 1 concept parent
                        dso = Term.findByIdentifier(context, uuid).get(0).getConcepts()[0];
                        break;
                }
            } else {
                // For Community, Collection, Item
                dso = HandleManager.resolveToObject(context, path[0] + "/" + path[1]);
            }

            if (dso != null) {
                identifier = RDFUtil.generateIdentifier(context, dso);
            }
        } catch (Exception ex) {
            // This covers SQLException NullPointerException

            log.error("Exception: " + ex.getMessage(), ex);
            // probably a problem with the db connection => send Service Unavailable
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        } finally {
            if (context != null)
                context.abort();
        }

        if (dso == null)
        {
            log.info("Cannot resolve identifier to dso. => 404");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (identifier == null)
        {
            // cannot generate identifier for dso?!
            log.error("Cannot generate identifier for " + dso.getTypeText() 
                    + " " + dso.getID() + "!");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        log.debug("Loading and sending named graph " + identifier + ".");
        this.serveNamedGraph(identifier, lang, cType, response, bitstreamURI);
    }

    protected void serveNamedGraph(String uri, String lang, String contentType, 
            HttpServletResponse response, String bitstreamURI)
            throws ServletException, IOException
    {
        Model result = null;
        result = RDFUtil.loadModel(uri);

        // Instead of checking if bitstream exists during parsing, check results
        if (result == null || result.isEmpty() || (bitstreamURI != null && !result.containsResource(result.createResource(bitstreamURI))))
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            if (result != null) result.close();

            log.info("Sent 404 Not Found, as the loaded model was null or "
                    + "empty (URI: " + uri + ").");
            return;
        }
        
        response.setContentType(contentType);
        log.debug("Set content-type to " + contentType + ".");
        try (PrintWriter out = response.getWriter()) {
            result.write(out, lang);
        } finally {
            result.close();

        }
    }
    
    protected String detectContentType(HttpServletRequest request, String lang)
    {
        // It is usefull to be able to overwrite the content type, to see the
        // request result directly in the browser. If a parameter "text" is part
        // of the request, we send the result with the content type "text/plain".
        if (request.getParameter("text") != null) return "text/plain;charset=UTF-8";
        
        if (lang.equalsIgnoreCase("TURTLE")) return "text/turtle;charset=UTF-8";
        if (lang.equalsIgnoreCase("n3")) return "text/n3;charset=UTF-8";
        if (lang.equalsIgnoreCase("RDF/XML")) return "application/rdf+xml;charset=UTF-8";
        if (lang.equalsIgnoreCase("N-TRIPLE")) return "application/n-triples;charset=UTF-8";
        
        throw new IllegalStateException("Cannot set content type for unknown language.");
    }
    
    protected String detectLanguage(HttpServletRequest request)
    {
        String pathInfo = request.getPathInfo();
        if (StringUtils.isEmpty(pathInfo)) return DEFAULT_LANG;
        String[] path = request.getPathInfo().split("/");
        String lang = path[(path.length - 1)];
        
        if (StringUtils.endsWithIgnoreCase(lang, "ttl")) return "TURTLE";
        if (StringUtils.equalsIgnoreCase(lang, "n3")) return "N3";
        if (StringUtils.equalsIgnoreCase(lang, "rdf") 
                || StringUtils.equalsIgnoreCase(lang, "xml"))
        {
            return "RDF/XML";
        }
        if (StringUtils.endsWithIgnoreCase(lang, "nt")) return "N-TRIPLE";

        return DEFAULT_LANG;
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    public String getServletInfo() {
        return "Serves repository content as rdf serialization (RDF/XML, Turtle, N-Triples and N3).";
    }
}
