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

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.amazonaws.services.s3.AmazonS3;

import de.tschumacher.bucketservice.data.DataCreater;

public class CommonS3ServiceTest {
  protected AmazonS3 amazonS3 = null;
  protected String bucketName;

  @Before
  public void setUp() {
    this.amazonS3 = Mockito.mock(AmazonS3.class);
    this.bucketName = DataCreater.createString();
  }

  @After
  public void afterTest() {
    Mockito.verifyNoMoreInteractions(this.amazonS3);
  }
}
