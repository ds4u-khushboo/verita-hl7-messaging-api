package com.example.hl7project.controller;

import com.example.hl7project.service.ADTService;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hl7")
public class ADTController {

    @Autowired
    private ADTService adtService;

    @PostMapping("/ADT")
    public Message getAdtMessage(@RequestBody String adtMessage) {
        return adtService.processMessage(adtMessage);
    }

}
