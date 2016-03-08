package org.dspace.rdf.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Created by mdiggory on 3/7/16.
 */
public class REPO {


    /**
     * The RDF model that holds the REPO entities
     */
    private static final Model m = ModelFactory.createDefaultModel();

    /**
     * The namespace of the REPO vocabulary as a string
     */
    public static final String uri = "http://digital-repositories.org/ontologies/dspace/0.1.0#";

    /**
     * Returns the namespace of the REPO schema as a string
     *
     * @return the namespace of the REPO schema
     */
    public static String getURI() {
        return uri;
    }

    /**
     * The namespace of the REPO vocabulary
     */
    public static final Resource NAMESPACE = m.createResource(uri);

    public static final Resource Bitstream = m.createResource(uri + "Bitstream");

    public static final Resource Collection = m.createResource(uri + "Collection");

    public static final Resource Community = m.createResource(uri + "Community");

    public static final Resource Item = m.createResource(uri + "Item");

    public static final Resource Repository = m.createResource(uri + "Repository");

    public static final Resource Dataset = m.createResource(uri + "Dataset");

    // REPO lexical label properties

    public static final Property hasBitstream = m.createProperty(uri + "hasBitstream");

    public static final Property hasCollection = m.createProperty(uri + "hasCollection");

    public static final Property hasCommunity = m.createProperty(uri + "hasCommunity");

    public static final Property hasItem = m.createProperty(uri + "hasItem");

    public static final Property hasPart = m.createProperty(uri + "hasPart");

    public static final Property hasSubcommunity = m.createProperty(uri + "hasSubcommunity");

    public static final Property isPartOf = m.createProperty(uri + "isPartOf");

    public static final Property isPartOfCollection = m.createProperty(uri + "isPartOfCollection");

    public static final Property isPartOfCommunity = m.createProperty(uri + "isPartOfCommunity");

    public static final Property isPartOfRepository = m.createProperty(uri + "isPartOfRepository");

    public static final Property isSubcommunityOf = m.createProperty(uri + "isSubcommunityOf");

    public static final Property oaiPmh2Interface = m.createProperty(uri + "oaiPmh2Interface");

    public static final Property checksum = m.createProperty(uri + "checksum");

    public static final Property checksumAlgorithm = m.createProperty(uri + "checksumAlgorithm");

    public static final Property mimeType = m.createProperty(uri + "mimeType");

    public static final Property size = m.createProperty(uri + "size");

}