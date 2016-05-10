/*
 * Copyright 2015 Tobias Schumacher
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.tschumacher.bucketservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.tschumacher.utils.FilePathUtils;

public class DefaultAmazonS3Service implements AmazonS3Service {
  private static final String DELIMITER = "/";
  private static final Regions DEFAULT_REGION = Regions.EU_CENTRAL_1;
  private final AmazonS3 amazonS3;
  private final String bucket;

  public DefaultAmazonS3Service(final AmazonS3 amazonS3, final String bucket) {
    super();
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }


  public DefaultAmazonS3Service(final String bucket, final String accessKey,
      final String secretKey, Regions region) {
    this(createAmazonS3Service(accessKey, secretKey, region), bucket);
  }


  public DefaultAmazonS3Service(final String bucket, final String accessKey, final String secretKey) {
    this(bucket, accessKey, secretKey, DEFAULT_REGION);
  }

  private static AmazonS3 createAmazonS3Service(final String accessKey, final String secretKey,
      Regions region) {
    final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    AmazonS3 amazonS3 = new AmazonS3Client(credentials);
    amazonS3.setRegion(com.amazonaws.regions.Region.getRegion(region));
    return amazonS3;
  }

  @Override
  public void uploadPublicFile(File file, String key) {
    AccessControlList access = getBucketAcl();
    access.grantPermission(GroupGrantee.AllUsers, Permission.Read);
    this.uploadFile(file, key, access);
  }

  @Override
  public void uploadFile(final File file, final String key) {
    this.uploadFile(file, key, null);
  }


  @Override
  public boolean fileExists(final String key) {
    return this.amazonS3.listObjects(this.bucket, key).getObjectSummaries().size() > 0;
  }


  @Override
  public File downloadFile(final String key) throws FileNotFoundException, IOException {
    final S3Object object = this.amazonS3.getObject(this.bucket, key);
    final File file = new File(FilePathUtils.extractFileName(key));
    IOUtils.copy(object.getObjectContent(), new FileOutputStream(file));
    return file;
  }

  @Override
  public void deleteFile(final String key) {
    if (fileExists(key)) {
      this.amazonS3.deleteObject(this.bucket, key);
    }
  }

  @Override
  public void moveFile(final String sourceKey, final String destinationKey) {
    CopyObjectRequest copyObjectRequest =
        new CopyObjectRequest(this.bucket, sourceKey, this.bucket, destinationKey);
    amazonS3.copyObject(copyObjectRequest);
    deleteFile(sourceKey);
  }

  @Override
  public List<String> listDirectories(final String path) {
    ListObjectsRequest listObjectsRequest =
        new ListObjectsRequest().withBucketName(this.bucket).withPrefix(path)
            .withDelimiter(DELIMITER);
    ObjectListing objects = amazonS3.listObjects(listObjectsRequest);
    return objects.getCommonPrefixes();
  }

  @Override
  public List<String> listFiles(final String path) {
    ListObjectsRequest listObjectsRequest =
        new ListObjectsRequest().withBucketName(this.bucket).withPrefix(path).withMarker(path)
            .withDelimiter(DELIMITER);
    ObjectListing objects = amazonS3.listObjects(listObjectsRequest);
    return createFileList(objects.getObjectSummaries());
  }



  private List<String> createFileList(List<S3ObjectSummary> objectSummaries) {
    List<String> fileList = new ArrayList<String>();
    if (objectSummaries == null)
      return fileList;

    for (S3ObjectSummary summary : objectSummaries) {
      fileList.add(summary.getKey());
    }
    return fileList;
  }


  @Override
  public String createPresignedUrl(final String key, final int minutes) {
    final java.util.Date expiration = new java.util.Date();
    long msec = expiration.getTime();
    msec += 1000 * 60 * minutes;
    expiration.setTime(msec);

    final GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(this.bucket, key);
    generatePresignedUrlRequest.setExpiration(expiration);
    generatePresignedUrlRequest.setMethod(HttpMethod.GET);

    return this.amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
  }


  private void uploadFile(final File file, final String key, AccessControlList access) {
    final PutObjectRequest request = new PutObjectRequest(this.bucket, key, file);
    if (access != null)
      request.setAccessControlList(access);
    this.amazonS3.putObject(request);
  }


  private AccessControlList getBucketAcl() {
    AccessControlList access = this.amazonS3.getBucketAcl(this.bucket);
    if (access == null) {
      access = new AccessControlList();
    }
    return access;
  }



}
