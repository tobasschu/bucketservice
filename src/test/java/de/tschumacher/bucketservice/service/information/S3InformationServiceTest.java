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

import static de.tschumacher.bucketservice.data.S3Assert.assertDirectories;
import static de.tschumacher.bucketservice.data.S3Assert.assertDirectoryRequest;
import static de.tschumacher.bucketservice.data.S3Assert.assertFileListRequest;
import static de.tschumacher.bucketservice.data.S3Assert.assertFileNames;
import static de.tschumacher.bucketservice.data.S3Assert.assertFiles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

import de.tschumacher.bucketservice.data.DataCreater;
import de.tschumacher.bucketservice.domain.S3File;
import de.tschumacher.bucketservice.service.CommonS3ServiceTest;

public class S3InformationServiceTest extends CommonS3ServiceTest {

  private S3InformationService service;
  private String key;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    this.key = DataCreater.createString();
    this.service = new DefaultS3InformationService(this.amazonS3, this.bucketName);
  }

  @Test
  public void listFilesTest() {
    final ObjectListing objects = DataCreater.createObjectListing();

    Mockito.when(this.amazonS3.listObjects(ArgumentMatchers.any(ListObjectsRequest.class))).thenReturn(
        objects);

    final List<S3File> files = this.service.listFiles(this.key);

    assertNotNull(files);
    assertEquals(objects.getObjectSummaries().size(), files.size());
    assertFiles(objects, files);


    verifyFileRequest();
  }


  @Test
  public void listFileNamesTest() {
    final ObjectListing objects = DataCreater.createObjectListing();

    Mockito.when(this.amazonS3.listObjects(ArgumentMatchers.any(ListObjectsRequest.class))).thenReturn(
        objects);

    final List<String> fileNames = this.service.listFileNames(this.key);

    assertNotNull(fileNames);
    assertEquals(objects.getObjectSummaries().size(), fileNames.size());
    assertFileNames(objects, fileNames);


    verifyFileRequest();
  }

  private void verifyFileRequest() {
    final ArgumentCaptor<ListObjectsRequest> listRequest =
        ArgumentCaptor.forClass(ListObjectsRequest.class);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(listRequest.capture());
    assertFileListRequest(listRequest.getValue(), this.bucketName, this.key,
        DefaultS3InformationService.DELIMITER);
  }


  @Test
  public void listDirectoriesTest() {

    final ObjectListing objects = DataCreater.createObjectListing();

    Mockito.when(this.amazonS3.listObjects(ArgumentMatchers.any(ListObjectsRequest.class))).thenReturn(
        objects);

    final List<String> directories = this.service.listDirectories(this.key);

    assertNotNull(directories);
    assertEquals(objects.getCommonPrefixes().size(), directories.size());
    assertDirectories(objects, directories);

    verifyDirectoryRequest();
  }

  private void verifyDirectoryRequest() {
    final ArgumentCaptor<ListObjectsRequest> listRequest =
        ArgumentCaptor.forClass(ListObjectsRequest.class);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(listRequest.capture());
    assertDirectoryRequest(listRequest.getValue(), this.bucketName, this.key,
        DefaultS3InformationService.DELIMITER);
  }

  @Test
  public void fileExistsFalseTest() {
    final ObjectListing listing = DataCreater.createEmptyObjectListing();
    Mockito.when(this.amazonS3.listObjects(this.bucketName, this.key)).thenReturn(listing);

    final boolean fileExists = this.service.fileExists(this.key);

    assertFalse(fileExists);

    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(this.bucketName, this.key);
  }

  @Test
  public void fileExistsTest() {

    final ObjectListing listing = DataCreater.createObjectListing();
    Mockito.when(this.amazonS3.listObjects(this.bucketName, this.key)).thenReturn(listing);

    final boolean fileExists = this.service.fileExists(this.key);

    assertTrue(fileExists);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(this.bucketName, this.key);
  }


}
