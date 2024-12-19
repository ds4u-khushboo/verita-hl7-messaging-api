package com.example.hl7project.controller;

import com.example.hl7project.dto.SlotDTO;
import com.example.hl7project.model.Resource;
import com.example.hl7project.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @RequestMapping("/slots")
    public List<SlotDTO> getResourceSlots(@RequestParam String resourceId, @RequestParam LocalDate localDate) {
        return resourceService.getResourceSlots(resourceId, localDate);
    }

    @RequestMapping("/allResources")
    public List<Resource> getResources() {
        return resourceService.getResources();
    }

    @RequestMapping("/visitTypes")
    public List<String> getResourceSlots(@RequestParam String resourceId) {
        return resourceService.getVisitTypesByResourceId(resourceId);
    }
}
