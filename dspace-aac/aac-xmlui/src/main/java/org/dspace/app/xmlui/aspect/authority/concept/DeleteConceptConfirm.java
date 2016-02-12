/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.authority.concept;

import java.sql.SQLException;
import java.util.ArrayList;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authority.model.Scheme;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authority.model.Concept;
import org.dspace.authorize.AuthorizeManager;

/**
 * Present the user with a list of not-yet-but-soon-to-be-deleted-epeople.
 *
 * @author Alexey Maslov
 */
public class DeleteConceptConfirm extends AbstractDSpaceTransformer
{
    /** Language Strings */
    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");

    private static final Message T_eperson_trail =
            message("xmlui.administrative.eperson.general.epeople_trail");

    private static final Message T_title =
            message("xmlui.administrative.concept.DeleteConceptConfirm.title");

    private static final Message T_trail =
            message("xmlui.administrative.concept.DeleteConceptConfirm.trail");

    private static final Message T_confirm_head =
            message("xmlui.administrative.concept.DeleteConceptConfirm.head");

    private static final Message T_confirm_para =
            message("xmlui.administrative.concept.DeleteConceptConfirm.para");

    private static final Message T_preferred_term =
            message("xmlui.administrative.concept.DeleteConceptConfirm.preferred_term");

    private static final Message T_identifier =
            message("xmlui.administrative.concept.DeleteConceptConfirm.identifier");

    private static final Message T_status =
            message("xmlui.administrative.concept.DeleteConceptConfirm.status");

    private static final Message T_head_id =
            message("xmlui.administrative.eperson.DeleteEPeopleConfirm.head_id");

    private static final Message T_head_name =
            message("xmlui.administrative.eperson.DeleteEPeopleConfirm.head_name");

    private static final Message T_head_email =
            message("xmlui.administrative.eperson.DeleteEPeopleConfirm.head_email");

    private static final Message T_submit_confirm =
            message("xmlui.administrative.eperson.DeleteEPeopleConfirm.submit_confirm");

    private static final Message T_submit_cancel =
            message("xmlui.general.cancel");

    private static final Message T_authorities =
            message("xmlui.administrative.scheme.trail.authorities");


    public void addPageMeta(PageMeta pageMeta) throws WingException, SQLException
    {
        Scheme scheme = Scheme.find(context, parameters.getParameterAsInteger("schemeId", -1));

        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(contextPath + "/admin/scheme",T_authorities);
        pageMeta.addTrailLink(contextPath + "/scheme/"+scheme.getID(), scheme.getName());
        pageMeta.addTrail().addContent(T_trail);
    }

    public void addBody(Body body) throws WingException, SQLException, AuthorizeException
    {
        // Get all our parameters
        String idsString = parameters.getParameter("conceptIds", null);

        ArrayList<Concept> concepts = new ArrayList<Concept>();
        for (String id : idsString.split(","))
        {
            Concept concept = Concept.find(context,Integer.valueOf(id));
            concepts.add(concept);
        }

        // DIVISION: epeople-confirm-delete
        Division deleted = body.addInteractiveDivision("concept-confirm-delete",contextPath+"/admin/concept",Division.METHOD_POST,"primary administrative concept");
        deleted.setHead(T_confirm_head);
        deleted.addPara(T_confirm_para);

        Table table = deleted.addTable("concept-confirm-delete",concepts.size() + 1, 1);
        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCell().addContent(T_preferred_term);
        header.addCell().addContent(T_identifier);
        header.addCell().addContent(T_status);
        for (Concept c : concepts)
        {
            Row row = table.addRow();
            row.addCell().addContent(c.getLabel());
            row.addCell().addContent(c.getIdentifier());
            row.addCell().addContent(c.getStatus());
        }
        Para buttons = deleted.addPara();
        buttons.addButton("submit_confirm").setValue(T_submit_confirm);
        buttons.addButton("submit_cancel").setValue(T_submit_cancel);

        deleted.addHidden("administrative-continue").setValue(knot.getId());
    }
}
