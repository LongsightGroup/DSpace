package org.dspace.authority.model;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.event.Event;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class representing a DSpace Term.
 *
 * @author Lantian Gai, Mark Diggory
 */
public class Term extends AuthorityObject {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";

    public static int alternate_term = 2;
    public static int prefer_term = 1;
    /** log4j category */
    private static final Logger log = Logger.getLogger(Term.class);

    Term(Context context, TableRow row) throws SQLException
    {
        super(context, row);
    }

    @Override
    public int getType() {
        return Constants.TERM;
    }

    @Override
    public int getID() {
        return row.getIntColumn("id");
    }

    @Override
    public String getHandle() {
        return null;
    }

    @Override
    public String getName() {
        return getLiteralForm() + " (" + getIdentifier() + ")";
    }

    public Date getLastModified()
    {
        Date myDate = row.getDateColumn("modified");

        if (myDate == null)
        {
            myDate = new Date();
        }

        return myDate;
    }
    public void setLastModified(Date date)
    {
        Date myDate = row.getDateColumn("modified");

        if (date != null)
        {
            row.setColumn("modified", date);
            modified = true;
        }
    }

    public Date getCreated()
    {
        Date myDate = row.getDateColumn("created");

        if (myDate == null)
        {
            myDate = new Date();
        }

        return myDate;
    }
    public void setCreated(Date date)
    {
        Date myDate = row.getDateColumn("created");

        if (date != null)
        {
            row.setColumn("created", date);
            modified = true;
        }
    }



    public String getStatus()
    {
        return row.getStringColumn("status");

    }
    public void setStatus(String status)
    {
        row.setColumn("status", status);
        modified = true;
    }

    public String getSource()
    {
        return row.getStringColumn("source");

    }
    public void setSource(String source)
    {
        row.setColumn("source", source);
        modified = true;
    }

    public String getLiteralForm()
    {
        return row.getStringColumn("literalForm");

    }
    public void setLiteralForm(String literalForm)
    {
        row.setColumn("literalForm", literalForm);
        modified = true;
    }
    public String getLang()
    {
        return row.getStringColumn("lang");

    }
    public void setLang(String lang)
    {
        row.setColumn("lang", lang);
        modified = true;
    }

    /**

     *
     * @param context
     *            DSpace context
     * @param query
     *            The search string
     *

     */
    public static Term[] search(Context context, String query)
            throws SQLException
    {
        return search(context, query, -1, -1,null);
    }

    /**
     * Find the terms that match the search query across firstname, lastname or email.
     * This method also allows offsets and limits for pagination purposes.
     *
     * @param context
     *            DSpace context
     *
     * @return array of Term objects
     */

    public static Term findByLiteralForm(Context context , String literalForm)
            throws SQLException, AuthorizeException
    {
        if (literalForm == null)
        {
            return null;
        }

        // All email addresses are stored as lowercase, so ensure that the email address is lowercased for the lookup
        TableRow row = DatabaseManager.findByUnique(context, "term",
                "literalForm", literalForm);

        if (row == null)
        {
            return null;
        }
        else
        {
            // First check the cache
            Term fromCache = (Term) context.fromCache(Term.class, row
                    .getIntColumn("id"));

            if (fromCache != null)
            {
                return fromCache;
            }
            else
            {
                return new Term(context, row);
            }
        }
    }

