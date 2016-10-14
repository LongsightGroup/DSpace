/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.authority.scheme;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.util.HashUtil;
import org.apache.excalibur.source.SourceValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.DSpaceValidity;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.browse.ItemCountException;
import org.dspace.browse.ItemCounter;


import org.dspace.content.DSpaceObject;
import org.dspace.authority.model.Concept;
import org.dspace.authority.model.Scheme;
import org.dspace.content.Metadatum;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

/**
 * Display a single scheme. This includes a full text search, browse by list,
 * scheme display and a list of recent submissions.
 *     private static final Logger log = Logger.getLogger(DSpaceFeedGenerator.class);

 * @author Scott Phillips
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 */
public class SchemeViewer extends AbstractDSpaceTransformer implements CacheableProcessingComponent
{
    /** Language Strings */
    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");
    private static final Message T_context_head = message("xmlui.administrative.Navigation.context_head");
    private static final Message T_administrative_authority 	= message("xmlui.administrative.Navigation.administrative_authority_control");
    public static final Message T_untitled =
            message("xmlui.general.untitled");
    private static final Message T_attribute_head =
            message("xmlui.administrative.scheme.SchemeViewer.attribute");
    private static final Message T_identifier =
            message("xmlui.administrative.scheme.SchemeViewer.identifier");
    private static final Message T_creation_date =
            message("xmlui.administrative.scheme.SchemeViewer.creation_date");
    private static final Message T_metadata_values =
            message("xmlui.administrative.scheme.SchemeViewer.metadata_values");
    private static final Message T_field_name =
            message("xmlui.administrative.scheme.SchemeViewer.field_name");
    private static final Message T_value =
            message("xmlui.administrative.scheme.SchemeViewer.value");
    public static final Message T_authorities =
            message("xmlui.administrative.scheme.trail.authorities");

    private static final Message T_head_browse =
            message("xmlui.ArtifactBrowser.SchemeViewer.head_browse");

    private static final Message T_browse_titles =
            message("xmlui.ArtifactBrowser.SchemeViewer.browse_titles");

    private static final Message T_browse_authors =
            message("xmlui.ArtifactBrowser.SchemeViewer.browse_authors");

    private static final Message T_browse_dates =
            message("xmlui.ArtifactBrowser.SchemeViewer.browse_dates");

    private static final Message T_head_sub_concepts =
            message("xmlui.ArtifactBrowser.SchemeViewer.head_sub_concepts");

    // How many results to show on a page.
    private static final int RESULTS_PER_PAGE = 5;

    /** The maximum size of a collection name allowed */
    private static final int MAX_COLLECTION_NAME = 25;

