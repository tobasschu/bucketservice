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
package de.tschumacher.bucketservice.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.tschumacher.bucketservice.domain.S3File;

public class S3Assert {

  public static void assertFiles(ObjectListing objects, List<S3File> files) {
    for (int i = 0; i < files.size(); i++) {
      assertFile(objects.getObjectSummaries().get(i), files.get(i));
    }
  }

  public static void assertFile(S3ObjectSummary s3ObjectSummary, S3File s3File) {
    assertEquals(s3ObjectSummary.getBucketName(), s3File.getBucketName());
    assertEquals(s3ObjectSummary.getETag(), s3File.getETag());
    assertEquals(s3ObjectSummary.getKey(), s3File.getKey());
    assertEquals(s3ObjectSummary.getSize(), s3File.getSize());
    assertEquals(s3ObjectSummary.getLastModified(), s3File.getLastModified());
  }

  public static void assertFileListRequest(ListObjectsRequest listRequest, String bucketName,
      String key, String delimiter) {
    assertEquals(bucketName, listRequest.getBucketName());
    assertEquals(key, listRequest.getPrefix());
    assertEquals(key, listRequest.getMarker());
    assertEquals(delimiter, listRequest.getDelimiter());
  }

  public static void assertFileNames(ObjectListing objects, List<String> fileNames) {
    for (int i = 0; i < fileNames.size(); i++) {
      assertEquals(objects.getObjectSummaries().get(i).getKey(), fileNames.get(i));
    }
  }

  public static void assertDirectoryRequest(ListObjectsRequest listRequest, String bucketName,
      String key, String delimiter) {
    assertEquals(bucketName, listRequest.getBucketName());
    assertEquals(key, listRequest.getPrefix());
    assertEquals(delimiter, listRequest.getDelimiter());
  }

  public static void assertDirectories(ObjectListing objects, List<String> directories) {
    for (int i = 0; i < directories.size(); i++) {
      assertEquals(objects.getCommonPrefixes().get(i), directories.get(i));
    }
  }

}
