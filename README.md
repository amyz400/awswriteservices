# aws-write-services

This project creates a test environment that simulates an Amazon SQS queue and an Amazon S3 bucket.  This configuration is useful for component tests and other manual testing without the overhead of creating a true Amazon SQS queue.  The SQS queue is backed by a JMS queue implement by the ElasticMq library.

SQS Queue - http://localhost:8282
S3 Bucket - http://localhost:8181
Bucket name - document-s3-bucket

GET http://localhost:8080/startDataLoading/{iterations} will load all 5 files into the s3 bucket and add a SendMessageBatchRequest with all 5 document ids to the SQS queue.  If iterations = 1, 1 grouping on 5 documents will be added.  If iterations = 5, 5 grouping on 5 documents will be added.
