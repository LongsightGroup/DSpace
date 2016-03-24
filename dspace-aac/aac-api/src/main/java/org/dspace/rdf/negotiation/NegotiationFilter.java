/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rdf.negotiation;

import org.apache.log4j.Logger;
import org.dspace.rdf.RDFConfiguration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Pascal-Nicolas Becker (dspace -at- pascal -hyphen- becker -dot- de)
 */
public class NegotiationFilter implements Filter
{
    private static final Logger log = Logger.getLogger(NegotiationFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to todo here.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
            FilterChain chain)
            throws IOException, ServletException
    {
        try
        {
            if (!RDFConfiguration.isContentNegotiationEnabled())
            {
                chain.doFilter(request, response);
                return;
            }
        }
        catch (Exception ex)
        {
            log.warn("Will deliver HTML, as I cannot determine if content "
                    + "negotiation should be enabled or not:\n" 
                    + ex.getMessage(), ex);
            chain.doFilter(request, response);
            return;
        }

        if (!(request instanceof HttpServletRequest) 
                || !(response instanceof HttpServletResponse))
        {
            // just pass request and response to the next filter, if we don't
            // have a HttpServletRequest.
            chain.doFilter(request, response);
            return;
        }

        if (!Negotiator.sendRedirect((HttpServletResponse) response, (HttpServletRequest) request, false))
        {
            // as we do content negotiation, we should send a vary caching so
            // browsers can adopt their caching strategy
            // the method Negotiator.sendRedirect does this only if it actually
            // does the redirection itself.
            ((HttpServletResponse) response).setHeader("Vary", "Accept");

            // send html as default => no forwarding necessary
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // nothing to do here.
    }
}
