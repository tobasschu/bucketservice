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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.CopyObjectRequest;

import de.tschumacher.bucketservice.data.DataCreater;
import de.tschumacher.bucketservice.service.CommonS3ServiceTest;

public class S3ModificationServiceTest extends CommonS3ServiceTest {

  private S3ModificationService service;
  private String key;


  @Override
  @Before
  public void setUp() {
    super.setUp();
    this.key = DataCreater.createString();
    this.service = new DefaultS3ModificationService(this.amazonS3, this.bucketName);
  }


  @Test
  public void deleteFileTest() throws AmazonClientException, IOException {

    this.service.deleteFile(this.key);

    Mockito.verify(this.amazonS3, Mockito.times(1)).deleteObject(this.bucketName, this.key);
  }

  @Test
  public void moveFileTest() throws AmazonClientException, IOException {
    final String destinationKey = DataCreater.createString();

    this.service.moveFile(this.key, destinationKey);

    Mockito.verify(this.amazonS3, Mockito.times(1)).deleteObject(this.bucketName, this.key);


    verifyCopyRequest(destinationKey);
  }


  private void verifyCopyRequest(final String destinationKey) {
    final ArgumentCaptor<CopyObjectRequest> copyRequest =
        ArgumentCaptor.forClass(CopyObjectRequest.class);
    Mockito.verify(this.amazonS3, Mockito.times(1)).copyObject(copyRequest.capture());
    assertObjectRequest(destinationKey, copyRequest.getValue());
  }


  private void assertObjectRequest(final String destinationKey,
      final CopyObjectRequest objectRequest) {
    assertEquals(this.bucketName, objectRequest.getSourceBucketName());
    assertEquals(this.key, objectRequest.getSourceKey());
    assertEquals(this.bucketName, objectRequest.getSourceBucketName());
    assertEquals(destinationKey, objectRequest.getDestinationKey());
  }
}
