package com.example.hl7project.service;

import com.example.hl7project.dto.BookingInfoDTO;
import com.example.hl7project.dto.SlotDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.Resource;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<SlotDTO> getResourceSlots(String resourceId, LocalDate localDate) {
//        String startTime = "11:00";
//        String endTime = "14:00";
//        int slotDuration = 15; // Slot duration in minutes
        Resource resource = resourceRepository.findByResourceId(resourceId);
        List<SlotDTO> slots = createSlots(resource.getStartTime(), resource.getEndTime(), resource.getSlotInterval(), localDate);
        List<Appointment> appointments = appointmentRepository.findByResourceIdAndLocalDate(resourceId, localDate);
        for (Appointment appointment : appointments) {
            for (SlotDTO slot : slots) {
                Boolean isBooked = isAppointmentTimeIn15MinuteSlot(appointment.getAppointmentDate(), slot);
                if (isBooked) {
                    slot.setIsBooked(true);
                    break;
                }
            }
        }
        return slots;
    }

    private boolean isAppointmentTimeIn15MinuteSlot(LocalDateTime appointmentDate, SlotDTO slot) {
        LocalDateTime slotStart = slot.getStartTime();
        LocalDateTime slotEnd = slot.getEndTime();

        return !appointmentDate.isBefore(slotStart) && !appointmentDate.isAfter(slotEnd);
    }

    private static List<SlotDTO> createSlots(String startTime, String endTime, int slotDuration, LocalDate localDate) {
        List<SlotDTO> slots = new ArrayList<>();
        // Parse start and end times
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // Generate slots
        LocalTime currentTime = start;
        while (currentTime.plusMinutes(slotDuration).isBefore(end) || currentTime.plusMinutes(slotDuration).equals(end)) {
            LocalTime nextTime = currentTime.plusMinutes(slotDuration);
            LocalDateTime startDateTime = localDate.atTime(currentTime);
            LocalDateTime endDateTime = localDate.atTime(nextTime);
            slots.add(new SlotDTO(startDateTime, endDateTime, false));
            currentTime = nextTime;
        }

        return slots;
    }


    public void bookAppointment(BookingInfoDTO bookingInfoDTO) {
        InboundHL7Message inboundHL7Message = new InboundHL7Message();
        bookingInfoDTO.getAppointment();
        bookingInfoDTO.getResource();
    }

    public List<Resource> getResources() {
        List<Resource> resources = resourceRepository.findAll();
        return resources;
    }

    public List<String> getVisitTypesByResourceId(String resourceId) {
        List<String> resourceList = appointmentRepository.findDistinctAppointmentTypesByResourceId(resourceId);
        return resourceList;
    }
}

