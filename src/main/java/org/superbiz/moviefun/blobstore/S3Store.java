package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.apigateway.model.Op;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client s3Client;
    private String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.getContentType());
        s3Client.putObject(new PutObjectRequest(photoStorageBucket, blob.getName(), blob.getInputStream(), objectMetadata));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if(s3Client.doesObjectExist(photoStorageBucket, name)){
            S3Object s3Object = s3Client.getObject(photoStorageBucket, name);
            Blob optionalBlob = new Blob(s3Object.getKey(), s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType());
            return Optional.of(optionalBlob);
        }
        return Optional.empty();
    }

    @Override
    public void deleteAll() {
        s3Client.deleteBucket(photoStorageBucket);
    }
}