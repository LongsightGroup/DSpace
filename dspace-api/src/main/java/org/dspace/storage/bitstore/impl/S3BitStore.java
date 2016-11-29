/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.storage.bitstore.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.dspace.content.Bitstream;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Utils;
import org.dspace.storage.bitstore.BitStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Asset store using Amazon's Simple Storage Service (S3).
 * S3 is a commercial, web-service accessible, remote storage facility.
 * NB: you must have obtained an account with Amazon to use this store
 * 
 * @author Richard Rodgers, Peter Dietz
 */ 

public class S3BitStore implements BitStore
{
    /** log4j log */
    private static Logger log = Logger.getLogger(S3BitStore.class);
    
    /** Checksum algorithm */
    private static final String CSA_MD5 = "MD5";
    private static final String CSA_ETAG = "etag";
    
    /** container for all the assets */
	private String bucketName = null;

    /** (Optional) subfolder within bucket where objects are stored */
    private String subfolder = null;

    private static AWSCredentials awsCredentials;
	
	/** S3 service */
	private AmazonS3 s3Service = null;
		
	public S3BitStore()
	{
	}
	
	/**
     * Initialize the asset store
     * S3 Requires:
     *  - access key
     *  - secret key
     *  - bucket name
     *  - Region (optional)
     * 
     * @param config
     *        String used to characterize configuration - may be a configuration
     *        value, or the name of a config file containing such values
     */
	public void init(String config) throws IOException
	{
        // load configs
		Properties props = new Properties();
		try
		{
		    props.load(new FileInputStream(config));
		}
		catch(Exception e)
		{
            log.error(e);
			throw new IOException("Exception loading properties. Config: " + config + ", exception: " + e.getMessage());
		}

        // access / secret
        String awsAccessKey = props.getProperty("aws_access_key_id");
        String awsSecretKey = props.getProperty("aws_secret_access_key");
        if(StringUtils.isBlank(awsAccessKey) || StringUtils.isBlank(awsSecretKey)) {
            log.warn("Empty S3 access or secret");
        }

        // init client
        awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        s3Service = new AmazonS3Client(awsCredentials);

        // bucket name
        bucketName = props.getProperty("bucketName");
        if(StringUtils.isEmpty(bucketName)) {
            bucketName = "dspace-asset-" + ConfigurationManager.getProperty("dspace.hostname");
            log.warn("S3 BucketName is not configured, setting default: " + bucketName);
        }

        try {
            if(! s3Service.doesBucketExist(bucketName)) {
                s3Service.createBucket(bucketName);
                log.info("Creating new S3 Bucket: " + bucketName);
            } else {
                log.info("Using existing S3 Bucket: " + bucketName);
            }
        }
        catch (Exception e)
        {
            log.error(e);
            throw new IOException(e);
        }

        // region
        String regionName = props.getProperty("aws_region");
        if(StringUtils.isNotBlank(regionName)) {
            try {
                Regions regions = Regions.fromName(regionName);
                Region region = Region.getRegion(regions);
                s3Service.setRegion(region);
                log.info("S3 Region set to: " + region.getName());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid aws_region");
            }
        }

        //subfolder within bucket
        subfolder = props.getProperty("subfolder");

        log.debug("AWS S3 Assetstore ready to go!");
	}
	
	/**
     * Return an identifier unique to this asset store instance
     * 
     * @return a unique ID
     */
	public String generateId()
	{
        return Utils.generateKey();
	}

	/**
     * Retrieve the bits for the asset with ID. If the asset does not
     * exist, returns null.
     * 
     * @param id
     *            The ID of the asset to retrieve
     * @exception IOException
     *                If a problem occurs while retrieving the bits
     * 
     * @return The stream of bits, or null
     */
	public InputStream get(String id) throws IOException
	{
        String key = getFullKey(id);

        try
        {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            File tempFile = File.createTempFile("s3-disk-copy", "temp");
            tempFile.deleteOnExit();

            TransferManager transferManager = new TransferManager(awsCredentials);
            Download download = transferManager.download(getObjectRequest, tempFile);
            download.waitForCompletion();

            return new DeleteOnCloseFileInputStream(tempFile);
		}
        catch (Exception e) {
            log.error("get(" + key + ")", e);
            throw new IOException(e);
        } finally {

        }
    }
	
