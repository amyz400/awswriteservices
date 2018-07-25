package com.amyz.projects.aws;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amyz.projects.aws.service.S3Properties;
import com.amyz.projects.aws.service.SqsProperties;
import org.apache.commons.lang3.StringUtils;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import javax.jms.Session;

@EnableJms
@Configuration
@DependsOn("S3StubService")
public class AWSConfiguration {

    private static final String REGION = "us-east-1";

    private final SqsProperties sqsProperties;
    private final S3Properties s3Properties;

    @Autowired
    public AWSConfiguration(SqsProperties sqsProperties, S3Properties s3Properties) {
        this.sqsProperties = sqsProperties;
        this.s3Properties = s3Properties;
        startMq();
    }

    private void startMq() {
        String portAsString = StringUtils.substringAfter(sqsProperties.getEndpoint(), "localhost:");
        int port = Integer.parseInt(portAsString);
        SQSRestServerBuilder.withPort(port).withInterface("localhost").start();
    }

    @PostConstruct
    public void init() {

        if (amazonS3Client() != null) {
            String bucket = s3Properties.getBucketName();
            amazonS3Client().createBucket(bucket);
        }
    }
    @Bean
    public AmazonS3 amazonS3Client() {

        AwsClientBuilder.EndpointConfiguration endpoint =
                new AwsClientBuilder.EndpointConfiguration(s3Properties.getEndpoint(), REGION);

        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();

        return client;
    }

    @Bean
    public AmazonSQS amazonSqsClient() {

        AwsClientBuilder.EndpointConfiguration endpoint =
                new AwsClientBuilder.EndpointConfiguration(sqsProperties.getEndpoint(), REGION);

        AmazonSQS client = AmazonSQSClientBuilder
                .standard()
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();

        client.createQueue(sqsProperties.getQueueName());

        return client;
    }

    @Bean
    public JmsTemplate createJmsTemplate() {

        SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
                .withAWSCredentialsProvider(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .withEndpoint(sqsProperties.getEndpoint())
                .withNumberOfMessagesToPrefetch(10)
                .build();

        JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
        jmsTemplate.setDefaultDestinationName(sqsProperties.getQueueName());
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return jmsTemplate;
    }

}
