package org.dspace.rdf.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Created by mdiggory on 3/3/16.
 */
public class SKOSXL {

    	/**
	 * The RDF model that holds the SKOS-XL entities
	 */
	private static final Model m = ModelFactory.createDefaultModel();
	/**
	 * The namespace of the SKOS-XL vocabulary as a string
	 */
	public static final String uri = "http://www.w3.org/2008/05/skos-xl#";
	/**
	 * Returns the namespace of the SKOS-XL schema as a string
	 * @return the namespace of the SKOS-XL schema
	 */
	public static String getURI() {
		return uri;
	}
	/**
	 * The namespace of the SKOS-XL vocabulary
	 */
	public static final Resource NAMESPACE = m.createResource( uri );
	/* ##########################################################
	 * Defines SKOS-XL Classes
	   ########################################################## */
	public static final Resource Label = m.createResource( uri + "Label");
	/* ##########################################################
	 * Defines SKOS-XL Properties
	   ########################################################## */
	public static final Property prefLabel = m.createProperty( uri + "prefLabel");
	public static final Property altLabel = m.createProperty( uri + "altLabel");
	public static final Property hiddenLabel = m.createProperty( uri + "hiddenLabel");
	public static final Property labelRelation = m.createProperty( uri + "labelRelation");
	public static final Property literalForm = m.createProperty( uri + "literalForm");
}
