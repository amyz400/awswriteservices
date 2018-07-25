package com.amyz.projects.aws.controller;

import com.amyz.projects.aws.service.DataLoadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddToBucketController {

    @Autowired
    private DataLoadingService dataLoadingService;

    @RequestMapping(method = RequestMethod.GET)
    public String healthCheck() {
        return "I am healthy!";
    }

    @RequestMapping(path = "/startDataLoading/{iterations}", method = RequestMethod.POST)
    public void triggerDataLoading(@PathVariable Integer iterations) {
        dataLoadingService.triggerDataLoading(iterations);
    }
}
