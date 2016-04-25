/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.authority.concept;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.util.HashUtil;
import org.apache.excalibur.source.SourceValidity;
import org.dspace.app.util.MetadataExposure;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.DSpaceValidity;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.authority.model.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.browse.ItemCountException;
import org.dspace.browse.ItemCounter;


import org.dspace.content.*;
import org.dspace.content.authority.*;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

/**
 * Display a single concept. This includes a full text search, browse by list,
 * concept display and a list of recent submissions.
 *     private static final Logger log = Logger.getLogger(DSpaceFeedGenerator.class);

 * @author Scott Phillips
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 */
public class ConceptViewer extends AbstractDSpaceTransformer implements CacheableProcessingComponent
{
    /** Language Strings */
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");

    private static final Message T_context_head = message("xmlui.administrative.Navigation.context_head");

    public static final Message T_untitled =
            message("xmlui.general.untitled");

    private static final Message T_head_browse =
            message("xmlui.ArtifactBrowser.ConceptViewer.head_browse");

    private static final Message T_browse_titles =
            message("xmlui.ArtifactBrowser.ConceptViewer.browse_titles");

    private static final Message T_browse_authors =
            message("xmlui.ArtifactBrowser.ConceptViewer.browse_authors");

    private static final Message T_browse_dates =
            message("xmlui.ArtifactBrowser.ConceptViewer.browse_dates");

    private static final Message T_head_sub_concepts =
            message("xmlui.ArtifactBrowser.ConceptViewer.head_sub_concepts");

    private static final Message T_authorities = message("xmlui.administrative.scheme.trail.authorities");
    private static final Message T_scheme = message("xmlui.ArtifactBrowser.ConceptViewer.scheme");
    private static final Message T_attribute = message("xmlui.ArtifactBrowser.ConceptViewer.attribute");
    private static final Message T_identifier = message("xmlui.ArtifactBrowser.ConceptViewer.identifier");
    private static final Message T_creation_date = message("xmlui.ArtifactBrowser.ConceptViewer.creation_date");
    private static final Message T_status = message("xmlui.ArtifactBrowser.ConceptViewer.status");
    private static final Message T_source = message("xmlui.ArtifactBrowser.ConceptViewer.source");
    private static final Message T_preferred_terms = message("xmlui.ArtifactBrowser.ConceptViewer.preferred_terms");
    private static final Message T_alternative_terms = message("xmlui.ArtifactBrowser.ConceptViewer.alternative_terms");
    private static final Message T_preferred_label = message("xmlui.ArtifactBrowser.ConceptViewer.preferred_label");
    private static final Message T_metadata_values = message("xmlui.ArtifactBrowser.ConceptViewer.metadata_values");
    private static final Message T_field_name = message("xmlui.ArtifactBrowser.ConceptViewer.field_name");
    private static final Message T_value = message("xmlui.ArtifactBrowser.ConceptViewer.value");
    private static final Message T_related_concept = message("xmlui.ArtifactBrowser.ConceptViewer.related_concept");
    private static final Message T_relation = message("xmlui.ArtifactBrowser.ConceptViewer.relation");


    /** Cached validity object */
    private SourceValidity validity;

    /**
     * Generate the unique caching key.
     * This key must be unique inside the space of this component.
     */
    public Serializable getKey() {
        try {
            DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

            if (dso == null)
            {
                return "0";  // no item, something is wrong
            }

            return HashUtil.hash(dso.getHandle());
        }
        catch (SQLException sqle)
        {
            // Ignore all errors and just return that the component is not cachable.
            return "0";
        }
    }

    /**
     * Generate the cache validity object.
     *
     * This validity object includes the concept being viewed, all
     * sub-communites (one level deep), all sub-concepts, and
     * recently submitted items.
     */
    public SourceValidity getValidity()
    {
        if (this.validity == null)
        {
            Concept concept = null;
            try {
                DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

                if (dso == null)
                {
                    return null;
                }

                if (!(dso instanceof Concept))
                {
                    return null;
                }

                concept = (Concept) dso;

                DSpaceValidity validity = new DSpaceValidity();
                validity.add(concept);

                Term[] terms = concept.getPreferredTerms();
                // Sub concepts
                for (Term term : terms)
                {
                    validity.add(term);

                    // Include the item count in the validity, only if the value is cached.
                    boolean useCache = ConfigurationManager.getBooleanProperty("webui.strengths.cache");
                    if (useCache)
                    {
                        try {
                            int size = new ItemCounter(context).getCount(term);
                            validity.add("size:"+size);
                        } catch(ItemCountException e) { /* ignore */ }
                    }
                }

                this.validity = validity.complete();
            }
            catch (Exception e)
            {
                // Ignore all errors and invalidate the cache.
            }

        }
        return this.validity;
    }


