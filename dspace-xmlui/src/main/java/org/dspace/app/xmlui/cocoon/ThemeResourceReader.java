/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.cocoon;

import java.io.IOException;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.lang.StringUtils;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

/**
 * An XMLUI Theme Resource Reader, which ONLY allows for certain types of files
 * to be included in a themes.
 *
 * @author Tim Donohue
 */
public class ThemeResourceReader extends SafeResourceReader
        implements CacheableProcessingComponent, Configurable
{
    protected String[] DEFAULT_WHITELIST = new String[]{"css", "js", "json", "gif", "jpg", "png", "ico", "bmp", "htm", "html", "svg", "ttf", "woff"};

    @Override
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException
    {
        // Check our whitelist
        String whitelistProp = ConfigurationManager.getProperty("xmlui.theme.whitelist");
        String[] whitelist;

        if(StringUtils.isEmpty(whitelistProp))
        {
            whitelist = DEFAULT_WHITELIST;
        }
        else
        {
            whitelist = whitelistProp.split(",");
        }

        // Check resource suffix against our whitelist
        for(String suffix : whitelist)
        {
            // If it is in our whitelist, let it through to the SafeResourceReader
            if(src != null && src.toLowerCase().endsWith("." + suffix.trim()))
            {
                super.setup(resolver, objectModel, src, par);
                return;
            }
        }

        // If the resource has a suffix that is NOT in our whitelist, block it
        throw new ResourceNotFoundException("Resource not found (" + src + ")");
    }
}

