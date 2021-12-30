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
package de.tschumacher.bucketservice.service.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;

import de.tschumacher.bucketservice.data.DataCreater;
import de.tschumacher.bucketservice.service.CommonS3ServiceTest;

public class S3DownloadServiceTest extends CommonS3ServiceTest {

  private static final String TEST_FILE = "src/test/resources/test.jpg";

  private S3DownloadService service;

  private String key;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    this.key = DataCreater.createString();
    this.service = new DefaultS3DownloadService(this.amazonS3, this.bucketName);
  }

  @Test
  public void downloadFileTest() throws AmazonClientException, IOException {

    final S3Object s3Object = DataCreater.createTestFileS3Object(TEST_FILE);
    Mockito.when(this.amazonS3.getObject(this.bucketName, this.key)).thenReturn(s3Object);

    final File file = this.service.downloadFile(this.key);

    assertNotNull(file);

    Mockito.verify(this.amazonS3, Mockito.times(1)).getObject(this.bucketName, this.key);
    delete(file);
    s3Object.close();
  }

  @Test
  public void downloadFileWithDirTest() throws AmazonClientException, IOException {
    final String localPath = DataCreater.createString();
    final S3Object s3Object = DataCreater.createTestFileS3Object(TEST_FILE);

    Mockito.when(this.amazonS3.getObject(this.bucketName, this.key)).thenReturn(s3Object);

    final File file = this.service.downloadFile(this.key, localPath);

    assertNotNull(file);

    Mockito.verify(this.amazonS3, Mockito.times(1)).getObject(this.bucketName, this.key);
    delete(file);
    s3Object.close();
  }

  @Test
  public void createPresignedUrlTest() throws AmazonClientException, MalformedURLException {
    final int minutes = DataCreater.createInteger();
    final URL url = DataCreater.createURL();

    Mockito.when(
        this.amazonS3.generatePresignedUrl(ArgumentMatchers.any(GeneratePresignedUrlRequest.class)))
        .thenReturn(url);

    final URL resultUrl = this.service.createPresignedUrl(this.key, minutes);

    assertNotNull(resultUrl);
    assertEquals(url, resultUrl);

    Mockito.verify(this.amazonS3, Mockito.times(1)).generatePresignedUrl(
        ArgumentMatchers.any(GeneratePresignedUrlRequest.class));
  }

  private void delete(final File file) {
    file.delete();
  }
}
