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
package de.tschumacher.bucketservice.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import de.tschumacher.bucketservice.service.download.DefaultS3DownloadService;
import de.tschumacher.bucketservice.service.download.S3DownloadService;
import de.tschumacher.bucketservice.service.information.DefaultS3InformationService;
import de.tschumacher.bucketservice.service.information.S3InformationService;
import de.tschumacher.bucketservice.service.modification.DefaultS3ModificationService;
import de.tschumacher.bucketservice.service.modification.S3ModificationService;
import de.tschumacher.bucketservice.service.upload.DefaultS3UploadService;
import de.tschumacher.bucketservice.service.upload.S3UploadService;

public class DefaultS3Service implements S3Service {
  private static final Regions DEFAULT_REGION = Regions.EU_CENTRAL_1;

  private final S3UploadService s3UploadService;
  private final S3ModificationService s3ModificationService;
  private final S3InformationService s3InformationService;
  private final S3DownloadService s3DownloadService;

  public DefaultS3Service(final AmazonS3 amazonS3, final String bucket) {
    super();
    this.s3UploadService = new DefaultS3UploadService(amazonS3, bucket);
    this.s3DownloadService = new DefaultS3DownloadService(amazonS3, bucket);
    this.s3InformationService = new DefaultS3InformationService(amazonS3, bucket);
    this.s3ModificationService = new DefaultS3ModificationService(amazonS3, bucket);
  }


  public DefaultS3Service(final String bucket, final String accessKey, final String secretKey,
      Regions region) {
    this(createAmazonS3Service(accessKey, secretKey, region), bucket);
  }


  public DefaultS3Service(final String bucket, final String accessKey, final String secretKey) {
    this(bucket, accessKey, secretKey, DEFAULT_REGION);
  }

  private static AmazonS3 createAmazonS3Service(final String accessKey, final String secretKey,
      Regions region) {
    final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    final AWSCredentialsProvider credentialsProvider =
        new AWSStaticCredentialsProvider(credentials);
    final AmazonS3 amazonS3 =
        AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region)
            .build();
    return amazonS3;
  }



  @Override
  public S3UploadService uploadService() {
    return this.s3UploadService;
  }


  @Override
  public S3DownloadService downloadService() {
    return this.s3DownloadService;
  }


  @Override
  public S3InformationService informationService() {
    return this.s3InformationService;
  }


  @Override
  public S3ModificationService modificationService() {
    return this.s3ModificationService;
  }



}
