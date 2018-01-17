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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

import de.tschumacher.bucketservice.data.DataCreater;
import de.tschumacher.bucketservice.service.CommonS3ServiceTest;

public class S3UploadServiceTest extends CommonS3ServiceTest {

  private S3UploadService service;
  private String key;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    this.key = DataCreater.createString();
    this.service = new DefaultS3UploadService(this.amazonS3, this.bucketName);
  }

  @Test
  public void uploadPublicFileTest() {
    final File file = Mockito.mock(File.class);

    this.service.uploadPublicFile(file, this.key);

    Mockito.verify(this.amazonS3, Mockito.times(1)).getBucketAcl(this.bucketName);

    verifyPublicPutRequest(file);
    Mockito.verifyNoMoreInteractions(file);
  }


  @Test
  public void uploadFileTest() {
    final File file = Mockito.mock(File.class);

    this.service.uploadFile(file, this.key);

    verifyPrivatePutRequest(file);
    Mockito.verifyNoMoreInteractions(file);
  }



  private void verifyPrivatePutRequest(File file) {
    final ArgumentCaptor<PutObjectRequest> putRequest =
        ArgumentCaptor.forClass(PutObjectRequest.class);
    verifyPutRequest(file, putRequest);
    assertPutRequestPrivateAccess(putRequest.getValue());
  }

  private void assertPutRequestPrivateAccess(PutObjectRequest putRequest) {
    assertNull(putRequest.getAccessControlList());

  }

  private void verifyPublicPutRequest(File file) {
    final ArgumentCaptor<PutObjectRequest> putRequest =
        ArgumentCaptor.forClass(PutObjectRequest.class);
    verifyPutRequest(file, putRequest);
    assertPutRequestPublicAccess(putRequest.getValue());
  }


  private void assertPutRequestPublicAccess(PutObjectRequest putRequest) {
    assertNotNull(putRequest.getAccessControlList());
    assertEquals(GroupGrantee.AllUsers, putRequest.getAccessControlList().getGrantsAsList().get(0)
        .getGrantee());
    assertEquals(Permission.Read, putRequest.getAccessControlList().getGrantsAsList().get(0)
        .getPermission());
  }

  private void assertPutRequest(PutObjectRequest putRequest, File file) {
    assertEquals(this.bucketName, putRequest.getBucketName());
    assertEquals(this.key, putRequest.getKey());
    assertEquals(file, putRequest.getFile());
  }

  private void verifyPutRequest(File file, final ArgumentCaptor<PutObjectRequest> putRequest) {
    Mockito.verify(this.amazonS3, Mockito.times(1)).putObject(putRequest.capture());
    assertPutRequest(putRequest.getValue(), file);
  }
}
