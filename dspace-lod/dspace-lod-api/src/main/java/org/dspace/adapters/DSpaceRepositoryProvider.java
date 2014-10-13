/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters;

import org.dspace.core.Context;
import org.dspace.event.Event;
import org.dspace.services.ConfigurationService;
import org.dspace.services.RequestService;
import org.dspace.services.model.RequestInterceptor;
import org.dspace.services.model.Session;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class DSpaceRepositoryProvider implements RepositoryProvider, RequestInterceptor {

    protected String CONNECTION = UUID.randomUUID().toString();

    private Repository repository = null;

    private RemoteRepositoryManager manager;

    private RepositoryConfig config;

    protected ConfigurationService configurationService;

    protected RequestService requestService;


    public List<DSpaceEventAdapter> getAdapters() {
        return adapters;
    }

    public void setAdapters(List<DSpaceEventAdapter> adapters) {
        this.adapters = adapters;
    }

    private List<DSpaceEventAdapter> adapters;

    public DSpaceRepositoryProvider(ConfigurationService configurationService, RequestService requestService) {
        this.configurationService = configurationService;
        this.requestService = requestService;
        requestService.registerRequestInterceptor(this);
    }


    @Override
    public void handle(Context context, Event event) throws Exception {

        DSpaceEventAdapter adapter = null;

        for(DSpaceEventAdapter dea : adapters)
        {
            if(dea.handlerFor(event.getSubject(context)))
            {
                adapter = dea;
            }
        }

        if(adapter != null)
        {
            adapter.setProvider(this);
            adapter.setBaseUri(configurationService.getProperty("dspace.url"));
            adapter.handleNamespaces();
            adapter.handle(context, event);
        }
    }

    public Repository getRepository() {
        try {

            if(repository == null)
            {
                if(!manager.isInitialized())
                    manager.initialize();

                if(!manager.hasRepositoryConfig(config.getID()))
                {
                    manager.addRepositoryConfig(this.getConfig());
                }

                repository = manager.getRepository(config.getID());

                if(repository != null && !repository.isInitialized())
                {
                    repository.initialize();
                }
            }

        } catch (RepositoryException e) {
            throw new RuntimeException(e.getMessage(),e);
        } catch (RepositoryConfigException e) {
            throw new RuntimeException(e.getMessage(),e);
        }

        return repository;
    }


    public RepositoryConnection getConnection() throws RepositoryException {
        try
        {
            RepositoryConnection connection = (RepositoryConnection)requestService.getCurrentRequest().getAttribute(CONNECTION);
            if(connection == null || !connection.isOpen()){
                connection = getRepository().getConnection();
                connection.begin();
                requestService.getCurrentRequest().setAttribute(CONNECTION,connection);
            }
            return connection;
        }catch(Exception e)
        {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public void onStart(String requestId, Session session) {

    }

    @Override
    public void onEnd(String requestId, Session session, boolean succeeded, Exception failure) {
        try
        {
            RepositoryConnection connection = (RepositoryConnection)requestService.getCurrentRequest().getAttribute(CONNECTION);
            if(connection != null){
                try {
                    connection.commit();
                } catch (RepositoryException e) {
                    connection.rollback();
                    throw new RuntimeException(e.getMessage(),e);
                } finally {
                    connection.close();
                    requestService.getCurrentRequest().setAttribute(CONNECTION,null);
                }
            }
        }catch(Exception e)
        {
            throw new RuntimeException(e.getMessage(),e);
        }


    }

    @Override
    public void clean(boolean force) throws Exception {
        getConnection().clear();
    }

    public void setManager(RemoteRepositoryManager manager) {
        this.manager = manager;
    }

    public RemoteRepositoryManager getManager() {
        return manager;
    }

    public void setConfig(RepositoryConfig config) {
        this.config = config;
    }

    public RepositoryConfig getConfig() {
        return config;
    }

    public int getOrder() {
        return 1;
    }
}
