package org.dspace.sesame;

import org.openrdf.repository.Repository;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.manager.LocalRepositoryManager;

public class RepositoryInitializer
{
    public RepositoryInitializer(LocalRepositoryManager manager, RepositoryConfig config) throws Exception {
        if (!manager.isInitialized()) {
            manager.initialize();
        }
        if (!manager.hasRepositoryConfig(config.getID())) {
            manager.addRepositoryConfig(config);
        }
        Repository repository = manager.getRepository(config.getID());
        if (!repository.isInitialized()) {
            repository.initialize();
        }
    }
}
