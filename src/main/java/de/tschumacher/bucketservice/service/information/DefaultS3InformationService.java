/*
 * Copyright 2017 Tobias Schumacher
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
package de.tschumacher.bucketservice.service.information;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.tschumacher.bucketservice.domain.S3File;

public class DefaultS3InformationService implements S3InformationService {

  public static final String DELIMITER = "/";
  private final AmazonS3 amazonS3;
  private final String bucket;

  public DefaultS3InformationService(AmazonS3 amazonS3, String bucket) {
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }


  @Override
  public boolean fileExists(final String key) {
    return this.amazonS3.listObjects(this.bucket, key).getObjectSummaries().size() > 0;
  }


  @Override
  public List<String> listDirectories(final String path) {
    final ListObjectsRequest listObjectsRequest = createDirectoriesListRequest(path);
    final ObjectListing objects = this.amazonS3.listObjects(listObjectsRequest);
    return objects.getCommonPrefixes();
  }



  @Override
  public List<String> listFileNames(final String path) {
    final ObjectListing objects = listS3Files(path);
    return createFileNameList(objects.getObjectSummaries());
  }


  @Override
  public List<S3File> listFiles(String path) {
    final ObjectListing objects = listS3Files(path);
    return createFileList(objects.getObjectSummaries());
  }

  private List<S3File> createFileList(List<S3ObjectSummary> objectSummaries) {
    final List<S3File> fileList = new ArrayList<S3File>();
    if (objectSummaries == null)
      return fileList;

    for (final S3ObjectSummary summary : objectSummaries) {
      fileList.add(createS3File(summary));
    }
    return fileList;
  }


  private S3File createS3File(S3ObjectSummary summary) {
    return S3File.newBuilder().withBucketName(summary.getBucketName()).withETag(summary.getETag())
        .withKey(summary.getKey()).withLastModified(summary.getLastModified())
        .withSize(summary.getSize()).build();
  }


  private ObjectListing listS3Files(final String path) {
    final ListObjectsRequest listObjectsRequest = createListObjectsRequest(path);
    final ObjectListing objects = this.amazonS3.listObjects(listObjectsRequest);
    return objects;
  }


  private ListObjectsRequest createListObjectsRequest(final String path) {
    return new ListObjectsRequest().withBucketName(this.bucket).withPrefix(path).withMarker(path)
        .withDelimiter(DELIMITER);
  }



  private List<String> createFileNameList(List<S3ObjectSummary> objectSummaries) {
    final List<String> fileList = new ArrayList<String>();
    if (objectSummaries == null)
      return fileList;

    for (final S3ObjectSummary summary : objectSummaries) {
      fileList.add(summary.getKey());
    }
    return fileList;
  }


  private ListObjectsRequest createDirectoriesListRequest(final String path) {
    return new ListObjectsRequest().withBucketName(this.bucket).withPrefix(path)
        .withDelimiter(DELIMITER);
  }

}
