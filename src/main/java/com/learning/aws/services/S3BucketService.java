package com.learning.aws.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.learning.aws.config.AWSConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

@Service
public class S3BucketService {

    private Logger logger = LoggerFactory.getLogger(S3BucketService.class);


    @Autowired
    private AWSConfiguration s3Config;

    private AmazonS3 s3client;

    @PostConstruct
    private void initializeAmazon() {
        this.s3client = s3Config.sessionCredentials();
    }

    public List<Bucket> listBuckets() {
        return s3client.listBuckets();
    }

    public List<Bucket> createBucket(String name) {
        s3client.createBucket(name);
        return s3client.listBuckets();
    }


    public List<Bucket> deleteBucket(String name) {
        s3client.deleteBucket(name);
        return s3client.listBuckets();
    }

    public String uploadFile(String bucket,MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = multipartFile.getOriginalFilename();
            fileUrl = s3Config.getEndpointUrl() + "/" + bucket + "/" + fileName;
            uploadFileTos3bucket(bucket,fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private void uploadFileTos3bucket(String bucket,String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucket, fileName, file));
    }

    public String deleteFileFromS3Bucket(String bucket,String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucket, fileName));
        return "Successfully deleted";
    }


    public ByteArrayOutputStream  downloadFile(String bucket, String file) {
        try
        {
            S3Object s3object = s3client.getObject(new GetObjectRequest(bucket, file));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream;
        } catch (IOException ioException) {
            logger.error("IOException: " + ioException.getMessage());
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException Message:    " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }
        return null;
    }


    public void  purgeObject(String bucket, String file) {
        try
        {
            s3client.deleteObject(bucket, file);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
