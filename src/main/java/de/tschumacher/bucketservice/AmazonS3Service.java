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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface AmazonS3Service {

  void uploadPublicFile(File file, String key);

  void uploadFile(File file, String path);

  File downloadFile(String key, String path) throws FileNotFoundException, IOException;

  File downloadFile(String key) throws FileNotFoundException, IOException;

  boolean fileExists(String key);

  void deleteFile(String key);

  String createPresignedUrl(String key, int minutes);

  List<String> listFiles(String path);

  List<String> listDirectories(String path);

  void moveFile(String sourceKey, String destinationKey);


}
