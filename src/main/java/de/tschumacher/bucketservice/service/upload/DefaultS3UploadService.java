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
package de.tschumacher.bucketservice.service.upload;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class DefaultS3UploadService implements S3UploadService {
  private final AmazonS3 amazonS3;
  private final String bucket;

  public DefaultS3UploadService(AmazonS3 amazonS3, String bucket) {
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }

  @Override
  public void uploadPublicFile(File file, String key) {
    final AccessControlList access = getBucketAcl();
    access.grantPermission(GroupGrantee.AllUsers, Permission.Read);
    this.uploadFile(file, key, access);
  }

  @Override
  public void uploadFile(final File file, final String key) {
    this.uploadFile(file, key, null);
  }


  private void uploadFile(final File file, final String key, AccessControlList access) {
    final PutObjectRequest request = createPutRequest(file, key, access);
    this.amazonS3.putObject(request);
  }

  private PutObjectRequest createPutRequest(final File file, final String key,
      AccessControlList access) {
    final PutObjectRequest request = createPutRequest(file, key);
    if (access != null) {
      request.setAccessControlList(access);
    }
    return request;
  }

  private PutObjectRequest createPutRequest(final File file, final String key) {
    return new PutObjectRequest(this.bucket, key, file);
  }

  private AccessControlList getBucketAcl() {
    AccessControlList access = this.amazonS3.getBucketAcl(this.bucket);
    if (access == null) {
      access = new AccessControlList();
    }
    return access;
  }
}
