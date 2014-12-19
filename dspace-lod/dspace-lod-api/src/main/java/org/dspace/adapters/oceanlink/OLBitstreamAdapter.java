/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.adapters.oceanlink;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.dspace.adapters.dspace.vocabularies.VOID;
import org.dspace.adapters.oceanlink.vocabularies.File;
import org.dspace.adapters.AbstractDSpaceEventAdapter;
import org.dspace.adapters.dspace.vocabularies.DC;
import org.dspace.adapters.dspace.vocabularies.DCTERMS;
import org.dspace.adapters.dspace.vocabularies.DS;
import org.dspace.adapters.oceanlink.vocabularies.RepositoryObject;
import org.dspace.content.*;
import org.dspace.core.Context;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * OceanLink Bitstream Adapter Provides conversion from DSpace Bitstream to OceanLink RDF model.
 *
 * @author Mini Pillai (minipillai at atmire.com)
 * @author Mark Diggory (mdiggory at atmire.com)
 */
public class OLBitstreamAdapter extends AbstractDSpaceEventAdapter {

    private static Logger log = Logger.getLogger(OLBitstreamAdapter.class);

    @Override
    public void install(Context ctx, DSpaceObject subject) throws Exception {
        if(checkPermission(ctx,subject)) {
            this.create(subject);

            this.modifyMetadata(ctx, subject);

            //Relation between item-bitsream instead of bundle-bistream
            this.add(ctx,subject,subject);
        }

    }
    @Override
    protected Resource create(DSpaceObject subject) throws RDFHandlerException, RepositoryException {
        Resource r = null;
        try {
            r = this.createResource((Item)subject.getParentObject(),(Bitstream)subject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        handleStatement(r, RDF.TYPE, File.File);
        return r;
    }


    @Override
    protected void modifyMetadata(Context ctx, DSpaceObject subject) throws Exception {
        Resource aggregation = createResource((Item)subject.getParentObject(),(Bitstream)subject);
        /*
        * =================================================================
        * REMOVE RDF with context as DS.DescriptiveMetadata
        * =================================================================
        */

        provider.getConnection().remove(aggregation,null,null,DS.DescriptiveMetadata);

        /*
        * =================================================================
        * ADDING RDF for bitstream metadata
        * =================================================================
        */

        Bitstream bitstream = (Bitstream) subject;

        Item item = null;
        try {
            item = (Item) bitstream.getParentObject();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Resource item_resource = this.createResource(item);

        Resource uri = createResource(item, bitstream);

        /*
        * cache the resources and write afterward so the rdfxml is
        * prettier
        */


        if(bitstream.getName() != null)
        {
            this.getProvider().getConnection().add(valueFactory.createStatement(
                    uri,
                    DC.title_,
                    valueFactory.createLiteral(StringEscapeUtils
                            .escapeXml(bitstream.getName()))),
                    DS.DescriptiveMetadata);
        }

        if(bitstream.getDescription() != null)
        {
            this.getProvider().getConnection().add(valueFactory.createStatement(
                    uri,
                    DC.description_,
                    valueFactory.createLiteral(StringEscapeUtils
                            .escapeXml(bitstream.getDescription()))),
                    DS.DescriptiveMetadata);
        }

        this.getProvider().getConnection().add(valueFactory.createStatement(
                uri,
                DS.size,
                valueFactory.createLiteral(bitstream.getSize())),
                DS.DescriptiveMetadata);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                uri,
                DS.messageDigest,
                valueFactory.createLiteral(StringEscapeUtils
                        .escapeXml(bitstream.getChecksum()))),
                DS.DescriptiveMetadata);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                uri,
                DS.messageDigestAlgorithm,
                valueFactory.createLiteral(StringEscapeUtils
                        .escapeXml(bitstream.getChecksumAlgorithm()))),
                DS.DescriptiveMetadata);


        //Adding bundle information as bitstream property instead of considering Bundle as a class.
        Bundle[] allBundles = bitstream.getBundles();
        Set<String> bundleSet = null;

        for(Bundle bundle : allBundles)
        {
            if(bundleSet == null)
                bundleSet = new HashSet<String>();
            bundleSet.add(bundle.getName());

        }

        // Handling Bistream format

        BitstreamFormat format = bitstream.getFormat();

        Resource fu = createResource(format);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                uri,
                DCTERMS.format_,
                fu),
                DS.DescriptiveMetadata);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                fu,
                RDF.TYPE,
                DS.BitstreamFormat),
                DS.DescriptiveMetadata);

        //this.getConnection().add(valueFactory.createStatement(fu,
        //        RDF.TYPE, DCTERMS.FileFormat),DS.DescriptiveMetadata);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                fu,
                RDF.VALUE,
                valueFactory.createLiteral(StringEscapeUtils
                        .escapeXml(format.getMIMEType()))),DS.DescriptiveMetadata);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                fu,
                DC.description_,
                valueFactory.createLiteral(StringEscapeUtils
                        .escapeXml(format.getDescription()))),DS.DescriptiveMetadata);

        this.getProvider().getConnection().add(valueFactory.createStatement(
                fu,
                DC.title_,
                valueFactory.createLiteral(StringEscapeUtils
                        .escapeXml(format.getShortDescription()))),DS.DescriptiveMetadata);

        String supportLevel = "Unknown";

        if (format.getSupportLevel() == BitstreamFormat.KNOWN)
            supportLevel = "Known";
        else if (format.getSupportLevel() == BitstreamFormat.SUPPORTED)
            supportLevel = "Supported";

        this.getProvider().getConnection().add(valueFactory.createStatement(
                fu,
                DS.support,
                valueFactory.createLiteral(supportLevel)),DS.DescriptiveMetadata);



    }

    @Override
    protected void add(Context ctx, DSpaceObject subject, DSpaceObject object) throws RepositoryException {


    }

    @Override
    public boolean handlerFor(DSpaceObject dso) {
        return dso instanceof Bitstream;
    }

}