    private static final Message T_search_head =
            message("xmlui.aspect.authority.scheme.ManageSchemeMain.search_head");


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
     * This validity object includes the scheme being viewed, all
     * sub-communites (one level deep), all sub-concepts, and
     * recently submitted items.
     */
    public SourceValidity getValidity()
    {
        if (this.validity == null)
        {
            Scheme scheme = null;
            try {
                DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

                if (dso == null)
                {
                    return null;
                }

                if (!(dso instanceof Scheme))
                {
                    return null;
                }

                scheme = (Scheme) dso;

                DSpaceValidity validity = new DSpaceValidity();
                validity.add(scheme);

                //remove it from showing because it slows down the page
                Concept[] concepts = scheme.getConcepts();
                // Sub concepts
                for (Concept concept : concepts)
                {
                    validity.add(concept);

                    // Include the item count in the validity, only if the value is cached.
                    boolean useCache = ConfigurationManager.getBooleanProperty("webui.strengths.cache");
                    if (useCache)
                    {
                        try {
                            int size = new ItemCounter(context).getCount(concept);
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
     * Add the scheme's title and trail links to the page's metadata
     */
    public void addPageMeta(PageMeta pageMeta) throws SAXException,
            WingException, UIException, SQLException, IOException,
            AuthorizeException
    {
        Scheme scheme=null;
        String schemeId = this.parameters.getParameter("scheme","-1");
        if (schemeId.equals("-1"))
        {
            return;
        }
        else
        {
            scheme = Scheme.find(context,Integer.parseInt(schemeId));
            if(scheme==null)
            {
                return;
            }
        }

        // Add the trail back to the repository root.
        pageMeta.addTrailLink(contextPath + "/",T_dspace_home);
        pageMeta.addTrailLink(contextPath + "/admin/scheme",T_authorities);
        pageMeta.addTrail().addContent(scheme.getName());
        HandleUtil.buildHandleTrail(scheme, pageMeta,contextPath);
        pageMeta.addMetadata("title").addContent(scheme.getName());
    }

    /**
     * Display a single scheme (and reference any sub communites or
     * concepts)
     */
    public void addBody(Body body) throws SAXException, WingException,
            UIException, SQLException, IOException, AuthorizeException
    {

        String schemeId = this.parameters.getParameter("scheme","-1");
        if(schemeId==null)
        {
            return;
        }
        Integer schemeID = Integer.parseInt(schemeId);
        Scheme scheme = Scheme.find(context, schemeID);

        Concept[] concepts = scheme.getConcepts();

        // Build the scheme viewer division.
        Division home = body.addDivision("scheme-home", "primary thesaurus scheme");
        String name = scheme.getAttribute("name");
        if (name == null || name.length() == 0)
        {
            home.setHead(T_untitled);
        }
        else
        {
            home.setHead(name);
        }

        Division viewer = home.addDivision("scheme-view","secondary");
        Division attributeSection = viewer.addDivision("attribute-section","thesaurus-section");
        Table attribute = attributeSection.addTable("attribute",3,2,"thesaurus-table");
        attribute.setHead(T_attribute_head);
        Row aRow = attribute.addRow();

        aRow.addCell().addContent(T_identifier);
        aRow.addCell().addContent(scheme.getIdentifier());
        aRow = attribute.addRow();
        aRow.addCell().addContent(T_creation_date);
        aRow.addCell().addContent(scheme.getCreated().toString());

        if(AuthorizeManager.isAdmin(context))
        {
            ArrayList<Metadatum> values = new ArrayList(scheme.getMetadata());
            Iterator i = values.iterator();
            if(values!=null&&values.size()>0) {
                Division metadataSection = viewer.addDivision("metadata-section","thesaurus-section");
                metadataSection.setHead(T_metadata_values);
                Table metadataTable = metadataSection.addTable("metadata", values.size() + 1, 2,"detailtable thesaurus-table");

                Row header = metadataTable.addRow(Row.ROLE_HEADER);
                header.addCell().addContent(T_field_name);
                header.addCell().addContent(T_value);
                while (i.hasNext())
                {
                    Metadatum value = (Metadatum)i.next();
                    Row mRow = metadataTable.addRow();

                    if(value.qualifier!=null&&value.qualifier.length()>0)
                    {
                        mRow.addCell().addContent(value.schema+"."+value.element+"."+value.qualifier);
                    }
                    else
                    {
                        mRow.addCell().addContent(value.schema+"."+value.element);
                    }
                    mRow.addCell().addContent(value.value);
                }
            }
        }
//
//        Division conceptSection = viewer.addDivision("concept-section","thesaurus-section");
//        conceptSection.setHead("Concepts");
//        if(concepts!=null && concepts.length >0)
//        {
//
//            Table table = conceptSection.addTable("concepts", concepts.length + 1, 2,"thesaurus-table");
//
//            Row header = table.addRow(Row.ROLE_HEADER);
//            header.addCell().addContent("ID");
//            header.addCell().addContent("Identifier");
//            header.addCell().addContent("Preferred Label");
//            for(Concept concept : concepts)
//            {
//                Row item = table.addRow();
//                item.addCell().addContent(concept.getID());
//                item.addCell().addXref("/concept/" + concept.getID(), concept.getIdentifier());
//                if(concept.getLabel()==null)
//                {
//                    item.addCell().addContent("");
//                }
//                else
//                {
//                    item.addCell().addXref("/concept/" + concept.getID(), concept.getLabel());
//                }
//            }
//
//        }
    } // main reference


    public void addOptions(org.dspace.app.xmlui.wing.element.Options options) throws org.xml.sax.SAXException, org.dspace.app.xmlui.wing.WingException, org.dspace.app.xmlui.utils.UIException, java.sql.SQLException, java.io.IOException, org.dspace.authorize.AuthorizeException
    {


        String schemeId = this.parameters.getParameter("scheme","-1");
        if(schemeId==null)
        {
            return;
        }
        Integer schemeID = Integer.parseInt(schemeId);
        Scheme scheme = Scheme.find(context, schemeID);

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
            authority.addItemXref(contextPath+"/admin/scheme?schemeID="+scheme.getID()+"&edit","Edit Scheme Attribute");
            authority.addItemXref(contextPath+"/admin/scheme?schemeID="+scheme.getID()+"&editMetadata","Edit Scheme Metadata Value");
            authority.addItemXref(contextPath+"/admin/scheme?schemeID="+scheme.getID()+"&search","Search & Add Concepts");
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

    /**
     * Search for epeople to add to this group.
     */
    private void addConceptSearch(Division div, String query, int page, Scheme scheme, String memberConceptIds) throws SQLException, WingException
    {
        String schemeId = null;
        if(scheme!=null)
        {
            schemeId = Integer.toString(scheme.getID());
        }
        int resultCount = Concept.searchResultCount(context, query, null);
        Concept[] concepts = Concept.search(context, query, page * RESULTS_PER_PAGE, RESULTS_PER_PAGE, null);

        Division results = div.addDivision("results");

        if (resultCount > RESULTS_PER_PAGE)
        {
            // If there are enough results then paginate the results
            String baseURL = contextPath +"/admin/scheme?administrative-continue="+knot.getId();
            int firstIndex = page*RESULTS_PER_PAGE+1;
            int lastIndex = page*RESULTS_PER_PAGE + concepts.length;

            String nextURL = null, prevURL = null;
            if (page < (resultCount / RESULTS_PER_PAGE))
            {
                nextURL = baseURL + "&page=" + (page + 1);
            }
            if (page > 0)
            {
                prevURL = baseURL + "&page=" + (page - 1);
            }

            results.setSimplePagination(resultCount,firstIndex,lastIndex,prevURL, nextURL);
        }

        /* Set up a table with search results (if there are any). */
        Table table = results.addTable("group-edit-search-concept",concepts.length + 1, 1);
        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCell().addContent("1");
        header.addCell().addContent("2");
        header.addCell().addContent("3");
        header.addCell().addContent("4");

        for (Concept concept : concepts)
        {
            String conceptId = String.valueOf(concept.getID());

            String lang = concept.getLang();
            String url = contextPath+"/admin/concept?administrative-continue="+knot.getId()+"&submit_edit_concept&conceptID="+conceptId;



            Row personData = table.addRow();

            personData.addCell().addContent(concept.getID());
            personData.addCell().addXref(url, concept.getIdentifier());
            personData.addCell().addXref(url, lang);

            // check if they are already a member of the group

            personData.addCell().addButton("submit_add_concept_"+conceptId).setValue("add");

        }

        if (concepts.length <= 0) {
            table.addRow().addCell(1, 4).addContent("no results");
        }
    }

}
