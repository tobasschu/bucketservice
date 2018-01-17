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

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.tschumacher.bucketservice.service.download.S3DownloadService;
import de.tschumacher.bucketservice.service.information.S3InformationService;
import de.tschumacher.bucketservice.service.modification.S3ModificationService;
import de.tschumacher.bucketservice.service.upload.S3UploadService;


public class S3ServiceTest extends CommonS3ServiceTest {
  private S3Service service = null;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    this.service = new DefaultS3Service(this.amazonS3, this.bucketName);
  }

  @Test
  public void downloadServiceTest() {
    final S3DownloadService downloadService = this.service.downloadService();

    assertNotNull(downloadService);
  }


  @Test
  public void informationServiceTest() {
    final S3InformationService informationService = this.service.informationService();

    assertNotNull(informationService);
  }

  @Test
  public void modificationServiceTest() {
    final S3ModificationService modificationService = this.service.modificationService();

    assertNotNull(modificationService);
  }

  @Test
  public void uploadServiceTest() {
    final S3UploadService uploadService = this.service.uploadService();

    assertNotNull(uploadService);
  }

}
