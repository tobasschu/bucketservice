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
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class AmazonS3ServiceTest {
  private AmazonS3Service service = null;
  private AmazonS3 amazonS3 = null;

  @Before
  public void setUp() {
    this.amazonS3 = Mockito.mock(AmazonS3.class);
    this.service = new DefaultAmazonS3Service(this.amazonS3, "bucket");
  }

  @After
  public void afterTest() {
    Mockito.verifyNoMoreInteractions(this.amazonS3);
    new File("src/test/resources/filname_tn.png").delete();
  }

  @Test
  public void uploadFileTest() {
    final File file = Mockito.mock(File.class);
    final String relativePath = "relativePath";

    this.service.uploadFile(file, relativePath);

    Mockito.verify(this.amazonS3, Mockito.times(1)).getBucketAcl(Matchers.anyString());
    Mockito.verify(this.amazonS3, Mockito.times(1)).putObject(Matchers.any(PutObjectRequest.class));
    Mockito.verifyNoMoreInteractions(file);
  }

  @Test
  public void fileExistsFalseTest() {
    final String key = "key";
    final ObjectListing listing = new ObjectListing();
    Mockito.when(this.amazonS3.listObjects(Matchers.anyString(), Matchers.anyString())).thenReturn(
        listing);

    final boolean fileExists = this.service.fileExists(key);

    assertFalse(fileExists);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(Matchers.anyString(),
        Matchers.anyString());
  }

  @Test
  public void fileExistsTest() {
    final String key = "key";
    final ObjectListing listing = Mockito.mock(ObjectListing.class);
    final List<S3ObjectSummary> summaries = new ArrayList<S3ObjectSummary>();
    summaries.add(new S3ObjectSummary());
    Mockito.when(listing.getObjectSummaries()).thenReturn(summaries);
    Mockito.when(this.amazonS3.listObjects(Matchers.anyString(), Matchers.anyString())).thenReturn(
        listing);

    final boolean fileExists = this.service.fileExists(key);

    assertTrue(fileExists);
    Mockito.verify(this.amazonS3, Mockito.times(1)).listObjects(Matchers.anyString(),
        Matchers.anyString());
  }

  @Test
  public void createPresignedUrlTest() throws AmazonClientException, MalformedURLException {
    final String key = "key";
    final int minutes = 1;
    Mockito.when(
        this.amazonS3.generatePresignedUrl(Matchers.any(GeneratePresignedUrlRequest.class)))
        .thenReturn(new URL("http://www.moovin.de"));

    final String url = this.service.createPresignedUrl(key, minutes);
    assertNotNull(url);
    Mockito.verify(this.amazonS3, Mockito.times(1)).generatePresignedUrl(
        Matchers.any(GeneratePresignedUrlRequest.class));
  }

  @Test
  public void getFileTest() throws AmazonClientException, IOException {
    final String key = "src/test/resources/key.jpg";
    final S3Object s3Object = new S3Object();
    final FileInputStream inputStream = new FileInputStream("src/test/resources/test.jpg");
    s3Object.setObjectContent(inputStream);
    Mockito.when(this.amazonS3.getObject(Matchers.anyString(), Matchers.anyString())).thenReturn(
        s3Object);

    final File file = this.service.downloadFile(key);
    assertNotNull(file);
    Mockito.verify(this.amazonS3, Mockito.times(1)).getObject(Matchers.anyString(),
        Matchers.anyString());

  }
}
