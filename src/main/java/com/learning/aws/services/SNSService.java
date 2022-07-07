package com.learning.aws.services;

import com.learning.aws.config.SNSConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SNSService {

    @Autowired
    private SNSConfiguration snsConfiguration;

    private SnsClient snsClient;

    @PostConstruct
    private void initializeAmazon() {
        this.snsClient = snsConfiguration.sessionCredentials();
    }


    public String createTopic(String topicName)
    {
        CreateTopicRequest request = CreateTopicRequest.builder()
                .name(topicName)
                .build();
        CreateTopicResponse response = snsClient.createTopic(request);
        return response.toString();
    }

    public int createPhoneSubscription(String topicArn, String phoneNumber) {

        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("sms")
                .endpoint(phoneNumber)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();

        SubscribeResponse result = snsClient.subscribe(request);
        return result.sdkHttpResponse().statusCode();
    }

    public int publishMessageToPhone(String topicArn, String message,String phoneNumber) {

        PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

        PublishResponse result = snsClient.publish(request);
        System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
        return result.sdkHttpResponse().statusCode();
    }

    public int createEmailSubscription(String topicArn, String email) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(email)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();
        SubscribeResponse result = snsClient.subscribe(request);
        return result.sdkHttpResponse().statusCode();
    }

    public int publishEmail(String topicArn,String message, String email) {
        PublishRequest request = PublishRequest.builder()
                .subject(message)
                .message(message)
                .topicArn(topicArn)
                .build();

        PublishResponse result = snsClient.publish(request);
        System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
        return result.sdkHttpResponse().statusCode();
    }
}
