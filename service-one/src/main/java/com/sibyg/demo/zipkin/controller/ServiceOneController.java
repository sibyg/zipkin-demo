package com.sibyg.demo.zipkin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServiceOneController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOneController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/serviceOne")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getSleuthTest(){
        LOGGER.info("I'm here in service calling service two");
        String response = restTemplate.getForObject("http://localhost:8081/serviceTwo", String.class);
        return new ResponseEntity<String>(response, HttpStatus.OK);

    }

}