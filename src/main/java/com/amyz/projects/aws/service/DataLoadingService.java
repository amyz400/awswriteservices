package com.amyz.projects.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataLoadingService {

    private static final String person1File = "data/person1.json";
    private static final String person2File = "data/person2.json";
    private static final String person3File = "data/person3.json";
    private static final String person4File = "data/person4.json";
    private static final String person5File = "data/person5.json";

    private static final String person1Id = "person1";
    private static final String person2Id = "person2";
    private static final String person3Id = "person3";
    private static final String person4Id = "person4";
    private static final String person5Id = "person5";

    private static final String record = "{\"Records\":[{\"s3\":{\"key\": \"--ID--\"}}]}";

    private final SqsProperties sqsProperties;
    private final S3Properties s3Properties;

    private final AmazonSQS amazonSqsClient;
    private final AmazonS3 amazonS3Client;

    @Autowired
    public DataLoadingService(SqsProperties sqsProperties, AmazonSQS amazonSqsClient,
                              S3Properties s3Properties, AmazonS3 s3Client) {
        this.sqsProperties = sqsProperties;
        this.amazonSqsClient = amazonSqsClient;
        this.s3Properties = s3Properties;
        this.amazonS3Client = s3Client;
    }

    public void triggerDataLoading(Integer iterations) {

        addToS3Bucket(person1Id, person1File);
        addToS3Bucket(person2Id, person2File);
        addToS3Bucket(person3Id, person3File);
        addToS3Bucket(person4Id, person4File);
        addToS3Bucket(person5Id, person5File);


        List<SendMessageBatchRequestEntry> messageList = new ArrayList<>();
        messageList.add(createBatchRequestEntry("test1", person1Id));
        messageList.add(createBatchRequestEntry("test2", person2Id));
        messageList.add(createBatchRequestEntry("test3", person3Id));
        messageList.add(createBatchRequestEntry("test4", person4Id));
        messageList.add(createBatchRequestEntry("test5", person5Id));
        String queueUrl = sqsProperties.getEndpoint();
        String queueName = sqsProperties.getQueueName();
        SendMessageBatchRequest request = new SendMessageBatchRequest(queueUrl + "/queue/" + queueName, messageList);

        // Lambda Runnable
        Runnable dataloadThread = () -> {

            amazonSqsClient.sendMessageBatch(request);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        new Thread(dataloadThread).start();
    }



    private void addToS3Bucket(String personId, String fileName) {

        String fileContent = "";
        try {
            fileContent = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(fileName));
            amazonS3Client.putObject(s3Properties.getBucketName(), personId, fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SendMessageBatchRequestEntry createBatchRequestEntry(String id, String personId) {

        SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry();
        entry.setId(id);
        entry.setMessageBody(new String(record).replace("--ID--", personId));

        return entry;
    }
}
