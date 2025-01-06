package com.example.hl7project.service;

import com.example.hl7project.dto.SlotDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Resource;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<SlotDTO> getResourceSlots(String resourceId, LocalDate localDate) {
        logger.info("Fetching resource slots for resourceId: {} and date: {}", resourceId, localDate);

        Resource resource = resourceRepository.findByResourceId(resourceId);
        if (resource == null) {
            logger.warn("Resource with resourceId: {} not found.", resourceId);
            return new ArrayList<>();
        }

        logger.debug("Resource details retrieved: {}", resource);

        List<SlotDTO> slots = createSlots(resource.getStartTime(), resource.getEndTime(), resource.getSlotInterval(), localDate);
        logger.info("Created {} slots for resourceId: {} and date: {}", slots.size(), resourceId, localDate);

        List<Appointment> appointments = appointmentRepository.findByResourceIdAndLocalDate(resourceId, localDate);
        logger.info("Found {} appointments for resourceId: {} and date: {}", appointments.size(), resourceId, localDate);

        for (Appointment appointment : appointments) {
            for (SlotDTO slot : slots) {
                Boolean isBooked = isAppointmentTimeIn15MinuteSlot(appointment.getAppointmentDate(), slot);
                if (isBooked) {
                    slot.setIsBooked(true);
                    logger.debug("Slot booked for appointment: {}", appointment);
                    break;
                }
            }
        }

        logger.info("Returning {} slots for resourceId: {} and date: {}", slots.size(), resourceId, localDate);
        return slots;
    }

    private boolean isAppointmentTimeIn15MinuteSlot(LocalDateTime appointmentDate, SlotDTO slot) {
        LocalDateTime slotStart = slot.getStartTime();
        LocalDateTime slotEnd = slot.getEndTime();

        boolean result = !appointmentDate.isBefore(slotStart) && !appointmentDate.isAfter(slotEnd);
        logger.debug("Checking if appointment time {} falls within slot ({}, {}): {}", appointmentDate, slotStart, slotEnd, result);
        return result;
    }

    private static List<SlotDTO> createSlots(String startTime, String endTime, int slotDuration, LocalDate localDate) {
        logger.info("Creating slots from {} to {} with duration {} minutes for date: {}", startTime, endTime, slotDuration, localDate);

        List<SlotDTO> slots = new ArrayList<>();
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        LocalTime currentTime = start;
        while (currentTime.plusMinutes(slotDuration).isBefore(end) || currentTime.plusMinutes(slotDuration).equals(end)) {
            LocalTime nextTime = currentTime.plusMinutes(slotDuration);
            LocalDateTime startDateTime = localDate.atTime(currentTime);
            LocalDateTime endDateTime = localDate.atTime(nextTime);
            slots.add(new SlotDTO(startDateTime, endDateTime, false));
            logger.debug("Created slot: {} - {}", startDateTime, endDateTime);
            currentTime = nextTime;
        }

        logger.info("Total slots created: {}", slots.size());
        return slots;
    }

    public List<Resource> getResources() {
        logger.info("Fetching all resources.");

        List<Resource> resources = resourceRepository.findAll();
        logger.info("Found {} resources.", resources.size());
        return resources;
    }

    public List<String> getVisitTypesByResourceId(String resourceId) {
        logger.info("Fetching visit types for resourceId: {}", resourceId);

        List<String> resourceList = appointmentRepository.findDistinctAppointmentTypesByResourceId(resourceId);
        logger.info("Found {} distinct visit types for resourceId: {}", resourceList.size(), resourceId);
        return resourceList;
    }
}
