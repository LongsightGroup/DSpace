package org.dspace.authority.model;

import org.apache.log4j.Logger;
import org.dspace.authority.AuthorityTypes;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.event.Event;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Base Class supporting all DSpace Authority Model
 * Objects. Provides Common Metadata Support to any
 * Object that inherits this class. Ideally, this class
 * is a model for providing metadata across all DSpace
 * Objects. Eventual this code may be pushed up into DSpaceObject
 * and new tables created for Community, Collection, Bundle, Bitstream
 * and so on.
 *
 * @author Lantian Gai, Mark Diggory
 */
public abstract class AuthorityObject extends DSpaceObject {

    // findAll sortby types
    public static final int ID = 0; // sort by ID

    public static final int NAME = 1; // sort by NAME (default)

    private enum AuthorityType {
        Scheme, Concept, Term;

        public static AuthorityType getType(int type) {
            switch (type) {
                case Constants.SCHEME:
                    return Scheme;
                case Constants.CONCEPT:
                    return Concept;
                case Constants.TERM:
                    return Term;
            }
            return null;
        }
    }

    /** log4j logger */
    private static Logger log = Logger.getLogger(AuthorityObject.class);

    /** The row in the table representing this object */
    protected TableRow row;

    /** lists that need to be written out again */
    protected boolean modified = false;

    AuthorityObject(Context context, TableRow row) throws SQLException
    {
        super(context);
        this.row = row;
    }

    public String getIdentifier()
    {
        return row.getStringColumn("identifier");
    }

    protected void setIdentifier(String identifier)
    {
        row.setColumn("identifier", identifier);
        modified = true;
    }

    public void clearMetadata(String schema, String element, String qualifier,
                              String lang)
    {
        super.clearMetadata(schema, element, qualifier, lang);
        ourContext.addEvent(new Event(Event.MODIFY_METADATA, getType(), getID(), null));
    }

    public void updateLastModified() {}

    /**
     * Update the scheme - writing out scheme object and Concept list if necessary
     */
    public void update() throws SQLException, AuthorizeException
    {
        if (super.updateMetadataBoolean() || modified)
        {
            try {
                Date lastModified = new Timestamp(new Date().getTime());
                row.setColumn("modified", lastModified);
                DatabaseManager.update(ourContext, row);
                //Also fire a modified event since the object HAS been modified
                ourContext.addEvent(new Event(Event.MODIFY, this.getType(), getID(), null));
            } catch (SQLException e) {
                log.error(LogManager.getHeader(ourContext, "Error while updating ", AuthorityType.getType(getType()) + " " + getID() + " " + getName()));
                throw e;
            }
            modified = false;
        }
    }

    public static String createIdentifier(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String hash(String input) {

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] data = input.getBytes();
            m.update(data, 0, data.length);
            BigInteger i = new BigInteger(1, m.digest());
            return String.format("%1$032X", i);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage(),e);
        }

    }

    public int getID()
    {
        return row.getIntColumn("resource_id");
    }

}
