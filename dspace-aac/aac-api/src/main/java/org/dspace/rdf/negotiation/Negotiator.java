/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rdf.negotiation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authority.model.AuthorityObject;
import org.dspace.authority.model.Concept;
import org.dspace.authority.model.Scheme;
import org.dspace.authority.model.Term;
import org.dspace.content.*;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.rdf.RDFConfiguration;
import org.dspace.utils.DSpace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author Pascal-Nicolas Becker (dspace -at- pascal -hyphen- becker -dot- de)
 */
public class Negotiator {
    
    // Serialiazation codes
    public static final int UNSPECIFIED = -1;
    public static final int WILDCARD = 0;
    public static final int HTML = 1;
    public static final int RDFXML = 2;
    public static final int TURTLE = 3;
    public static final int N3 = 4;
    
    public static final String DEFAULT_LANG="html";
    public static final String ACCEPT_HEADER_NAME = "Accept";
    
    private static final Logger log = Logger.getLogger(Negotiator.class);
    
    public static int negotiate(String acceptHeader)
    {
        if (acceptHeader == null) return UNSPECIFIED;
        
        String[] mediaRangeSpecs = acceptHeader.split(",");
        ArrayList<MediaRange> requestedMediaRanges = new ArrayList<>();
        for (String mediaRangeSpec : mediaRangeSpecs)
        {
            try
            {
                requestedMediaRanges.add(new MediaRange(mediaRangeSpec));
            }
            catch (IllegalArgumentException | IllegalStateException ex)
            {
                log.warn("Couldn't parse part of an AcceptHeader, ignoring it.\n"
                        + ex.getMessage(), ex);
            }
        }
        if (requestedMediaRanges.isEmpty())
        {
            return UNSPECIFIED;
        }
        
        Collections.sort(requestedMediaRanges, getMediaRangeComparator());
        Collections.reverse(requestedMediaRanges);
        
        if (log.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("Parsed Accept header '" + acceptHeader + "':\n");
            for (Iterator<MediaRange> it = requestedMediaRanges.iterator(); it.hasNext(); )
            {
                MediaRange mr = it.next();
                sb.append(mr.getType()).append("/").append(mr.getSubtype());
                sb.append(" has a qvalue of ").append(Double.toString(mr.getQvalue()));
                sb.append("\n");
            }
            log.debug(sb.toString());
        }
        
        boolean wildcard = false;
        boolean html = false;
        boolean rdf = false;
        boolean n3 = false;
        boolean turtle = false;
        Iterator<MediaRange> it = requestedMediaRanges.iterator();
        
        MediaRange lookahead = it.hasNext() ? it.next() : null;
        while (lookahead != null)
        {
            double qvalue = lookahead.getQvalue();
            String type = lookahead.getType();
            String subtype = lookahead.getSubtype();

            lookahead = it.hasNext() ? it.next() : null;
            
            if (qvalue <= 0.0)
            {
                // a quality of 0.0 means that the defined media range should 
                // not to be send => don't parse it.
                continue;
            }
            
            if ("*".equals(type))
            {
                wildcard = true;
            }
            if (("text".equals(type) && "html".equals(subtype))
                    || ("application".equals(type) && "xhtml+xml".equals(subtype)))
            {
                html = true;
            }
            if ("application".equals(type) && "rdf+xml".equals(subtype))
            {
                rdf = true;
            }
            if (("text".equals(type) && "n3".equals(subtype))
                    || ("text".equals(type) && "rdf+n3".equals(subtype))
                    || ("application".equals(type) && "n3".equals(subtype)))
            {
                n3 = true;
            }
            if (("text".equals(type) && "turtle".equals(subtype))
                    || ("application".equals(type) && "turtle".equals(subtype))
                    || ("application".equals(type) && "x-turtle".equals(subtype))
                    || ("application".equals(type) && "rdf+turtle".equals(subtype)))
            {
                turtle = true;
            }
            
            if (lookahead != null
                    && qvalue != lookahead.qvalue
                    && (wildcard || html || rdf || n3 || turtle))
            {
                // we've looked over all media range with the same precedence
                // and found one, we can serve
                break;
            }
        }
        
        if (html)
        {
            return HTML;
        }
        if (wildcard)
        {
            return WILDCARD;
        }
        else if (turtle)
        {
            return TURTLE;
        }
        else if (n3)
        {
            return N3;
        }
        else if (rdf)
        {
            return RDFXML;
        }
        
        return UNSPECIFIED;
    }

