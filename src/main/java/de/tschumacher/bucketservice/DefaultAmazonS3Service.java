/*
   Copyright 2015 Tobias Schumacher

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package de.tschumacher.bucketservice;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import de.tschumacher.utils.FilePathUtils;

public class DefaultAmazonS3Service implements AmazonS3Service {
	private final AmazonS3 amazonS3;
	private final String bucket;

	public DefaultAmazonS3Service(final String bucket, final String accessKey,
			final String secretKey) {
		final AWSCredentials credentials = new BasicAWSCredentials(accessKey,
				secretKey);
		this.amazonS3 = new AmazonS3Client(credentials);
		this.amazonS3.setRegion(com.amazonaws.regions.Region
				.getRegion(Regions.EU_CENTRAL_1));
		this.bucket = bucket;
	}

	public DefaultAmazonS3Service(final AmazonS3 amazonS3, final String bucket) {
		super();
		this.amazonS3 = amazonS3;
		this.bucket = bucket;
	}

	@Override
	public void uploadFile(final File file, final String relativePath) {
		final PutObjectRequest request = new PutObjectRequest(this.bucket,
				relativePath, file);
		AccessControlList access = this.amazonS3.getBucketAcl(this.bucket);
		if (access == null) {
			access = new AccessControlList();
		}
		access.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		request.setAccessControlList(access);
		this.amazonS3.putObject(request);
	}

	@Override
	public boolean fileExists(final String key) {
		return this.amazonS3.listObjects(this.bucket, key).getObjectSummaries()
				.size() > 0;
	}

	@Override
	public String createPresignedUrl(final String key, final int minutes) {
		final java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * minutes;
		expiration.setTime(msec);

		final GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
				this.bucket, key);
		generatePresignedUrlRequest.setExpiration(expiration);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);

		return this.amazonS3.generatePresignedUrl(generatePresignedUrlRequest)
				.toString();
	}

	@Override
	public File getFile(final String key) {
		final S3Object object = this.amazonS3.getObject(this.bucket, key);
		final File file = new File(FilePathUtils.extractFileName(key));
		try {
			IOUtils.copy(object.getObjectContent(), new FileOutputStream(file));
		} catch (final Exception e) {
			return null;
		}
		return file;
	}

	@Override
	public void delete(final String key) {
		if (fileExists(key)) {
			this.amazonS3.deleteObject(this.bucket, key);
		}
	}

}
