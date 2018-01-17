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
package de.tschumacher.bucketservice.service.modification;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;

public class DefaultS3ModificationService implements S3ModificationService {

  private final AmazonS3 amazonS3;
  private final String bucket;

  public DefaultS3ModificationService(AmazonS3 amazonS3, String bucket) {
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }

  @Override
  public void deleteFile(final String key) {
    this.amazonS3.deleteObject(this.bucket, key);
  }

  @Override
  public void moveFile(final String sourceKey, final String destinationKey) {
    final CopyObjectRequest copyObjectRequest = createCopyRequest(sourceKey, destinationKey);
    this.amazonS3.copyObject(copyObjectRequest);
    deleteFile(sourceKey);
  }

  private CopyObjectRequest createCopyRequest(final String sourceKey, final String destinationKey) {
    return new CopyObjectRequest(this.bucket, sourceKey, this.bucket, destinationKey);
  }

}