    /**
     * Method to get a comparator to compare media ranges regarding their 
     * content negotiation precedence. Following RFC 2616 a media range is 
     * higher prioritized then another media range if the first one has a higher 
     * quality value then the second. If both quality values are equal, the 
     * media range that is more specific should be used.
     * 
     * <p>Note: this comparator imposes orderings that are inconsistent with 
     * equals! Caution should be exercised when using it to order a sorted set
     * or a sorted map. Take a look at the java.util.Comparator for further 
     * information.</p>
     * @return A comparator that imposes orderings that are inconsistent with equals!
     */
    public static Comparator<MediaRange> getMediaRangeComparator() {
        return new Comparator<MediaRange>() {

            @Override
            public int compare(MediaRange mr1, MediaRange mr2) {
                if (Double.compare(mr1.qvalue, mr2.getQvalue()) != 0)
                {
                    return Double.compare(mr1.qvalue, mr2.getQvalue());
                }
                if (mr1.typeIsWildcard() && mr2.typeIsWildcard()) return 0;
                if (mr1.typeIsWildcard() && !mr2.typeIsWildcard()) return -1;
                if (!mr1.typeIsWildcard() && mr2.typeIsWildcard()) return 1;
                if (mr1.subtypeIsWildcard() && mr2.subtypeIsWildcard()) return 0;
                if (mr1.subtypeIsWildcard() && !mr2.subtypeIsWildcard()) return -1;
                if (!mr1.subtypeIsWildcard() && mr2.subtypeIsWildcard()) return 1;

                // if the quality of two media ranges is equal and both don't 
                // use an asterisk either as type or subtype, they are equal in 
                // the sense of content negotiation precedence.
                return 0;
            }
        };
    }