    public static Term[] search(Context context, String query, int offset, int limit,String conceptId)
            throws SQLException
    {
        String params = "%"+query.toLowerCase()+"%";
        StringBuffer queryBuf = new StringBuffer();

        if(conceptId==null){
            queryBuf.append("SELECT * FROM term WHERE id = ? ");
            queryBuf.append(" AND LOWER(identifier) like LOWER(?) ORDER BY id, created ASC ");
        }
        else
        {
            queryBuf.append("SELECT * FROM term t,concept2term c WHERE t.id = c.term_id AND c.concept_id = "+conceptId + " AND (t.id = ? OR LOWER(identifier) like LOWER(?)) ORDER BY c.role_id , t.id ASC");
        }
        // Add offset and limit restrictions - Oracle requires special code
        if ("oracle".equals(ConfigurationManager.getProperty("db.name")))
        {
            // First prepare the query to generate row numbers
            if (limit > 0 || offset > 0)
            {
                queryBuf.insert(0, "SELECT /*+ FIRST_ROWS(n) */ rec.*, ROWNUM rnum  FROM (");
                queryBuf.append(") ");
            }

            // Restrict the number of rows returned based on the limit
            if (limit > 0)
            {
                queryBuf.append("rec WHERE rownum<=? ");
                // If we also have an offset, then convert the limit into the maximum row number
                if (offset > 0)
                {
                    limit += offset;
                }
            }

            // Return only the records after the specified offset (row number)
            if (offset > 0)
            {
                queryBuf.insert(0, "SELECT * FROM (");
                queryBuf.append(") WHERE rnum>?");
            }
        }
        else
        {
            if (limit > 0)
            {
                queryBuf.append(" LIMIT ? ");
            }

            if (offset > 0)
            {
                queryBuf.append(" OFFSET ? ");
            }
        }

        String dbquery = queryBuf.toString();

        // When checking against the term-id, make sure the query can be made into a number
        Integer int_param;
        try {
            int_param = Integer.valueOf(query);
        }
        catch (NumberFormatException e) {
            int_param = Integer.valueOf(-1);
        }

        // Create the parameter array, including limit and offset if part of the query
        Object[] paramArr = new Object[] {int_param,params,params,params};
        if (limit > 0 && offset > 0)
        {
            paramArr = new Object[]{int_param, params,limit, offset};
        }
        else if (limit > 0)
        {
            paramArr = new Object[]{int_param,params,  limit};
        }
        else if (offset > 0)
        {
            paramArr = new Object[]{int_param,params,  offset};
        }

        // Get all the terms that match the query
        TableRowIterator rows = DatabaseManager.query(context,
                dbquery, paramArr);
        try
        {
            List<TableRow> termsRows = rows.toList();
            Term[] terms = new Term[termsRows.size()];

            for (int i = 0; i < termsRows.size(); i++)
            {
                TableRow row = (TableRow) termsRows.get(i);

                // First check the cache
                Term fromCache = (Term) context.fromCache(Term.class, row
                        .getIntColumn("id"));

                if (fromCache != null)
                {
                    terms[i] = fromCache;
                }
                else
                {
                    terms[i] = new Term(context, row);
                }
            }

            return terms;
        }
        finally
        {
            if (rows != null)
            {
                rows.close();
            }
        }
    }


    /**
     * Create a new Term
     *
     * @param context
     *            DSpace context object
     */
    public static Term create(Context context) throws SQLException,
            AuthorizeException
    {
        // authorized?
        if (!AuthorizeManager.isAdmin(context))
        {
            throw new AuthorizeException(
                    "You must be an admin to create an Metadata Term");
        }

        // Create a table row
        TableRow row = DatabaseManager.create(context, "term");

        Term e = new Term(context, row);

        e.setIdentifier(AuthorityObject.createIdentifier());
        log.info(LogManager.getHeader(context, "create_term", "metadata_term_id="
                + e.getID()));

        context.addEvent(new Event(Event.CREATE, Constants.TERM, e.getID(), null));

        return e;
    }

    /**
     * Delete an Term
     *
     */
    public void delete() throws SQLException, AuthorizeException
    {
        // authorized?
        if (!AuthorizeManager.isAdmin(ourContext))
        {
            throw new AuthorizeException(
                    "You must be an admin to delete an Term");
        }

        TableRow trow = DatabaseManager.querySingle(ourContext,
                "SELECT COUNT(DISTINCT concept_id) AS num FROM concept2term WHERE term_id= ? AND role_id=1",
                getID());
        if (trow.getLongColumn("num") > 0)
        {
            log.error("Can't remove term :"+getID()+", concept referred");
        }

        // Remove from cache
        ourContext.removeCached(this, getID());


        // Remove metadata
        DatabaseManager.updateQuery(ourContext,
                "DELETE FROM metadatavalue WHERE resource_id=? AND resource_type_id=?",
                getID(), Constants.TERM);

        // Remove any concept memberships first
        DatabaseManager.updateQuery(ourContext,
                "DELETE FROM Concept2Term WHERE term_id= ? ",
                getID());

        // Remove ourself
        DatabaseManager.delete(ourContext, row);

        log.info(LogManager.getHeader(ourContext, "delete_metadata_term",
                "term_id=" + getID()));
    }


