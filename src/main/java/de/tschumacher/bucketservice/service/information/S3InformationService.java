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

import java.util.List;

import de.tschumacher.bucketservice.domain.S3File;

public interface S3InformationService {

  boolean fileExists(String key);

  List<String> listFileNames(String path);

  List<S3File> listFiles(String path);

  List<String> listDirectories(String path);

}
