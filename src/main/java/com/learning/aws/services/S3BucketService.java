package com.learning.aws.services;

import com.learning.aws.config.AWSConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class S3BucketService {

    private Logger logger = LoggerFactory.getLogger(S3BucketService.class);


    @Autowired
    private AWSConfiguration s3Config;

    private S3Client s3client;

    @PostConstruct
    private void initializeAmazon() {
        this.s3client = s3Config.s3client();
    }

    public List<Bucket> listBuckets() {
        return s3client.listBuckets().buckets();
    }

    public List<Bucket> createBucket(String name) {
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket(name).build();
        s3client.createBucket(bucketRequest);
        return listBuckets();
    }


    public List<Bucket> deleteBucket(String name) {
        DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(name).build();
        s3client.deleteBucket(request);
        return listBuckets();
    }

    public String uploadFile(String bucket,MultipartFile multipartFile) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = multipartFile.getOriginalFilename();
            return  uploadFileTos3bucket(bucket,fileName, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String uploadFileTos3bucket(String bucket,String objectKey, File file) {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-amz-meta-myVal", "test");

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .build();

        PutObjectResponse response = s3client.putObject(putObjectRequest,
                RequestBody.fromBytes(getObjectFile(file)));
        logger.info("putObjectResult = " + response);
        final URL reportUrl = s3client.utilities().getUrl(GetUrlRequest.builder().bucket(bucket).key(objectKey).build());
        logger.info("reportUrl = " + reportUrl);
        return reportUrl.toString();
    }

    public String deleteFileFromS3Bucket(String bucket,String objectKey) {

        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder().key(objectKey).build());

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(toDelete).build())
                .build();
        DeleteObjectsResponse deleteObjectsResponse = s3client.deleteObjects(request);
        return deleteObjectsResponse.toString();
    }


    public void  downloadFile(String bucket, String objectKey, Path destination) {

    }

    private static byte[] getObjectFile(File file) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

}
