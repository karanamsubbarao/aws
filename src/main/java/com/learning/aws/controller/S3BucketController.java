package com.learning.aws.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.learning.aws.common.AWSContentType;
import com.learning.aws.services.S3BucketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/rest/aws/s3")
public class S3BucketController {

    @Autowired
    private S3BucketService s3Service;


    @RequestMapping(value = "Hello world",method = RequestMethod.GET)
    public ResponseEntity<String> helloWorld()
    {
        return  new ResponseEntity<String>("hello world", HttpStatus.OK);
    }

    @RequestMapping(value = "list",method = RequestMethod.GET)
    public ResponseEntity<List<Bucket>> listBuckets()
    {
        List<Bucket> buckets = s3Service.listBuckets();
        return  new ResponseEntity<List<Bucket>>(buckets, HttpStatus.OK);
    }


    @RequestMapping(value = "/bucket/{name}",method = RequestMethod.POST)
    public ResponseEntity<List<Bucket>> createBucket(@PathVariable("name") String name)
    {
        List<Bucket> allBuckets = s3Service.createBucket(name);
        return  new ResponseEntity<List<Bucket>>(allBuckets, HttpStatus.OK);
    }

    @RequestMapping(value = "/bucket/{name}",method = RequestMethod.DELETE)
    public ResponseEntity<List<Bucket>> deleteBucket(@PathVariable("name") String name)
    {
        List<Bucket> allBuckets = s3Service.deleteBucket(name);
        return  new ResponseEntity<List<Bucket>>(allBuckets, HttpStatus.OK);
    }


    @RequestMapping(value = "upload/bucket/{bucket}/file/{file}",method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadObject(@PathVariable("bucket") String bucket,
                                               @RequestPart(value = "file") MultipartFile file)
    {
        String fileUrl = s3Service.uploadFile(bucket, file);
        if(StringUtils.isNotBlank(fileUrl))
        {
            return  new ResponseEntity<String>(fileUrl, HttpStatus.OK);
        }
        return  new ResponseEntity<String>(fileUrl, HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "download/bucket/{bucket}/file/{file}",method = RequestMethod.POST, produces = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<byte[]> uploadObject(@PathVariable("bucket") String bucket,String filename)
    {
        ByteArrayOutputStream downloadInputStream = s3Service.downloadFile(bucket, filename);
        return ResponseEntity.ok()
                .contentType(AWSContentType.contentType(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(downloadInputStream.toByteArray());
    }

    @RequestMapping(value = "purge/bucket/{bucket}/file/{file}",method = RequestMethod.DELETE)
    public ResponseEntity purgeObject(@PathVariable("bucket") String bucket,@PathVariable("file")  String file) {
        s3Service.purgeObject(bucket, file);
        return  new ResponseEntity<>(HttpStatus.OK);
    }


}