    /**
     * Add the concept's title and trail links to the page's metadata
     */
    public void addPageMeta(PageMeta pageMeta) throws SAXException,
            WingException, UIException, SQLException, IOException,
            AuthorizeException
    {
        Concept concept=null;
        String conceptId = this.parameters.getParameter("concept",null);
        if (conceptId == null)
        {
            return;
        }
        if (conceptId.contains("uuid:")) {
            concept = Concept.findByIdentifier(context, conceptId.replace("uuid:", ""));
        } else {
            concept = Concept.find(context, Integer.parseInt(conceptId));
        }
        if(concept==null)
        {
            return;
        }
        // Set the page title
        String name = concept.getLabel();
        if (name == null || name.length() == 0)
        {
            pageMeta.addMetadata("title").addContent(T_untitled);
        }
        else
        {
            pageMeta.addMetadata("title").addContent(name);
        }

        // Add the trail back to the repository root.
        pageMeta.addTrailLink(contextPath + "/",T_dspace_home);
        Scheme owner = concept.getScheme();
        if(owner!=null)
        {
            pageMeta.addTrailLink(contextPath + "/scheme/"+owner.getID(),owner.getName());
        }
        if(concept!=null)
        {
            pageMeta.addTrail().addContent(concept.getLabel());
        }
        HandleUtil.buildHandleTrail(concept, pageMeta, contextPath);
    }

    /**
     * Display a single concept (and reference any sub communites or
     * concepts)
     */
    public void addBody(Body body) throws SAXException, WingException,
            UIException, SQLException, IOException, AuthorizeException
    {

        String conceptId = this.parameters.getParameter("concept",null);
        if(conceptId==null)
        {
            return;
        }
        Concept concept = null;
        if (conceptId.contains("uuid:")) {
            concept = Concept.findByIdentifier(context, conceptId.replace("uuid:", ""));
        } else {
            concept = Concept.find(context, Integer.parseInt(conceptId));
        }
        if(concept==null)
        {
            return;
        }

        // Build the concept viewer division.
        Division home = body.addDivision("concept-home", "primary thesaurus concept");


        Scheme parentScheme = concept.getScheme();
        List parentList = home.addList("scheme");
        parentList.setHead(T_scheme);
        if(parentScheme!=null)
        {
            parentList.addItem().addXref("/scheme/"+parentScheme.getID(),parentScheme.getName() + "(" + parentScheme.getIdentifier().substring(0,8) + ")");
        }

        String name = concept.getLabel();
        if (name == null || name.length() == 0)
        {
            home.setHead(T_untitled);
        }
        else
        {
            home.setHead(name);
        }

        // Add main reference:
        {
            Division viewer = home.addDivision("concept-view","secondary");
            Division attributeSection = viewer.addDivision("attribute-section","thesaurus-section");
            Table attribute = attributeSection.addTable("attribute",3,2,"thesaurus-table");
            attribute.setHead(T_attribute);
            Row aRow = attribute.addRow();
            aRow.addCell().addContent(T_identifier);
            aRow.addCell().addContent(concept.getIdentifier());
            aRow = attribute.addRow();
            aRow.addCell().addContent(T_creation_date);
            aRow.addCell().addContent(concept.getCreated().toString());
            aRow = attribute.addRow();
            aRow.addCell().addContent(T_status);
            aRow.addCell().addContent(concept.getStatus());
            aRow = attribute.addRow();
            aRow.addCell().addContent(T_source);
            if(concept.getSource()!=null) {
                aRow.addCell().addContent(concept.getSource());
            }
            else
            {
                aRow.addCell().addContent("NULL");
            }

            Term[] preferredTerms = concept.getPreferredTerms();
            if(preferredTerms!=null && preferredTerms.length >0)
            {
                Division preSection = viewer.addDivision("pre-term-section","thesaurus-section");
                preSection.setHead(T_preferred_terms);
                Table table = preSection.addTable("pre-term", preferredTerms.length + 1, 3,"thesaurus-table");

                Row header = table.addRow(Row.ROLE_HEADER);
                header.addCell().addContent(T_preferred_label);
                header.addCell().addContent(T_identifier);

                for(Term term : preferredTerms)
                {
                    Row item = table.addRow();
                    item.addCell().addXref("/term/"+term.getID(),term.getLiteralForm());
                    item.addCell().addContent(term.getIdentifier());
                }
            }

            Term[] altTerms = concept.getAltTerms();
            if(altTerms!=null && altTerms.length >0)
            {
                Division altSection = viewer.addDivision("alt-term-section","thesaurus-section");
                altSection.setHead(T_alternative_terms);
                Table table = altSection.addTable("alt-term", altTerms.length + 1, 3,"thesaurus-table");

                Row header = table.addRow(Row.ROLE_HEADER);
                header.addCell().addContent(T_preferred_label);
                header.addCell().addContent(T_identifier);

                for(Term term : altTerms)
                {
                    Row item = table.addRow();
                    item.addCell().addXref("/term/"+term.getID(),term.getLiteralForm());
                    item.addCell().addContent(term.getIdentifier());
                }
            }

            java.util.List<Metadatum> values = concept.getMetadata();
            int i = 0;

            if(values!=null&&values.size()>0)
            {
                Division metadataSection = viewer.addDivision("metadata-section", "thesaurus-section");
                metadataSection.setHead(T_metadata_values);
                Table metadataTable = metadataSection.addTable("metadata", values.size() + 1, 2,"detailtable thesaurus-table");

                Row header = metadataTable.addRow(Row.ROLE_HEADER);
                header.addCell().addContent(T_field_name);
                header.addCell().addContent(T_value);
                while (i<values.size()&&values.get(i)!=null)
                {
                    Metadatum value = (Metadatum)values.get(i);
                    if (!MetadataExposure.isHidden(context, value.schema, value.element, value.qualifier)) {
                        Row mRow = metadataTable.addRow();

                        if (value.qualifier != null && value.qualifier.length() > 0) {
                            mRow.addCell().addContent(value.schema + "." + value.element + "." + value.qualifier);
                        } else {
                            mRow.addCell().addContent(value.schema + "." + value.element);
                        }
                        mRow.addCell().addContent(value.value);
                    }
                    i++;
                }

            }

            for (Concept2ConceptRole role : Concept2ConceptRole.findAll(context)) {
                Concept2Concept[] parent = Concept2Concept.findByParentAndRole(context, concept.getID(), role.getRelationID());
                if (parent == null) {
                    parent = new Concept2Concept[0];
                }
                Concept2Concept[] child = Concept2Concept.findByChildAndRole(context, concept.getID(), role.getRelationID());
                if (child == null) {
                    child = new Concept2Concept[0];
                }

                if (parent.length > 0 || child.length > 0) {
                    int length = parent.length + child.length;
                    Division aSection = viewer.addDivision("associate-section","thesaurus-section");
                    String incomingLabel = role.getIncomingLabel();
                    String outgoingLabel = role.getLabel();
                    if (incomingLabel.equals(outgoingLabel)) {
                        aSection.setHead(outgoingLabel);
                    } else {
                        aSection.setHead(incomingLabel + "/" + outgoingLabel);
                    }
                    Table table = aSection.addTable("associate", length + 1, 3,"detailtable thesaurus-table");

                    Row header = table.addRow(Row.ROLE_HEADER);
                    header.addCell().addContent(T_relation);
                    header.addCell().addContent(T_related_concept);

                    for(Concept2Concept parentRelation : parent)
                    {
                        Concept incomingConcept = Concept.find(context,parentRelation.getOutgoingId());
                        Row acRow = table.addRow();
                        acRow.addCell().addContent(outgoingLabel);
                        acRow.addCell().addXref(contextPath + "/concept/"+incomingConcept.getID(),incomingConcept.getLabel());
                    }
                    for(Concept2Concept childRelation : child)
                    {
                        Concept incomingConcept = Concept.find(context,childRelation.getIncomingId());
                        Row acRow = table.addRow();
                        acRow.addCell().addContent(incomingLabel);
                        acRow.addCell().addXref(contextPath + "/concept/"+incomingConcept.getID(),incomingConcept.getLabel());
                    }
                }
            }
        } // main reference
    }



