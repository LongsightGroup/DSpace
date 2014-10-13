/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.adapters.dspace.vocabularies.DC;
import org.dspace.adapters.dspace.vocabularies.DCTERMS;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.adapters.dspace.vocabularies.VOID;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.w3c.tidy.Tidy;

public abstract class AbstractDSpaceEventAdapter extends AdapterSupport implements DSpaceEventAdapter
{

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(AbstractDSpaceEventAdapter.class);

    @Override
    public void handle(Context ctx, Event event) throws Exception {

        if(event.getSubject(ctx) == null)
            return;

        DSpaceObject subject = event.getSubject(ctx);

        DSpaceObject object = event.getObject(ctx);

        int et = event.getEventType();

        if (subject == null)
        {
            log.warn(event.getEventTypeAsString() + " event, could not get object for "
                    + event.getSubjectTypeAsString() + " id="
                    + String.valueOf(event.getSubjectID())
                    + ", perhaps it has been deleted.");
            return;
        }

        switch (et) {
            case Event.INSTALL:{
                install(ctx, subject);
            }
            break;
            case Event.CREATE:{
                create(subject);
            }
            break;
            case Event.MODIFY:break;
            case Event.MODIFY_METADATA:{
                modifyMetadata(ctx, subject);
            }
            break;
            case Event.REMOVE:{
                remove(ctx, subject, object);
            }
            break;
            case Event.ADD: {
                add(ctx, subject, object);
            }
            break;
            case Event.DELETE:{
                delete(ctx, event.getDetail());
            }
            break;
            default:
                log
                        .warn("IndexConsumer should not have been given a event of type="
                                + event.getEventTypeAsString()
                                + " on subject="
                                + event.getSubjectTypeAsString());
                break;
        }

        try
        {

            RepositoryConnection connection = this.getProvider().getConnection();
            if(connection != null){
                try {
                    connection.commit();
                } catch (RepositoryException e) {
                    connection.rollback();
                    throw new RuntimeException(e.getMessage(),e);
                } finally {
                    connection.close();
                }
            }
        }catch(Exception e)
        {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    protected abstract void install(Context ctx, DSpaceObject subject) throws Exception;

    protected abstract void add(Context ctx, DSpaceObject item, DSpaceObject object) throws Exception;

    /**
     * Generic Remove Method, may be overridden if neccessary
     * @param ctx
     * @param subject
     * @param object
     * @throws RepositoryException
     */
    protected void remove(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException, SQLException {

        Resource objResource = createResource(object);
        Resource subjResource = createResource(subject);


        // Remove a object properties that have subject as value
        this.getProvider().getConnection().remove(objResource,null,subjResource,null);

        // Remove a subject properties that have object as value
        this.getProvider().getConnection().remove(subjResource,null,objResource,null);
    }

    protected abstract void modifyMetadata(Context ctx, DSpaceObject item) throws Exception;

    protected abstract Resource create(DSpaceObject object) throws RDFHandlerException, RepositoryException;

    protected void delete(Context ctx, String object) throws RepositoryException {
            Resource resource = this.createResource(object);
            this.getProvider().getConnection().remove(resource, null, null);
    }

    public String cleanHTML(String original)
    {
        StringWriter writer = new StringWriter();
        Tidy tidy = new Tidy(); // obtain a new Tidy instance
        tidy.setXHTML(true);
        tidy.setXmlOut(true);
        tidy.setPrintBodyOnly(true);
        tidy.setQuiet(true);
        tidy.parse(new StringReader(original), writer); 
        return writer.getBuffer().toString(); 
    }



    public boolean checkPermission(Context c, DSpaceObject object) throws SQLException {

        List<ResourcePolicy> policies = AuthorizeManager.getPolicies(c, object);

        if(policies != null){
            for(ResourcePolicy policy : policies){
                // If Anonymous with READ   (Action = 0 : anonymous)
                if(policy.getAction() == 0 && policy.getGroupID() == Constants.READ)
                    return true;
                else
                    return false;

            }
        }
        return false;
    }


    public void handleNamespaces() throws RepositoryException {
        this.getProvider().getConnection().setNamespace("rdf", RDF.NAMESPACE);
        this.getProvider().getConnection().setNamespace( "dc", DC.NAMESPACE);
        this.getProvider().getConnection().setNamespace("dcterms", DCTERMS.NAMESPACE);
        this.getProvider().getConnection().setNamespace("ds", DS.NAMESPACE);
        this.getProvider().getConnection().setNamespace("void", VOID.NAMESPACE);
    }


}