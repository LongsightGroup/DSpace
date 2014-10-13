/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.springmvc.rdf;


import org.dspace.core.ConfigurationManager;
import org.openrdf.http.server.ClientHTTPException;
import org.openrdf.http.server.ProtocolUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * User: mini @ atmire . com
 * Date: 2/6/14
 * Time: 2:44 PM
 */
@Controller
@RequestMapping(value={"/data"})

public class LoDController {
    public static final String DSPACE_OBJECT = "dspace.object";

    private String dspaceUrl = ConfigurationManager.getProperty("dspace.url");

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleDefault(HttpServletRequest request, HttpServletResponse response) throws RepositoryException, MalformedQueryException {
        String query = "CONSTRUCT {<" +dspaceUrl +"> ?p ?o } WHERE {<"+dspaceUrl+"> ?p ?o}";
        Map<String, Object> model = new HashMap<String,Object>();
        model.put("query", query);
        model.put("filename", "data");
        return new ModelAndView(
                new LoDView() ,
                model
        );

    }

    @RequestMapping(params = "subject")  // action phase
    public ModelAndView handleParameter(@RequestParam("subject") String subject, HttpServletRequest request, HttpServletResponse response) throws RepositoryException, MalformedQueryException {

        if(subject.startsWith(dspaceUrl))
        {
            return new ModelAndView("redirect:" + "/data" + subject.replace(dspaceUrl,""));
        }
        else
        {
            return new ModelAndView("redirect:" + subject);
        }

    }

    @RequestMapping("/**")
    public ModelAndView handleResource(HttpServletRequest request, HttpServletResponse response) throws RepositoryException, MalformedQueryException {

        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String[] url_splits = restOfTheUrl.split("/data");

        String currentUrl = dspaceUrl;
        if(url_splits != null & url_splits.length >0)
            currentUrl += url_splits[1];


        String query = "CONSTRUCT {<" +currentUrl +"> ?p ?o } WHERE {<"+currentUrl+"> ?p ?o}";
        //String query = "SELECT ?s ?p ?o  WHERE {<http://localhost:8111> ?p ?o}";


        Map<String, Object> model = new HashMap<String,Object>();

        model.put("query", query);
        model.put("filename", url_splits[1].replace("/","_"));
        return new ModelAndView(
                new LoDView() ,
                model
        );

    }




}