    public void addOptions(org.dspace.app.xmlui.wing.element.Options options) throws org.xml.sax.SAXException, org.dspace.app.xmlui.wing.WingException, org.dspace.app.xmlui.utils.UIException, java.sql.SQLException, java.io.IOException, org.dspace.authorize.AuthorizeException
    {


        String conceptId = this.parameters.getParameter("concept",null);
        if(conceptId==null)
        {
            return;
        }
        Concept concept = null;
        if (conceptId.contains("uuid:")) {
            concept = Concept.findByIdentifier(context, conceptId.replace("uuid:", ""));
        } else {
            concept = Concept.find(context, Integer.parseInt(conceptId));
        }
        if (concept == null) {
            return;
        }
        options.addList("browse");
        options.addList("account");
        List authority = options.addList("context");
        options.addList("administrative");

        //Check if a system administrator
        boolean isSystemAdmin = AuthorizeManager.isAdmin(this.context);


        // System Administrator options!
        if (isSystemAdmin)
        {
            authority.setHead(T_context_head);
            authority.addItemXref(contextPath+"/admin/concept?conceptID="+concept.getID()+"&edit","Edit Concept Attribute");
            authority.addItemXref(contextPath+"/admin/concept?conceptID="+concept.getID()+"&editMetadata","Edit Concept Metadata Value");
            authority.addItemXref(contextPath+"/admin/concept?conceptID="+concept.getID()+"&addConcept","Add Related Concept");
            authority.addItemXref(contextPath+"/admin/concept?conceptID="+concept.getID()+"&search","Search & Add Terms");
        }
    }



    /**
     * Recycle
     */
    public void recycle()
    {
        // Clear out our item's cache.
        this.validity = null;
        super.recycle();
    }


}
