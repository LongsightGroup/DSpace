/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.dspace.app.util.Util;
import org.dspace.content.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class AdapterSupport
{

    protected ValueFactory valueFactory = ValueFactoryImpl.getInstance();

    
    protected String baseUri = null;
    
    private String metadataServiceUri = "/handle";

    protected RepositoryProvider provider;

    public AdapterSupport()
    {
        super();
    }

    public void setProvider(RepositoryProvider provider)
    {
        this.provider = provider;
    }

    public RepositoryProvider getProvider()
    {
        return provider;
    }

    public void setBaseUri(String baseUri)
    {
        this.baseUri = baseUri;

    }

    /**
     *
     * @return
     */
    public String getBaseUri()
    {
        return baseUri;
    }

    public String getMetadataServiceUri()
    {
        return metadataServiceUri;
    }

    public void setMetadataServiceUri(String metadataServiceUri)
    {
        this.metadataServiceUri = metadataServiceUri;
    }

    /*
    protected String getMetadataURL(Object object)
    {
        if(object instanceof DSpaceObject)
        {
            return getMetadataURL(((DSpaceObject)object).getHandle());
        }
        
        return this.generateDefaultURI(String.valueOf(object.hashCode()));
    }
    */ 
    


    public String getMetadataURL(String identifier)
    {
        // Same URIs as history uses
        return getBaseUri() + getMetadataServiceUri() + "/" + identifier;
    }
    
    public Resource createResource(String handle)
    {
        return valueFactory.createURI(getMetadataURL(handle));
    }

    public Resource createResource(DSpaceObject object)
    {
        return valueFactory.createURI(getMetadataURL(object.getHandle()));
    }

    public Resource createResource(Item item)
    {
        return valueFactory.createURI(getMetadataURL(item.getHandle()));
    }

    public Resource createResource(Collection collection)
    {
        return valueFactory.createURI(getMetadataURL(collection.getHandle()));
    }



    public Resource createResource(Community community)
    {
        return valueFactory.createURI(getMetadataURL(community.getHandle()));
    }
    
    public Resource createResource(Site site)
    {
        return valueFactory.createURI(getBaseUri());
    }



    /**
     * Return the bitstream location of the the Collections Logo
     * 
     * @param bitstream
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public Resource createResource(Bitstream bitstream)
    {
        String url = getBaseUri()
        + "/retrieve/"
        + bitstream.getID();
        
        try
        {
            if(bitstream.getName() != null)
                url += "/" + Util.encodeBitstreamName(bitstream.getName(),
                    Constants.DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            if(bitstream.getName() != null)
                url += "/" + bitstream.getName();
            
            e.printStackTrace();
        }
        
        return valueFactory.createURI(url);
    }

    /**
     * Generate a default URI for a String value.
     * 
     * @param string
     * @return
     */
    public String generateDefaultURI(String string)
    {
        return "sha:" + Utils.toHex(DigestUtils.sha(string));
    }

    public Resource createResource(Item item, Bitstream bitstream)
    {
        String url = getBaseUri() + "/bitstream/handle/" + item.getHandle() + "/"
        + bitstream.getSequenceID();
        
        try
        {
            url += "/" + Util.encodeBitstreamName(bitstream.getName(),
                    Constants.DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            url += "/" + bitstream.getName();
            e.printStackTrace();
        }
        
        // TODO Auto-generated method stub
        return  valueFactory.createURI(url);
    }

    public Resource createResource(BitstreamFormat format)
    {
        return valueFactory.createURI( getBaseUri() + "/format/" + format.getID());
    }

    
    public void handleStatement(Resource subject, URI predicate, Date date)
            throws RepositoryException {
        try
        {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            
            XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            /**
             * kinda hacky, but gets around strange bug when Gregcal is
             * year, month, day is set by hand.
             */
            xmlCal.setFractionalSecond(null);
            xmlCal.setHour(DatatypeConstants.FIELD_UNDEFINED);
            xmlCal.setMinute(DatatypeConstants.FIELD_UNDEFINED);
            xmlCal.setSecond(DatatypeConstants.FIELD_UNDEFINED);
            xmlCal.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
            xmlCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            handleStatement(subject, predicate, valueFactory.createLiteral(xmlCal)); 
        }
        catch (DatatypeConfigurationException e)
        {
            throw new RepositoryException(e.getMessage(), e);
        }
    }
    
    public void handleStatement(Resource subject, URI predicate, String literal)
            throws RepositoryException {
        handleStatement(subject, predicate, valueFactory.createLiteral(literal));
    }

    public void handleStatement(Resource subject, URI predicate, Value object)
            throws RepositoryException {
        this.handleStatement(
                valueFactory.createStatement(subject, predicate, object));
    }

    public void handleStatement(Resource subject, URI predicate, Value object, Resource context)
            throws RepositoryException {
        this.handleStatement(valueFactory.createStatement(subject, predicate, object, context));
    }

    public void handleStatement(Resource subject, URI predicate, String literal, Resource context)
            throws RepositoryException {
        handleStatement(subject, predicate, valueFactory.createLiteral(literal),context);
    }

    public void handleStatement(Statement statement)
            throws RepositoryException {
        this.getProvider().getConnection().add(statement);
    }
}