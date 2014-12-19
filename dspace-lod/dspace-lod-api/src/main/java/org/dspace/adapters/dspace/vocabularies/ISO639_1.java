/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.dspace.vocabularies;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.HashSet;

/**
 * User: mini @ atmire . com
 * Date: 3/27/14
 * Time: 3:33 PM
 */
public class ISO639_1 {

    private static final ValueFactory vf = ValueFactoryImpl.getInstance();


    public static final String NAMESPACE = "http://id.loc.gov/vocabulary/iso639-1/";

    public static final URI NS = vf.createURI(NAMESPACE);


    public static final HashSet<URI> URIS = new HashSet<URI>();

    public static URI getURIForString(String code){

        if(code == null || code.length() < 2)
            return null;

        for(URI uri : URIS)
        {
            if(uri.getLocalName().equals(code.substring(0,2))){
                return uri;
            }
        }
        return null;
    }
              
    static {
        URIS.add(vf.createURI(NAMESPACE, "aa"));
        URIS.add(vf.createURI(NAMESPACE, "ab"));
        URIS.add(vf.createURI(NAMESPACE, "af"));
        URIS.add(vf.createURI(NAMESPACE, "ak"));
        URIS.add(vf.createURI(NAMESPACE, "sq"));
        URIS.add(vf.createURI(NAMESPACE, "am"));
        URIS.add(vf.createURI(NAMESPACE, "ar"));
        URIS.add(vf.createURI(NAMESPACE, "an"));
        URIS.add(vf.createURI(NAMESPACE, "hy"));
        URIS.add(vf.createURI(NAMESPACE, "as"));
        URIS.add(vf.createURI(NAMESPACE, "av"));
        URIS.add(vf.createURI(NAMESPACE, "ae"));
        URIS.add(vf.createURI(NAMESPACE, "ay"));
        URIS.add(vf.createURI(NAMESPACE, "az"));
        URIS.add(vf.createURI(NAMESPACE, "ba"));
        URIS.add(vf.createURI(NAMESPACE, "bm"));
        URIS.add(vf.createURI(NAMESPACE, "eu"));
        URIS.add(vf.createURI(NAMESPACE, "be"));
        URIS.add(vf.createURI(NAMESPACE, "bn"));
        URIS.add(vf.createURI(NAMESPACE, "bh"));
        URIS.add(vf.createURI(NAMESPACE, "bi"));
        URIS.add(vf.createURI(NAMESPACE, "bs"));
        URIS.add(vf.createURI(NAMESPACE, "br"));
        URIS.add(vf.createURI(NAMESPACE, "bg"));
        URIS.add(vf.createURI(NAMESPACE, "my"));
        URIS.add(vf.createURI(NAMESPACE, "ca"));
        URIS.add(vf.createURI(NAMESPACE, "ch"));
        URIS.add(vf.createURI(NAMESPACE, "ce"));
        URIS.add(vf.createURI(NAMESPACE, "zh"));
        URIS.add(vf.createURI(NAMESPACE, "cu"));
        URIS.add(vf.createURI(NAMESPACE, "cv"));
        URIS.add(vf.createURI(NAMESPACE, "kw"));
        URIS.add(vf.createURI(NAMESPACE, "co"));
        URIS.add(vf.createURI(NAMESPACE, "cr"));
        URIS.add(vf.createURI(NAMESPACE, "cs"));
        URIS.add(vf.createURI(NAMESPACE, "da"));
        URIS.add(vf.createURI(NAMESPACE, "dv"));
        URIS.add(vf.createURI(NAMESPACE, "nl"));
        URIS.add(vf.createURI(NAMESPACE, "dz"));
        URIS.add(vf.createURI(NAMESPACE, "en"));
        URIS.add(vf.createURI(NAMESPACE, "eo"));
        URIS.add(vf.createURI(NAMESPACE, "et"));
        URIS.add(vf.createURI(NAMESPACE, "ee"));
        URIS.add(vf.createURI(NAMESPACE, "fo"));
        URIS.add(vf.createURI(NAMESPACE, "fj"));
        URIS.add(vf.createURI(NAMESPACE, "fi"));
        URIS.add(vf.createURI(NAMESPACE, "fr"));
        URIS.add(vf.createURI(NAMESPACE, "fy"));
        URIS.add(vf.createURI(NAMESPACE, "ff"));
        URIS.add(vf.createURI(NAMESPACE, "ka"));
        URIS.add(vf.createURI(NAMESPACE, "de"));
        URIS.add(vf.createURI(NAMESPACE, "gd"));
        URIS.add(vf.createURI(NAMESPACE, "ga"));
        URIS.add(vf.createURI(NAMESPACE, "gl"));
        URIS.add(vf.createURI(NAMESPACE, "gv"));
        URIS.add(vf.createURI(NAMESPACE, "el"));
        URIS.add(vf.createURI(NAMESPACE, "gn"));
        URIS.add(vf.createURI(NAMESPACE, "gu"));
        URIS.add(vf.createURI(NAMESPACE, "ht"));
        URIS.add(vf.createURI(NAMESPACE, "ha"));
        URIS.add(vf.createURI(NAMESPACE, "he"));
        URIS.add(vf.createURI(NAMESPACE, "hz"));
        URIS.add(vf.createURI(NAMESPACE, "hi"));
        URIS.add(vf.createURI(NAMESPACE, "ho"));
        URIS.add(vf.createURI(NAMESPACE, "hu"));
        URIS.add(vf.createURI(NAMESPACE, "ig"));
        URIS.add(vf.createURI(NAMESPACE, "is"));
        URIS.add(vf.createURI(NAMESPACE, "io"));
        URIS.add(vf.createURI(NAMESPACE, "ii"));
        URIS.add(vf.createURI(NAMESPACE, "iu"));
        URIS.add(vf.createURI(NAMESPACE, "ie"));
        URIS.add(vf.createURI(NAMESPACE, "ia"));
        URIS.add(vf.createURI(NAMESPACE, "id"));
        URIS.add(vf.createURI(NAMESPACE, "ik"));
        URIS.add(vf.createURI(NAMESPACE, "it"));
        URIS.add(vf.createURI(NAMESPACE, "jv"));
        URIS.add(vf.createURI(NAMESPACE, "ja"));
        URIS.add(vf.createURI(NAMESPACE, "kl"));
        URIS.add(vf.createURI(NAMESPACE, "kn"));
        URIS.add(vf.createURI(NAMESPACE, "ks"));
        URIS.add(vf.createURI(NAMESPACE, "kr"));
        URIS.add(vf.createURI(NAMESPACE, "kk"));
        URIS.add(vf.createURI(NAMESPACE, "km"));
        URIS.add(vf.createURI(NAMESPACE, "ki"));
        URIS.add(vf.createURI(NAMESPACE, "rw"));
        URIS.add(vf.createURI(NAMESPACE, "ky"));
        URIS.add(vf.createURI(NAMESPACE, "kv"));
        URIS.add(vf.createURI(NAMESPACE, "kg"));
        URIS.add(vf.createURI(NAMESPACE, "ko"));
        URIS.add(vf.createURI(NAMESPACE, "kj"));
        URIS.add(vf.createURI(NAMESPACE, "ku"));
        URIS.add(vf.createURI(NAMESPACE, "lo"));
        URIS.add(vf.createURI(NAMESPACE, "la"));
        URIS.add(vf.createURI(NAMESPACE, "lv"));
        URIS.add(vf.createURI(NAMESPACE, "li"));
        URIS.add(vf.createURI(NAMESPACE, "ln"));
        URIS.add(vf.createURI(NAMESPACE, "lt"));
        URIS.add(vf.createURI(NAMESPACE, "lb"));
        URIS.add(vf.createURI(NAMESPACE, "lu"));
        URIS.add(vf.createURI(NAMESPACE, "lg"));
        URIS.add(vf.createURI(NAMESPACE, "mk"));
        URIS.add(vf.createURI(NAMESPACE, "mh"));
        URIS.add(vf.createURI(NAMESPACE, "ml"));
        URIS.add(vf.createURI(NAMESPACE, "mi"));
        URIS.add(vf.createURI(NAMESPACE, "mr"));
        URIS.add(vf.createURI(NAMESPACE, "ms"));
        URIS.add(vf.createURI(NAMESPACE, "mg"));
        URIS.add(vf.createURI(NAMESPACE, "mt"));
        URIS.add(vf.createURI(NAMESPACE, "mn"));
        URIS.add(vf.createURI(NAMESPACE, "na"));
        URIS.add(vf.createURI(NAMESPACE, "nv"));
        URIS.add(vf.createURI(NAMESPACE, "nr"));
        URIS.add(vf.createURI(NAMESPACE, "nd"));
        URIS.add(vf.createURI(NAMESPACE, "ng"));
        URIS.add(vf.createURI(NAMESPACE, "ne"));
        URIS.add(vf.createURI(NAMESPACE, "nn"));
        URIS.add(vf.createURI(NAMESPACE, "nb"));
        URIS.add(vf.createURI(NAMESPACE, "no"));
        URIS.add(vf.createURI(NAMESPACE, "ny"));
        URIS.add(vf.createURI(NAMESPACE, "oc"));
        URIS.add(vf.createURI(NAMESPACE, "oj"));
        URIS.add(vf.createURI(NAMESPACE, "or"));
        URIS.add(vf.createURI(NAMESPACE, "om"));
        URIS.add(vf.createURI(NAMESPACE, "os"));
        URIS.add(vf.createURI(NAMESPACE, "pa"));
        URIS.add(vf.createURI(NAMESPACE, "fa"));
        URIS.add(vf.createURI(NAMESPACE, "pi"));
        URIS.add(vf.createURI(NAMESPACE, "pl"));
        URIS.add(vf.createURI(NAMESPACE, "pt"));
        URIS.add(vf.createURI(NAMESPACE, "ps"));
        URIS.add(vf.createURI(NAMESPACE, "qu"));
        URIS.add(vf.createURI(NAMESPACE, "rm"));
        URIS.add(vf.createURI(NAMESPACE, "ro"));
        URIS.add(vf.createURI(NAMESPACE, "rn"));
        URIS.add(vf.createURI(NAMESPACE, "ru"));
        URIS.add(vf.createURI(NAMESPACE, "sg"));
        URIS.add(vf.createURI(NAMESPACE, "sa"));
        URIS.add(vf.createURI(NAMESPACE, "sr"));
        URIS.add(vf.createURI(NAMESPACE, "hr"));
        URIS.add(vf.createURI(NAMESPACE, "si"));
        URIS.add(vf.createURI(NAMESPACE, "sk"));
        URIS.add(vf.createURI(NAMESPACE, "sl"));
        URIS.add(vf.createURI(NAMESPACE, "se"));
        URIS.add(vf.createURI(NAMESPACE, "sm"));
        URIS.add(vf.createURI(NAMESPACE, "sn"));
        URIS.add(vf.createURI(NAMESPACE, "sd"));
        URIS.add(vf.createURI(NAMESPACE, "so"));
        URIS.add(vf.createURI(NAMESPACE, "st"));
        URIS.add(vf.createURI(NAMESPACE, "es"));
        URIS.add(vf.createURI(NAMESPACE, "sc"));
        URIS.add(vf.createURI(NAMESPACE, "ss"));
        URIS.add(vf.createURI(NAMESPACE, "su"));
        URIS.add(vf.createURI(NAMESPACE, "sw"));
        URIS.add(vf.createURI(NAMESPACE, "sv"));
        URIS.add(vf.createURI(NAMESPACE, "ty"));
        URIS.add(vf.createURI(NAMESPACE, "ta"));
        URIS.add(vf.createURI(NAMESPACE, "tt"));
        URIS.add(vf.createURI(NAMESPACE, "te"));
        URIS.add(vf.createURI(NAMESPACE, "tg"));
        URIS.add(vf.createURI(NAMESPACE, "tl"));
        URIS.add(vf.createURI(NAMESPACE, "th"));
        URIS.add(vf.createURI(NAMESPACE, "bo"));
        URIS.add(vf.createURI(NAMESPACE, "ti"));
        URIS.add(vf.createURI(NAMESPACE, "to"));
        URIS.add(vf.createURI(NAMESPACE, "tn"));
        URIS.add(vf.createURI(NAMESPACE, "ts"));
        URIS.add(vf.createURI(NAMESPACE, "tk"));
        URIS.add(vf.createURI(NAMESPACE, "tr"));
        URIS.add(vf.createURI(NAMESPACE, "tw"));
        URIS.add(vf.createURI(NAMESPACE, "ug"));
        URIS.add(vf.createURI(NAMESPACE, "uk"));
        URIS.add(vf.createURI(NAMESPACE, "ur"));
        URIS.add(vf.createURI(NAMESPACE, "uz"));
        URIS.add(vf.createURI(NAMESPACE, "ve"));
        URIS.add(vf.createURI(NAMESPACE, "vi"));
        URIS.add(vf.createURI(NAMESPACE, "vo"));
        URIS.add(vf.createURI(NAMESPACE, "cy"));
        URIS.add(vf.createURI(NAMESPACE, "wa"));
        URIS.add(vf.createURI(NAMESPACE, "wo"));
        URIS.add(vf.createURI(NAMESPACE, "xh"));
        URIS.add(vf.createURI(NAMESPACE, "yi"));
        URIS.add(vf.createURI(NAMESPACE, "yo"));
        URIS.add(vf.createURI(NAMESPACE, "za"));
        URIS.add(vf.createURI(NAMESPACE, "zu"));
    }
}