    public static Term find(Context context, int id) throws SQLException
    {
        // First check the cache
        Term fromCache = (Term) context.fromCache(Term.class, id);

        if (fromCache != null)
        {
            return fromCache;
        }

        TableRow row = DatabaseManager.find(context, "term", id);

        if (row == null)
        {
            return null;
        }
        else
        {
            return new Term(context, row);
        }
    }

    public static Term findByIdentifier(Context context, String identifier)
            throws SQLException
    {
        TableRowIterator row = DatabaseManager.query(context, "select * from term where LOWER(identifier) like LOWER('" + identifier + "')");

        if (row != null)
        {
            while(row.hasNext())
            {
                TableRow myRow = row.next();
                myRow.setTable("term");
                return new Term(context,myRow);
            }
        }
        return null;
    }


    public static int searchResultCount(Context context, String query,String conceptId)
            throws SQLException
    {
        String dbquery = "%"+query.toLowerCase()+"%";
        Long count;

        // When checking against the term-id, make sure the query can be made into a number
        Integer int_param;
        try {
            int_param = Integer.valueOf(query);
        }
        catch (NumberFormatException e) {
            int_param = Integer.valueOf(-1);
        }
        TableRow row = null;
        if(conceptId==null){
            // Get all the terms that match the query
            row = DatabaseManager.querySingle(context,
                    "SELECT count(*) as termcount FROM term WHERE id = ? OR " +
                            "LOWER(identifier) like LOWER(?)",
                    new Object[]{int_param, dbquery});
        }
        else
        {
            // Get all the terms that match the query
            row = DatabaseManager.querySingle(context,
                    "SELECT count(*) as termcount FROM term t,concept2term c WHERE t.id = c.term_id AND c.concept_id = " + conceptId + " AND (t.id = ? OR LOWER(identifier) like LOWER(?))",
                    new Object[]{int_param, dbquery});
        }


        // use getIntColumn for Oracle count data
        if ("oracle".equals(ConfigurationManager.getProperty("db.name")))
        {
            count = Long.valueOf(row.getIntColumn("termcount"));
        }
        else  //getLongColumn works for postgres
        {
            count = Long.valueOf(row.getLongColumn("termcount"));
        }

        return count.intValue();
    }

    public Concept[] getConcepts() throws SQLException
    {
        List<Concept> concepts = new ArrayList<Concept>();

        // Get the table rows
        TableRowIterator tri = DatabaseManager.queryTable(
                ourContext, "concept",
                "SELECT concept.* FROM concept, concept2term WHERE " +
                        "concept2term.concept_id=concept.id " +
                        "AND concept2term.term_id= ? ORDER BY LOWER(concept.identifier)",
                getID());

        // Make Concept objects
        try
        {
            while (tri.hasNext())
            {
                TableRow row = tri.next();

                // First check the cache
                Concept fromCache = (Concept) ourContext.fromCache(
                        Concept.class, row.getIntColumn("id"));

                if (fromCache != null)
                {
                    concepts.add(fromCache);
                }
                else
                {
                    concepts.add(new Concept(ourContext, row));
                }
            }
        }
        finally
        {
            // close the TableRowIterator to free up resources
            if (tri != null)
            {
                tri.close();
            }
        }

        // Put them in an array
        Concept[] conceptArray = new Concept[concepts.size()];
        conceptArray = (Concept[]) concepts.toArray(conceptArray);

        return conceptArray;
    }
}
