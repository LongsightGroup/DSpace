/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.artifactbrowser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dspace.app.requestitem.RequestItem;
import org.dspace.app.requestitem.RequestItemAuthor;
import org.dspace.app.requestitem.RequestItemAuthorExtractor;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.content.Bitstream;
import org.dspace.content.DCValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.Email;
import org.dspace.core.I18nUtil;
import org.dspace.core.Utils;
import org.dspace.eperson.EPerson;
import org.dspace.handle.HandleManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.utils.DSpace;
import sun.net.www.http.HttpClient;

/**
 * This action will send a mail to request a item to administrator when all mandatory data is present.
 * It will record the request into the database.
 * 
 * Original Concept, JSPUI version:    Universidade do Minho   at www.uminho.pt
 * Sponsorship of XMLUI version:    Instituto Oceanográfico de España at www.ieo.es
 * 
 * @author Adán Román Ruiz at arvo.es (added request item support)
 */
public class SendItemRequestAction extends AbstractAction
{
    private static Logger log = Logger.getLogger(SendItemRequestAction.class);

    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel,
            String source, Parameters parameters) throws Exception
    {
        Request request = ObjectModelHelper.getRequest(objectModel);
       
        String requesterName = request.getParameter("requesterName");
        String requesterEmail = request.getParameter("requesterEmail");
        String allFiles = request.getParameter("allFiles");
        String message = request.getParameter("message");
        String bitstreamId = request.getParameter("bitstreamId");

        boolean validRecaptcha = false;
        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        String recaptchaURL = "https://www.google.com/recaptcha/api/siteverify";
        String recaptchaSecret = "6LeH6wQTAAAAAM4Gl3lmTkxdIyKWhGqmPYNw9-Id";
        String ip = request.getRemoteHost();
        //TODO Xforwardfor

        if(StringUtils.isNotBlank(recaptchaResponse)) {
            log.info("recaptcha: response:[" + recaptchaResponse +"] remoteip:[" + ip + "]");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(recaptchaURL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("secret", recaptchaSecret, ContentType.TEXT_PLAIN);
            builder.addTextBody("response", recaptchaResponse, ContentType.TEXT_PLAIN);
            builder.addTextBody("remoteip", ip, ContentType.TEXT_PLAIN);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            log.info("recaptcha post response: " + httpResponse.getStatusLine().getStatusCode() + " entity: " + EntityUtils.toString(httpResponse.getEntity()));
        } else {
            log.info("no recaptcha");
        }
     
        // User email from context
        Context context = ContextUtil.obtainContext(objectModel);
        EPerson loggedin = context.getCurrentUser();
        String eperson = null;
        if (loggedin != null)
        {
            eperson = loggedin.getEmail();
        }

        // Check all data is there
        if (StringUtils.isEmpty(requesterName) || StringUtils.isEmpty(requesterEmail) || StringUtils.isEmpty(allFiles) || StringUtils.isEmpty(message) || StringUtils.isEmpty(recaptchaResponse))
        {
            // Either the user did not fill out the form or this is the
            // first time they are visiting the page.
            Map<String,String> map = new HashMap<String,String>();
            map.put("bitstreamId",bitstreamId);

            if (StringUtils.isEmpty(requesterEmail))
            {
                map.put("requesterEmail", eperson);
            }
            else
            {
                map.put("requesterEmail", requesterEmail);
            }
            map.put("requesterName",requesterName);
            map.put("allFiles",allFiles);
            map.put("message",message);
            map.put("g-recaptcha-response", recaptchaResponse);
            return map;
        }
    	DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
        if (!(dso instanceof Item))
        {
            throw new Exception("Invalid DspaceObject at ItemRequest.");
        }
        
        Item item = (Item) dso;
        String title="";
        DCValue[] titleDC = item.getDC("title", null, Item.ANY);
        if (titleDC != null || titleDC.length > 0) {
        	title=titleDC[0].value;
        }
        
		RequestItemAuthor requestItemAuthor = new DSpace()
				.getServiceManager()
				.getServiceByName(RequestItemAuthorExtractor.class.getName(),
						RequestItemAuthorExtractor.class)
				.getRequestItemAuthor(context, item);

        RequestItem requestItem = new RequestItem(item.getID(), Integer.parseInt(bitstreamId), requesterEmail, requesterName, message, Boolean.getBoolean(allFiles));

        // All data is there, send the email
        Email email = Email.getEmail(I18nUtil.getEmailFilename(context.getCurrentLocale(), "request_item.author"));
        email.addRecipient(requestItemAuthor.getEmail());

        email.addArgument(requesterName);    
        email.addArgument(requesterEmail);   
        email.addArgument(allFiles.equals("true")?I18nUtil.getMessage("itemRequest.all"):Bitstream.find(context,Integer.parseInt(bitstreamId)).getName());      
        email.addArgument(HandleManager.getCanonicalForm(item.getHandle()));      
        email.addArgument(title);    // request item title
        email.addArgument(message);   // message
        email.addArgument(getLinkTokenEmail(context,requestItem));
        email.addArgument(requestItemAuthor.getFullName());    //   corresponding author name
        email.addArgument(requestItemAuthor.getEmail());    //   corresponding author email
        email.addArgument(ConfigurationManager.getProperty("dspace.name"));
        email.addArgument(ConfigurationManager.getProperty("mail.helpdesk"));

        email.setReplyTo(requesterEmail);
         
        email.send();
        // Finished, allow to pass.
        return null;
    }

    /**
     * Get the link to the author in RequestLink email.
     * @param context
     * @param requestItem
     * @return
     * @throws SQLException
     */
    protected String getLinkTokenEmail(Context context, RequestItem requestItem)
            throws SQLException
    {
        String base = ConfigurationManager.getProperty("dspace.url");

        String specialLink = (new StringBuffer()).append(base).append(
                base.endsWith("/") ? "" : "/").append(
                "itemRequestResponse/").append(requestItem.getNewToken(context))
                .toString()+"/";

        return specialLink;
    }

}
