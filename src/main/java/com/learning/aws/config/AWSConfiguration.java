package com.learning.aws.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {

    @Value("${aws.access_key_id}")
    private String accessKey;

    @Value("${aws.secret_access_key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;


    @Value("${aws.s3.endpointUrl}")
    private String endpointUrl;

    @Value("${aws.temporary.credentials.validity.duration}")
    private String credentialsValidityDuration;

    private static final Integer TEMPORARY_CREDENTIALS_DURATION_DEFAULT = 7200;


    @Bean
    public AmazonS3 s3client()
    {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTPS);
        clientConfiguration.setSignerOverride("S3SignerType");


        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey,secretKey));

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConfiguration).
                withPathStyleAccessEnabled(true).withRegion(Regions.US_EAST_1).build();
        return s3Client;
    }

    @Bean
    public AmazonS3 sessionCredentials()
    {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTPS);
        //clientConfiguration.setSignerOverride("S3SignerType");

        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey,secretKey));

        AWSSecurityTokenService tokenService =
                AWSSecurityTokenServiceClientBuilder.standard().withClientConfiguration(clientConfiguration).
                        withRegion(Regions.US_EAST_1).withCredentials(credentialsProvider).build();


        GetSessionTokenRequest session_token_request = new GetSessionTokenRequest();
        if(this.credentialsValidityDuration == null || this.credentialsValidityDuration.trim().equals("")) {
            session_token_request.setDurationSeconds(TEMPORARY_CREDENTIALS_DURATION_DEFAULT);
        } else {
            session_token_request.setDurationSeconds(Integer.parseInt(this.credentialsValidityDuration));
        }

        GetSessionTokenResult session_token_result =
                tokenService.getSessionToken(session_token_request);
        Credentials session_creds = session_token_result.getCredentials();

        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                session_creds.getAccessKeyId(),
                session_creds.getSecretAccessKey(),
                session_creds.getSessionToken());

        return AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials)).build();

    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

}
