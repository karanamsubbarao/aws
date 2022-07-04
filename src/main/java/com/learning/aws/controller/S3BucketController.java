package com.learning.aws.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.learning.aws.config.S3Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/aws/s3")
public class S3BucketController {

    @Autowired
    private S3Config s3Config;


    @RequestMapping(value = "Hello world",method = RequestMethod.GET)
    public ResponseEntity<String> helloWorld()
    {
        return  new ResponseEntity<String>("hello world", HttpStatus.OK);
    }

    @RequestMapping(value = "list",method = RequestMethod.GET)
    public ResponseEntity<List<Bucket>> listBuckets()
    {
        List<Bucket> buckets =
                s3Config.sessionCredentials().listBuckets();
        return  new ResponseEntity<List<Bucket>>(buckets, HttpStatus.OK);
    }


    @RequestMapping(value = "create",method = RequestMethod.POST)
    public ResponseEntity<List<Bucket>> createBucket()
    {
        List<Bucket> buckets =
                s3Config.sessionCredentials().listBuckets();
        return  new ResponseEntity<List<Bucket>>(buckets, HttpStatus.OK);
    }

}
