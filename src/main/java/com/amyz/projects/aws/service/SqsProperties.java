package com.amyz.projects.aws.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sqs")
@Getter
@Setter
public class SqsProperties {

    private String queueName;
    private Integer maxMessages;
    private String endpoint;
    private Integer testDelay;
    private Integer startupDelay;
}
