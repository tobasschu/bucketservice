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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;

public class DataCreater {


  protected static PodamFactory factory = createFactory();

  private static PodamFactoryImpl createFactory() {
    final PodamFactoryImpl podamFactory = new PodamFactoryImpl();
    podamFactory.getStrategy().addOrReplaceTypeManufacturer(URL.class, new URLTypeManufacturer());
    return podamFactory;
  }

  public static String createString() {
    return factory.manufacturePojo(String.class);
  }

  public static S3Object createTestFileS3Object(String testFilePath) throws FileNotFoundException {
    final S3Object s3Object = new S3Object();
    final FileInputStream inputStream = new FileInputStream(testFilePath);
    s3Object.setObjectContent(inputStream);
    return s3Object;
  }

  public static Integer createInteger() {
    return factory.manufacturePojo(Integer.class);
  }

  public static URL createURL() {
    return factory.manufacturePojo(URL.class);
  }

  public static ObjectListing createObjectListing() {
    return factory.manufacturePojo(ObjectListing.class);
  }

  public static ObjectListing createEmptyObjectListing() {
    return new ObjectListing();
  }
}