    public static boolean sendRedirect(HttpServletResponse response, HttpServletRequest request, boolean redirectHTML)
            throws IOException
    {
        // Determined language based on Accept header
        String acceptHeader = request.getHeader(ACCEPT_HEADER_NAME);
        int serialization = Negotiator.negotiate(acceptHeader);

        StringBuilder urlBuilder = new StringBuilder();
        String lang = null;
        switch (serialization)
        {
            case (Negotiator.UNSPECIFIED):
            case (Negotiator.WILDCARD):
            {
                lang = DEFAULT_LANG;
                break;
            }
            case (Negotiator.HTML):
            {
                lang = "html";
                break;
            }
            case (Negotiator.RDFXML):
            {
                lang = "rdf";
                break;
            }
            case (Negotiator.TURTLE):
            {
                lang = "turtle";
                break;
            }
            case (Negotiator.N3):
            {
                lang = "n3";
                break;
            }
            default:
            {
                lang = DEFAULT_LANG;
                break;
            }
        }
        assert (lang != null);
        
        // don't redirect if HTML is requested and content negotiation is done
        // in a ServletFilter, as the ServletFilter should just let the request
        // pass.
        if ("html".equals(lang) && !redirectHTML)
        {
            return false;
        }

        // as we do content negotiation and we'll redirect the request, we 
        // should send a vary caching so browsers can adopt their caching strategy
        response.setHeader("Vary", "Accept");

        // Parse the requested path
        String pathInfo = request.getPathInfo();
        if (StringUtils.isEmpty(pathInfo) || StringUtils.countMatches(pathInfo, "/") < 2)
        {
            log.debug("Path does not contain the expected number of slashes.");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        // remove extra parts of the path info and split it.
        String[] path;
        if (request.getPathInfo().startsWith("/handle/")) {
            path = request.getPathInfo().substring(8).split("/");
        } else {
            path = request.getPathInfo().substring(1).split("/");
        }


        int sequenceID = -1;
        String filename = null;
        Context context = null;
        DSpaceObject dso = null;
        try {
            context = new Context(Context.READ_ONLY);
            if (request.getPathInfo().contains("bitstream/handle")) {
                // For Bitstream
                dso = HandleManager.resolveToObject(context, path[2] + "/" + path[3]);
                if (redirectHTML) { // This contains the sequenceID
                    sequenceID = Integer.parseInt(path[4]);
                    filename = path[5];
                } else { // Get sequence ID as parameter
                    sequenceID = Integer.parseInt(request.getParameter("sequence"));
                    filename = path[4];
                }
            } else if (request.getPathInfo().contains("scheme") || request.getPathInfo().contains("concept") || request.getPathInfo().contains("term")) {
                // For Authority Object
                String uuid = null;
                int id = -1;
                if (redirectHTML) { // Is UUID
                    uuid = path[1].replace("uuid:","");
                } else { // Is ID
                    id = Integer.parseInt(path[1]);
                }
                switch (path[0]) {
                    case "scheme":
                        if (redirectHTML) {
                            dso = Scheme.findByIdentifier(context, uuid);
                        } else {
                            dso = Scheme.find(context, id);
                        }
                        break;
                    case "concept":
                        if (redirectHTML) {
                            dso = Concept.findByIdentifier(context, uuid);
                        } else {
                            dso = Concept.find(context, id);
                        }
                        break;
                    case "term":
                        if (redirectHTML) {
                            dso = Term.findByIdentifier(context, uuid);
                        } else {
                            dso = Term.find(context, id);
                        }
                        break;
                }
            } else {
                // For Community, Collection, Item
                dso = HandleManager.resolveToObject(context, path[0] + "/" + path[1]);
            }
        } catch (SQLException ex) {
            log.error("SQLException: " + ex.getMessage(), ex);
            // probably a problem with the db connection => send Service Unavailable
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        } finally {
            if (context != null)
                context.abort();
        }

        if (dso == null)
        {
            log.info("Cannot resolve handle identifier to dso. => 404");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        // if html is requested we have to forward to the repositories webui.
        if ("html".equals(lang))
        {
            urlBuilder.append((new DSpace()).getConfigurationService()
                    .getProperty("dspace.url"));
            if (filename != null && sequenceID != -1) {
                urlBuilder.append("/bitstream/handle/" + dso.getHandle() +"/" + filename + "?sequence=" + sequenceID + "&isAllowed=y");
            }else if (dso instanceof Community || dso instanceof Collection || dso instanceof Item) {
                urlBuilder.append("/handle/" + dso.getHandle() + "/");
            } else if (dso instanceof Scheme || dso instanceof Concept || dso instanceof Term){
                urlBuilder.append("/" + dso.getClass().toString().toLowerCase().split("\\.")[4] + "/" + dso.getID());
            }
            String url = urlBuilder.toString();

            log.debug("Will forward to '" + url + "'.");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("Location", url);
            response.flushBuffer();
            return true;
        }
        
        // load the URI of the dspace-rdf module.
        urlBuilder.append(RDFConfiguration.getDSpaceRDFModuleURI());
        if (urlBuilder.length() == 0)
        {
            log.error("Cannot load URL of dspace-rdf module. "
                    + "=> 500 Internal Server Error");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.flushBuffer();
            return true;
        }
        // and build the uri to the DataProviderServlet

        if (filename != null && sequenceID != -1) {
            // /bitstream/handle/[handle]/[sequence-id]/filename.ext for Bitstreams
            urlBuilder.append("/data/bitstream/handle/" + dso.getHandle() + "/" + sequenceID + "/" + filename + "/" + lang);
        }else if (dso instanceof Community || dso instanceof Collection || dso instanceof Item) {
            // /[handle] for Items, Collections, and Communities
            urlBuilder.append("/data/" + dso.getHandle() + "/" + lang);
        } else if (dso instanceof Scheme || dso instanceof Concept || dso instanceof Term){
            // /scheme|concept|term/uuid:[UUID] for Schemes, Concepts, and Terms
            urlBuilder.append("/data/" + dso.getClass().toString().toLowerCase().split("\\.")[4] + "/uuid:" + ((AuthorityObject) dso).getIdentifier() + "/" + lang);
        }
        String url = urlBuilder.toString();
        log.debug("Will forward to '" + url + "'.");
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location", url);
        response.flushBuffer();
        return true;
    }
}
