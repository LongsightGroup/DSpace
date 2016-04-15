/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rdf.storage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.app.util.Util;
import org.dspace.authority.model.Concept;
import org.dspace.authority.model.Scheme;
import org.dspace.authority.model.Term;
import org.dspace.content.Bitstream;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.Site;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.rdf.RDFConfiguration;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 *
 * @author Pascal-Nicolas Becker (dspace -at- pascal -hyphen- becker -dot- de)
 */
public class LocalURIGenerator implements URIGenerator {
    private static final Logger log = Logger.getLogger(LocalURIGenerator.class);

    @Override
    public String generateIdentifier(Context context, int type, int id, 
            String identifier, String[] identifiers)
            throws SQLException
    {
        String urlPrefix = RDFConfiguration.getDSpaceRDFModuleURI() + "/resource/";
        
        if (type == Constants.SITE)
        {
            return urlPrefix + Site.getSiteHandle();
        }

        if (type == Constants.SCHEME)
        {
            if (identifier != null) {
                return urlPrefix + "scheme/uuid:" + identifier;
            } else {
                return urlPrefix + "scheme/uuid:" + Scheme.find(context, id).getIdentifier();
            }
        }

        if (type == Constants.CONCEPT)
        {
            if (identifier != null) {
                return urlPrefix + "concept/uuid:" + identifier;
            } else {
                return urlPrefix + "concept/uuid:" + Concept.find(context, id).getIdentifier();
            }
        }

        if (type == Constants.TERM)
        {
            if (identifier != null) {
                return urlPrefix + "term/uuid:" + identifier;
            } else {
                return urlPrefix + "term/uuid:" + Term.find(context, id).getIdentifier();
            }
        }

        if (type == Constants.BITSTREAM)
        {
            Bitstream bitstream = Bitstream.find(context, id);

            DSpaceObject parent = bitstream.getParentObject();

            if (!(parent instanceof Item))
            {
                // Bitstream is a community or collection logo.
                // we currently ignore those
                return null;
            }

            String name = "file";

            try{
                name = Util.encodeBitstreamName(bitstream.getName(), Constants.DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }

            return urlPrefix + parent.getHandle() + "/" + bitstream.getSequenceID() + "/" + name;

        }
        
        if (type == Constants.COMMUNITY 
                || type == Constants.COLLECTION 
                || type == Constants.ITEM)
        {
            if (StringUtils.isEmpty(identifier))
            {
                throw new IllegalArgumentException("Handle is null");
            }
            return urlPrefix + identifier;
        }

        return null;
    }

    @Override
    public String generateIdentifier(Context context, DSpaceObject dso) throws SQLException {
//        if (dso.getType() != Constants.SITE
//                && dso.getType() != Constants.COMMUNITY
//                && dso.getType() != Constants.COLLECTION
//                && dso.getType() != Constants.ITEM
//                && dso.getType() != Constants.BITSTREAM)
//        {
//            return null;
//        }
        
        return generateIdentifier(context, dso.getType(), dso.getID(), dso.getHandle(), dso.getIdentifiers(context));
    }

}