    /**
     * Store a stream of bits.
     * 
     * <p>
     * If this method returns successfully, the bits have been stored.
     * If an exception is thrown, the bits have not been stored.
     * </p>
     *
     * @param in
     *            The stream of bits to store
     * @exception IOException
     *             If a problem occurs while storing the bits
     * 
     * @return Map containing technical metadata (size, checksum, etc)
     */
	public Map put(InputStream in, String id) throws IOException
	{
        String key = getFullKey(id);
        //Copy istream to temp file, and send the file, with some metadata
        File scratchFile = File.createTempFile(id, "s3bs");
        try {
            FileUtils.copyInputStreamToFile(in, scratchFile);
            Long contentLength = Long.valueOf(scratchFile.length());
            TransferManager transferManager = new TransferManager(awsCredentials);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, scratchFile);

            // begin async
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();

            //Use ETAG, md5 not available
            Map attrs = new HashMap();
            attrs.put(Bitstream.SIZE_BYTES, contentLength);
            attrs.put(Bitstream.CHECKSUM, uploadResult.getETag());
            attrs.put(Bitstream.CHECKSUM_ALGORITHM, CSA_ETAG);
            log.debug("Upload complete.");

            scratchFile.delete();
            return attrs;

        } catch(Exception e) {
            log.error("put(is, "+key+")", e);
            throw new IOException(e);
        } finally {
            if(scratchFile.exists()) {
                scratchFile.delete();
            }
        }
	}
	
    /**
     * Obtain technical metadata about an asset in the asset store.
     *
     * @param id
     *            The ID of the asset to describe
     * @param attrs
     *            A Map whose keys consist of desired metadata fields
     * 
     * @exception IOException
     *            If a problem occurs while obtaining metadata
     * @return attrs
     *            A Map with key/value pairs of desired metadata
     *            If file not found, then return null
     */
	public Map about(String id, Map attrs) throws IOException
	{
        String key = getFullKey(id);
        try {
            ObjectMetadata objectMetadata = s3Service.getObjectMetadata(bucketName, key);

            if (objectMetadata != null) {
                if (attrs.containsKey(Bitstream.SIZE_BYTES)) {
                    attrs.put(Bitstream.SIZE_BYTES, objectMetadata.getContentLength());
                }
                if (attrs.containsKey(Bitstream.CHECKSUM)) {
                    attrs.put(Bitstream.CHECKSUM, objectMetadata.getETag());
                    attrs.put(Bitstream.CHECKSUM_ALGORITHM, CSA_ETAG);
                }
                if (attrs.containsKey("modified")) {
                    attrs.put("modified", String.valueOf(objectMetadata.getLastModified().getTime()));
                }
                return attrs;
            }
        } catch (AmazonS3Exception e) {
            if(e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return null;
            }
        } catch (Exception e) {
            log.error("about("+key+", attrs)", e);
            throw new IOException(e);
        }
        return null;
	}
	
    /**
     * Remove an asset from the asset store. An irreversible operation.
     *
     * @param id
     *            The ID of the asset to delete
     * @exception IOException
     *             If a problem occurs while removing the asset
     */
	public void remove(String id) throws IOException
	{
        String key = getFullKey(id);
        try {
            s3Service.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("remove("+key+")", e);
            throw new IOException(e);
        }
	}

    /**
     * Utility Method: Prefix the key with a subfolder, if this instance assets are stored within subfolder
     * @param id
     * @return
     */
    public String getFullKey(String id) {
        if(StringUtils.isNotEmpty(subfolder)) {
            return subfolder + "/" + id;
        } else {
            return id;
        }
    }
}
