package com.amyz.projects.aws.service;

import io.findify.s3mock.S3Mock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service("S3StubService")
public class S3StubService {

    private final S3Properties s3Properties;
    private static S3Mock api;

    @Autowired
    public S3StubService(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    @PostConstruct
    public void start() {
        String portAsString = StringUtils.substringAfter(s3Properties.getEndpoint(), "localhost:");
        int port = Integer.parseInt(portAsString);
        api = S3Mock.create(port);
        api.start();
    }

    @PreDestroy
    public void stop() {
        api.stop();
    }

}
