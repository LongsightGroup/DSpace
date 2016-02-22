package org.dspace.authority.mock;

import org.dspace.authority.AuthoritySource;
import org.dspace.authority.AuthorityValue;
import org.dspace.authority.model.Concept;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.*;

/**
 * User: mini @ atmire . com
 * Date: 3/19/15
 * Time: 11:49 AM
 */
public class MockSource implements AuthoritySource {
    static HashMap<String, MockAuthorityValue> map = new HashMap<String,MockAuthorityValue>();
    static {


        for(int i = 0 ; i < 10 ; i++){
            MockAuthorityValue value = new MockAuthorityValue();
            value.setId("7104ab2683574f4ca6e2c07d9030d70" + i);
            value.setValue("title" + i);
            value.setMock_id("id" + i);
            map.put(value.getValue(), value);
        }

    }
    @Override
    public List<AuthorityValue> queryAuthorities(String text, int max) {
        ArrayList<AuthorityValue> values = new  ArrayList<AuthorityValue>();
        for(Map.Entry<String,MockAuthorityValue> entry : map.entrySet())
        {
               if(entry.getKey().startsWith(text))
               {
                   values.add(entry.getValue());
               }

        }
        return values;
    }

    @Override
    public AuthorityValue queryAuthorityID(String id) {

        for(Map.Entry<String,MockAuthorityValue> entry : map.entrySet())
        {
            if(entry.getValue().getMock_id().startsWith(id))
            {
                return entry.getValue();
            }

        }
        return null;
    }

}
