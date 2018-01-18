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
package de.tschumacher.bucketservice.domain;

import java.util.Date;

public class S3File {
  private String bucketName;
  private String key;
  private String eTag;
  private long size;
  private Date lastModified;


  public String getBucketName() {
    return this.bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getETag() {
    return this.eTag;
  }

  public void setETag(String eTag) {
    this.eTag = eTag;
  }

  public long getSize() {
    return this.size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Date getLastModified() {
    return this.lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  private S3File(Builder builder) {
    this.bucketName = builder.bucketName;
    this.key = builder.key;
    this.eTag = builder.eTag;
    this.size = builder.size;
    this.lastModified = builder.lastModified;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String bucketName;
    private String key;
    private String eTag;
    private long size;
    private Date lastModified;

    public Builder withBucketName(String bucketName) {
      this.bucketName = bucketName;
      return this;
    }

    public Builder withKey(String key) {
      this.key = key;
      return this;
    }

    public Builder withETag(String eTag) {
      this.eTag = eTag;
      return this;
    }

    public Builder withSize(long size) {
      this.size = size;
      return this;
    }

    public Builder withLastModified(Date lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    public S3File build() {
      return new S3File(this);
    }
  }


}
