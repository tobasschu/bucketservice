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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;

import de.tschumacher.utils.FilePathUtils;

public class DefaultS3DownloadService implements S3DownloadService {
  private final AmazonS3 amazonS3;
  private final String bucket;

  public DefaultS3DownloadService(AmazonS3 amazonS3, String bucket) {
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }

  @Override
  public File downloadFile(final String key) throws FileNotFoundException, IOException {
    return downloadFile(key, "");
  }

  @Override
  public File downloadFile(String key, String localPath) throws FileNotFoundException, IOException {
    final S3Object object = getObject(key);
    final File file = createFile(key, localPath);
    IOUtils.copy(object.getObjectContent(), new FileOutputStream(file));
    return file;
  }

  private S3Object getObject(String key) {
    return this.amazonS3.getObject(this.bucket, key);
  }

  @Override
  public URL createPresignedUrl(final String key, final int minutes) {
    final java.util.Date expiration = computeExpiration(minutes);

    final GeneratePresignedUrlRequest generatePresignedUrlRequest =
        createPresignedRequest(key, expiration);

    return this.amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
  }

  private GeneratePresignedUrlRequest createPresignedRequest(final String key,
      final java.util.Date expiration) {
    final GeneratePresignedUrlRequest presignedUrlRequest =
        new GeneratePresignedUrlRequest(this.bucket, key, HttpMethod.GET);
    presignedUrlRequest.setExpiration(expiration);
    return presignedUrlRequest;
  }

  private java.util.Date computeExpiration(final int minutes) {
    final java.util.Date expiration = new java.util.Date();
    long msec = expiration.getTime();
    msec += 1000 * 60 * minutes;
    expiration.setTime(msec);
    return expiration;
  }


  private File createFile(String key, String path) {
    final File file = new File(path + FilePathUtils.extractFileName(key));
    createMissingDirs(file);
    return file;
  }

  private void createMissingDirs(final File file) {
    if (file.getParentFile() != null && !file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
  }


}
