# Task List

## 1. Logger Implementation
Implement logging functionality across the system to track events, errors, and system activity.

## 2. Mapping Using DTO from JPA Query in Appointment Repository
Map data from the JPA queries to DTO (Data Transfer Object) for appointment-related data.

## 3. Refactor Types in All Objects
Ensure consistency in data types between objects and database tables (e.g., Date, Id).

## 4. Refactord Code into Service per Feature
Refactor the code to organize it by feature (e.g., message service, patient service).

## 5. Renamed Tables
- Rename `inbound_message` table to `inbound_hl7_messages`.
- Rename `outbound_message` table to `outbound_hl7_messages`.



## 6. Created APIs for reporting and messaging :
Implemented apis for reporting analytics and messaging Apis

## HL7 Messaging:
Integrates HL7 messages for appointment booking, updates, and patient data exchange, ensuring interoperability between healthcare systems.

# Reporting APIs

## 1. No Show Appointment Count
**GET**: `/hl7/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}`  
**Description**: Fetches the count of no-show appointments for a given patient within a specified date range.


## 2. Booked Appointment Count
**GET**: `/reports/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}`  
**Description**: Fetches the count of successfully booked appointments for a specific patient within a given date range.



## 3. New Appointment with Patient Summary
**GET**: `/reports/findNoShowAppointmentsWithPatients?patientId={patientId}&startDate={startDate}&endDate={endDate}`  
**Description**: Returns a summary of no-show appointments, including patient details, for a specific date range.



## 4. No Show Appointments with Patients
**GET**: `/reports/findNoShowAppointmentsWithPatients`  
**Description**: Retrieves no-show appointments along with patient details, filtered by patient ID and date range.



## 5. Booked Appointment by Demographics
**GET**: `/reports/findBookedAppointmentByPatientDemographics?gender={gender}&patientName={patientName}&address={address}&minAge={minAge}&maxAge={maxAge}&startDate={startDate}&endDate={endDate}`  
**Description**: Retrieves booked appointments filtered by patient demographics (e.g., gender, age, address) within a specified date range.



## 6. Find No-Show Appointments by Patient Demographics
**GET**: `/reports/findNoShowAppointmentByPatientDemographics`  
**Description**: Retrieves no-show appointments based on patient demographics (gender, name, age, address) within a specified date range.



## 7. Booked Appointment with Providers
**GET**: `/reports/findNewAppointmentsWithProviders`  
**Description**: Retrieves new appointments scheduled with specific providers based on provider ID, specialty, and date range.



## 8. No Show Appointments with Providers
**GET**: `/reports/findNoShowAppointmentsWithProviders`  
**Description**: Retrieves no-show appointments filtered by provider ID and optionally by specialty.



## 9. Find Booked Appointments by Location
**GET**: `/reports/findBookedAppointmentWithLocation`  
**Description**: Retrieves booked appointments filtered by location, allowing tracking of appointments for specific locations within a defined date range.



## 10. Find No-Show Appointments by Location
**GET**: `/reports/findNoShowAppointmentWithLocation`  
**Description**: Retrieves no-show appointments filtered by location, enabling healthcare providers to track missed appointments at specific locations.



# Messaging APIs

## 1. Patient Appointment Listing by Name
**GET**: `/appointment/listByName?patientName={patientName}`  
**Description**: Retrieves the list of patient appointments filtered by the patient's name.



## 2. Patient Appointment Listing by Phone Number
**GET**: `/appointment/listByPhNumber?phNumber={phNumber}`  
**Description**: Retrieves the list of patient appointments filtered by the patient's phone number.



## 3. Get HL7 Messages by Date Range
**GET**: `/hl7/getMessageByRange?startDate={startDate}&endDate={endDate}`  
**Description**: Retrieves HL7 messages within the specified date range.



## 4. Get Appointment Count by Type
**GET**: `/appointment/count?type={type}`  
**Description**: Retrieves the count of appointments filtered by their type.



## 5. Delete Appointment by Days
**DELETE**: `/appointment/deleteByDays?days={days}`  
**Description**: Deletes appointments that are older than the specified number of days.



## 6. Delete HL7 Messages by Date
**DELETE**: `/hl7/deleteByDate?date={date}`  
**Description**: Deletes HL7 messages older than the specified date.



## 7. No-Show Rate
**GET**: `/appointment/noshow-rate`  
**Description**: Retrieves the current rate of no-show appointments.



## 8. Count Appointments by Type
**GET**: `/appointment/count-by-type`  
**Description**: Retrieves the count of appointments filtered by their type.


## 9. Book Appointment
**POST**: `/hl7/book-appointment`  
**Description**: Books a new appointment for a patient in the system.



## 10. SIU Request (Schedule Information Unsolicited)
**POST**: `/hl7/SIU`  
**Description**: Sends a Schedule Information Unsolicited (SIU) message to update appointment details in the system.



## 11. Send HL7 Messages Over TCP
**POST**: `/hl7/sendTcp`  
**Description**: Sends HL7 messages over TCP, including ADT messages with patient and visit data.



## 12. Send ADT Message in HTTP Format
**POST**: `/sendHttp`  
**Description**: Sends an ADT message in HTTP format for processing patient data and visit information.



## 13. Book a New Appointment
**POST**: `/appointment/book`  
**Description**: Books a new appointment for a patient, including patient details, visit information, insurance details, and appointment timings.



## 14. Process No-Show Reminders
**GET**: `/appointment/process-no-show-reminders`  
**Description**: Processes no-show reminders to ensure patients are reminded about their upcoming appointments.



