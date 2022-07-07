package com.learning.aws.controller;

import com.learning.aws.services.S3BucketService;
import com.learning.aws.services.SNSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/rest/aws/sns")
public class SNSController {

    @Autowired
    private SNSService snsService;

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public ResponseEntity<String> helloWorld()
    {
        return  new ResponseEntity<String>("hello world", HttpStatus.OK);
    }

    @RequestMapping(value = "/topic/{name}",method = RequestMethod.POST)
    public ResponseEntity createTopic(@PathVariable("name") String name)
    {
        String topic = snsService.createTopic(name);
        return ResponseEntity.ok(topic);
    }

    @RequestMapping(value = "/phoneSubscription/topic/{topicName}/phone/{phone}",method = RequestMethod.POST)
    public ResponseEntity createPhoneSubscription(@PathVariable("topicName") String topicName, @PathVariable("phone")  String phone)
    {
        int phoneSubscription = snsService.createPhoneSubscription(topicName, phone);
        return ResponseEntity.ok(phoneSubscription);
    }

    @RequestMapping(value = "/emailSubscription/topic/{topicName}/email/{email}",method = RequestMethod.POST)
    public ResponseEntity createEmailSubscription(@PathVariable("topicName") String topicName, @PathVariable("email")  String email)
    {
        int emailSubscription = snsService.createEmailSubscription(topicName, email);
        return ResponseEntity.ok(emailSubscription);
    }

    @RequestMapping(value = "/publishSMS/topic/{topicName}/message/{message}/phone/{phone}/",method = RequestMethod.POST)
    public ResponseEntity publishMessageToPhone(@PathVariable("topicName") String topicName,  @PathVariable("message")  String message, @PathVariable("phone")  String phone)
    {
        int publishMessage = snsService.publishMessageToPhone(topicName, message,phone);
        return ResponseEntity.ok(publishMessage);
    }


    @RequestMapping(value = "/publishEmail/topic/{topicName}/message/{message}/email/{email}/",method = RequestMethod.POST)
    public ResponseEntity publishMessageToEmail(@PathVariable("topicName") String topicName,  @PathVariable("message")  String message, @PathVariable("email")  String email)
    {
        int publishMessage = snsService.publishEmail(topicName, message,email);
        return ResponseEntity.ok(publishMessage);
    }


}
