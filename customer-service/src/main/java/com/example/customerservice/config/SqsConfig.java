package com.example.customerservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// Marker annotation that tells spring to generate bean definitions at runtime for the methods annotated with @Bean annotation.
@Configuration
@EnableSqs
public class SqsConfig {


    // Value is populated by the region code.
    @Value("${cloud.aws.region.static}")
    private String region;

    // Value is populated with the aws access key.
    @Value("${cloud.aws.credentials.accessKey}")
    private String awsAccessKey;

    // Value is populated with the aws secret key
    @Value("${cloud.aws.credentials.secretKey}")
    private String awsSecretKey;

    // @Bean annotation tells that a method produces a bean that is to be managed by the spring container.
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }

    @Bean
    // @Primary annotation gives a higher preference to a bean (when there are multiple beans of the same type).
    @Primary
    // AmazonSQSAsync is an interface for accessing the SQS asynchronously.
    // Each asynchronous method will return a Java Future object representing the asynchronous operation.
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                .build();
    }
}