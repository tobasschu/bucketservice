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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class AmazonS3ServiceTest {
  private static final String BUCKET_NAME = "bucket";
  private AmazonS3Service service = null;
  private AmazonS3 amazonS3 = null;

  @Before
  public void setUp() {
    this.amazonS3 = Mockito.mock(AmazonS3.class);
    this.service = new DefaultAmazonS3Service(this.amazonS3, BUCKET_NAME);
  }

  @After
  public void afterTest() {
    Mockito.verifyNoMoreInteractions(this.amazonS3);
    new File("src/test/resources/filname_tn.png").delete();
  }

  @Test
  public void uploadPublicFileTest() {
    final File file = Mockito.mock(File.class);
    final String relativePath = "relativePath";

    this.service.uploadPublicFile(file, relativePath);

    Mockito.verify(this.amazonS3, Mockito.times(1)).getBucketAcl(BUCKET_NAME);
    Mockito.verify(this.amazonS3, Mockito.times(1)).putObject(Matchers.any(PutObjectRequest.class));
    Mockito.verifyNoMoreInteractions(file);
  }

  @Test
  public void uploadFileTest() {
    final File file = Mockito.mock(File.class);
    final String relativePath = "relativePath";

    this.service.uploadFile(file, relativePath);

    Mockito.verify(this.amazonS3, Mockito.times(1)).putObject(Matchers.any(PutObjectRequest.class));
    Mockito.verifyNoMoreInteractions(file);
  }

  @Test
  public void fileExistsFalseTest() {
    final String key = "key";
    final ObjectListing listing = new ObjectListing();
    Mockito.when(this.amazonS3.listObjects(BUCKET_NAME, key)).thenReturn(listing);

    final boolean fileExists = this.service.fileExists(key);

    assertFalse(fileExists);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(BUCKET_NAME, key);
  }

  @Test
  public void fileExistsTest() {
    final String key = "key";
    final ObjectListing listing = Mockito.mock(ObjectListing.class);
    final List<S3ObjectSummary> summaries = new ArrayList<S3ObjectSummary>();
    summaries.add(new S3ObjectSummary());
    Mockito.when(listing.getObjectSummaries()).thenReturn(summaries);
    Mockito.when(this.amazonS3.listObjects(BUCKET_NAME, key)).thenReturn(listing);

    final boolean fileExists = this.service.fileExists(key);

    assertTrue(fileExists);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(BUCKET_NAME, key);
  }

  @Test
  public void createPresignedUrlTest() throws AmazonClientException, MalformedURLException {
    final String key = "key";
    final int minutes = 1;
    Mockito.when(
        this.amazonS3.generatePresignedUrl(Matchers.any(GeneratePresignedUrlRequest.class)))
        .thenReturn(new URL("http://www.test.de"));

    final URL url = this.service.createPresignedUrl(key, minutes);
    assertNotNull(url);
    Mockito.verify(this.amazonS3, Mockito.times(1)).generatePresignedUrl(
        Matchers.any(GeneratePresignedUrlRequest.class));
  }

  @Test
  public void downloadFileTest() throws AmazonClientException, IOException {
    final String key = "src/test/resources/key.jpg";
    final S3Object s3Object = new S3Object();
    final FileInputStream inputStream = new FileInputStream("src/test/resources/test.jpg");
    s3Object.setObjectContent(inputStream);
    Mockito.when(this.amazonS3.getObject(BUCKET_NAME, key)).thenReturn(s3Object);

    final File file = this.service.downloadFile(key);
    assertNotNull(file);
    Mockito.verify(this.amazonS3, Mockito.times(1)).getObject(BUCKET_NAME, key);
    file.delete();
  }

  @Test
  public void downloadFileWithDirTest() throws AmazonClientException, IOException {
    final String key = "src/test/resources/key.jpg";
    final S3Object s3Object = new S3Object();
    final FileInputStream inputStream = new FileInputStream("src/test/resources/test.jpg");
    s3Object.setObjectContent(inputStream);
    Mockito.when(this.amazonS3.getObject(BUCKET_NAME, key)).thenReturn(s3Object);

    final File file = this.service.downloadFile(key, "path/");
    assertNotNull(file);
    Mockito.verify(this.amazonS3, Mockito.times(1)).getObject(BUCKET_NAME, key);
    delete(file);

  }

  private void delete(final File file1) {
    file1.delete();
    if (file1.getParentFile().list().length == 0) {
      file1.getParentFile().delete();
    }
  }

  @Test
  public void listFilesTest() throws AmazonClientException, IOException {
    final String key = "src/test/resources/key.jpg";
    final ObjectListing objects = new ObjectListing();
    final S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
    s3ObjectSummary.setKey(key);
    objects.getObjectSummaries().add(s3ObjectSummary);

    Mockito.when(this.amazonS3.listObjects(Matchers.any(ListObjectsRequest.class))).thenReturn(
        objects);

    final List<String> files = this.service.listFiles(key);
    assertNotNull(files);
    assertEquals(1, files.size());
    assertEquals(key, files.get(0));
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(
        Matchers.any(ListObjectsRequest.class));
  }

  @Test
  public void listDirectoriesTest() throws AmazonClientException, IOException {
    final String key = "src/test/resources/key.jpg";
    final ObjectListing objects = new ObjectListing();
    objects.getCommonPrefixes().add(key);

    Mockito.when(this.amazonS3.listObjects(Matchers.any(ListObjectsRequest.class))).thenReturn(
        objects);

    final List<String> files = this.service.listDirectories(key);
    assertNotNull(files);
    assertEquals(1, files.size());
    assertEquals(key, files.get(0));
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(
        Matchers.any(ListObjectsRequest.class));
  }

  @Test
  public void deleteFileTest() throws AmazonClientException, IOException {
    final String key = "src/test/resources/key.jpg";
    final ObjectListing objects = new ObjectListing();
    final S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
    s3ObjectSummary.setKey(key);
    objects.getObjectSummaries().add(s3ObjectSummary);
    Mockito.when(this.amazonS3.listObjects(BUCKET_NAME, key)).thenReturn(objects);

    this.service.deleteFile(key);

    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(BUCKET_NAME, key);
    Mockito.verify(this.amazonS3, Mockito.times(1)).deleteObject(BUCKET_NAME, key);
  }

  @Test
  public void moveFileTest() throws AmazonClientException, IOException {
    final String sourceKey = "src/test/resources/key.jpg";
    final String destinationKey = "src/test/dest/key.jpg";
    final ObjectListing objects = new ObjectListing();
    final S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
    s3ObjectSummary.setKey(sourceKey);
    objects.getObjectSummaries().add(s3ObjectSummary);
    Mockito.when(this.amazonS3.listObjects(BUCKET_NAME, sourceKey)).thenReturn(objects);

    this.service.moveFile(sourceKey, destinationKey);

    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(BUCKET_NAME, sourceKey);
    Mockito.verify(this.amazonS3, Mockito.times(1)).deleteObject(BUCKET_NAME, sourceKey);
    Mockito.verify(this.amazonS3, Mockito.times(1)).copyObject(
        Matchers.any(CopyObjectRequest.class));

  }
}
