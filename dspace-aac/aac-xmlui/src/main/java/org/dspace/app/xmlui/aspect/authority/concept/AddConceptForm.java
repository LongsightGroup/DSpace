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
import java.util.Locale;

import org.apache.cocoon.el.objectmodel.ObjectModel;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authority.model.Scheme;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authority.model.AuthorityObject;
import org.dspace.authority.model.Concept;
import org.dspace.authority.model.Concept;

/**
 * Present the user with all the concept metadata fields so that they
 * can describe the new concept before being created. If the user's
 * input is incorrect in someway then they may be returning here with 
 * some fields in error. In particular there is a special case for the 
 * condition when the email-address entered is already in use by 
 * another user.
 *
 * @author Alexey Maslov
 */
public class AddConceptForm extends AbstractDSpaceTransformer
{
    /** Language Strings */
    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");

    private static final Message T_concept_trail =
            message("xmlui.administrative.concept.general.epeople_trail");

    private static final Message T_title =
            message("xmlui.administrative.concept.AddConceptForm.title");

    private static final Message T_trail =
            message("xmlui.administrative.concept.AddConceptForm.trail");

    private static final Message T_head1 =
            message("xmlui.administrative.concept.AddConceptForm.head1");

    private static final Message T_email_taken =
            message("xmlui.administrative.concept.AddConceptForm.email_taken");

    private static final Message T_head2 =
            message("xmlui.administrative.concept.AddConceptForm.head2");

    private static final Message T_preferred_term =
            message("xmlui.administrative.concept.AddConceptForm.preferred_term");

    private static final Message T_preferred_error =
            message("xmlui.administrative.concept.AddConceptForm.preferred_error");

    private static final Message T_accepted =
            message("xmlui.administrative.concept.AddConceptForm.accepted");

    private static final Message T_candidate =
            message("xmlui.administrative.concept.AddConceptForm.candidate");

    private static final Message T_withdrawn =
            message("xmlui.administrative.concept.AddConceptForm.withdrawn");

    private static final Message T_status =
            message("xmlui.administrative.concept.AddConceptForm.status");

    private static final Message T_language =
            message("xmlui.administrative.concept.AddConceptForm.language");

    private static final Message T_error =
            message("xmlui.administrative.concept.AddConceptForm.error");

    private static final Message T_top_concept =
            message("xmlui.administrative.concept.AddConceptForm.top_concept");

    private static final Message T_submit_create =
            message("xmlui.administrative.concept.AddConceptForm.submit_create");

    private static final Message T_submit_cancel =
            message("xmlui.general.cancel");


    /** Language string used from other aspects: */

    private static final Message T_email_address =
            message("xmlui.Concept.EditProfile.email_address");

    private static final Message T_first_name =
            message("xmlui.Concept.EditProfile.first_name");

    private static final Message T_last_name =
            message("xmlui.Concept.EditProfile.last_name");

    private static final Message T_telephone =
            message("xmlui.Concept.EditProfile.telephone");

    private static final Message T_authorities =
            message("xmlui.administrative.scheme.trail.authorities");

    public void addPageMeta(PageMeta pageMeta) throws WingException
    {
        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(contextPath + "/admin/scheme",T_authorities);
        String schemeId = ObjectModelHelper.getRequest(objectModel).getParameter("schemeID");
        Scheme scheme = null;
        if(schemeId!=null)
        {
            try{
                if(context==null) {
                    context = ContextUtil.obtainContext(objectModel);
                }
                scheme = Scheme.find(context, Integer.parseInt(schemeId));
            }
            catch (Exception e)
            {

            }
        }
        if(scheme!=null)
        {
            pageMeta.addTrailLink(contextPath + "/scheme/"+scheme.getID(),scheme.getName());
        }
        pageMeta.addTrail().addContent(T_trail);
    }


    public void addBody(Body body) throws WingException, SQLException, AuthorizeException
    {
        // Get all our parameters
        Request request = ObjectModelHelper.getRequest(objectModel);

        Boolean topConcept = (request.getParameter("topConcept") == null)  ? false : true;
        String language = request.getParameter("language");
        String status = request.getParameter("status");
        String formUrl = contextPath+"/admin/concept";
        String schemeId = request.getParameter("schemeID");
        String value = request.getParameter("value");

        formUrl =contextPath + "/admin/scheme";
        // DIVISION: concept-add
        Division add = body.addInteractiveDivision("concept-add",formUrl,Division.METHOD_POST,"primary administrative concept");

        add.setHead(T_head1);


        List identity = add.addList("identity",List.TYPE_FORM);
        if(schemeId!=null&&schemeId.length()>0)
        {
            identity.addItem().addHidden("scheme").setValue(schemeId);

            identity.addLabel(T_preferred_term);
            Text valueText = identity.addItem().addText("value");
            if(value!=null&&value.length()==0)
            {
                valueText.setValue(value);
            }

            identity.addLabel(T_top_concept);
            CheckBox topConceptBox = identity.addItem().addCheckBox("topConcept");
            topConceptBox.setLabel(T_top_concept);
            topConceptBox.addOption(topConcept, "yes");
            topConceptBox.setOptionSelected("yes");


            identity.addLabel(T_status);
            Select statusSelect=identity.addItem().addSelect("status");
            statusSelect.addOption(Concept.Status.ACCEPTED.name(),T_accepted);
            statusSelect.addOption(Concept.Status.CANDIDATE.name(),T_candidate);
            statusSelect.addOption(Concept.Status.WITHDRAWN.name(),T_withdrawn);
            if(status!=null)
            {
                if(status.equals(Concept.Status.CANDIDATE.name()))
                {
                    statusSelect.setOptionSelected(Concept.Status.CANDIDATE.name());
                }
                else if(status.equals(Concept.Status.WITHDRAWN.name()))
                {
                    statusSelect.setOptionSelected(Concept.Status.WITHDRAWN.name());
                }
                else if(status.equals(Concept.Status.ACCEPTED.name()))
                {
                    statusSelect.setOptionSelected(Concept.Status.ACCEPTED.name());
                }
            }

            identity.addLabel(T_language);
            Select languageSelect = identity.addItem().addSelect("language");
            for(String lang: Locale.getISOLanguages()){
                languageSelect.addOption(lang,lang);
            }
            languageSelect.setOptionSelected("en");
        } else {
            identity.addItem().addContent(T_error);
        }
        Item buttons = identity.addItem();
        buttons.addButton("submit_save").setValue(T_submit_create);
        buttons.addButton("submit_cancel").setValue(T_submit_cancel);

        add.addHidden("administrative-continue").setValue(knot.getId());
    }

}
