To-Do List
1. Logger Implementation
Implement logging functionality across the system to track events, errors, and system activity.
2. Mapping Using DTO from JPA Query in Appointment Repository
Map data from the JPA queries to DTO (Data Transfer Object) for appointment-related data.
3. Refactor Types in All Objects
Ensure consistency in data types between objects and database tables (e.g., Date, Id).
4. Refactor Code into Service per Feature
Refactor the code to organize it by feature (e.g., message service, patient service).
5. Rename Tables
Rename inbound_message table to inbound_hl7_messages.
Rename outbound_message table to outbound_hl7_messages.
Bugs
Reminder Query Issue:
sql
SELECT a.visit_appointment_id as appointmentId,
       a.patient_id as patientId,
       a.appointment_date as appointmentDate,
       a.provider_id as providerId,
       p.specialty as specialty,
       a.visit_status_code as visitStatusCode,
       a.reminder_message_status as ReminderMessageStatus,
       DATEDIFF(CURDATE(), a.appointment_date) as days
FROM appointments a
LEFT JOIN providers p on p.provider_id = a.provider_id
WHERE a.patient_id = '259163'
AND p.specialty = 'Internal Medicine'
AND a.visit_status_code = 'N/S'
AND DATEDIFF(CURDATE(), a.appointment_date) < 29
AND a.reminder_message_status IN ('NONE', 'NO_SHOW', 'NO_SHOW_2_WEEK')
Reporting APIs
No Show Appointment Count
GET: /hl7/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}
Booked Appointment Count
GET: /reports/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}
Description: Fetches the count of appointments successfully booked for a specific patient within a given date range.
New Appointment with Patient Summary
GET: /reports/findNoShowAppointmentsWithPatients?patientId={patientId}&startDate={startDate}&endDate={endDate}
Description: Returns a summary of no-show appointments, including patient details, for a specific date range.
No Show Appointments with Patients
GET: /reports/findNoShowAppointmentsWithPatients
Description: Retrieves no-show appointments along with patient details, filtered by patient ID and date range.
Booked Appointment by Demographics
GET: /reports/findBookedAppointmentByPatientDemographics?gender={gender}&patientName={patientName}&address={address}&minAge={minAge}&maxAge={maxAge}&startDate={startDate}&endDate={endDate}
Description: Retrieves booked appointments filtered by patient demographics (e.g., gender, age, address) within a specified date range.
Find No-Show Appointments by Patient Demographics
GET: /reports/findNoShowAppointmentByPatientDemographics
Description: Retrieves no-show appointments based on patient demographics (gender, name, age, address) within a specified date range.
Booked Appointment with Providers
GET: /reports/findNewAppointmentsWithProviders
Description: Retrieves new appointments scheduled with specific providers based on provider ID, specialty, and date range.
No Show Appointments with Providers
GET: /reports/findNoShowAppointmentsWithProviders
Description: Retrieves no-show appointments filtered by provider ID and optionally by specialty.
Find Booked Appointments by Location
GET: /reports/findBookedAppointmentWithLocation
Description: Retrieves booked appointments filtered by location, allowing you to track appointments for specific locations within a defined date range.
Find No-Show Appointments by Location
GET: /reports/findNoShowAppointmentWithLocation
Description: Retrieves no-show appointments filtered by location, enabling healthcare providers to track missed appointments at specific locations.
Messaging APIs
Patient Appointment Listing by Name
GET: http://localhost:8083/appointment/listByName?patientName={patientName}
Patient Appointment Listing by Phone Number
GET: http://localhost:8083/appointment/listByPhNumber?phNumber={phNumber}
Get HL7 Messages by Date Range
GET: http://localhost:8082/hl7/getMessageByRange?startDate={startDate}&endDate={endDate}
Get Appointment Count by Type
GET: http://localhost:8083/appointment/count?type={type}
Delete Appointment by Days
DELETE: http://localhost:8083/appointment/deleteByDays?days={days}
Delete HL7 Messages by Date
DELETE: https://20.119.41.172:8082/hl7/deleteByDate?date={date}
No-Show Rate
GET: http://localhost:8082/appointment/noshow-rate
Count Appointments by Type
GET: http://localhost:8083/appointment/count-by-type
Book Appointment
POST: http://localhost:8083/hl7/book-appointment
SIU Request (Schedule Information Unsolicited)
POST: http://localhost:8083/hl7/SIU
Description: Sends a Schedule Information Unsolicited (SIU) message to update appointment details in the system.
Send HL7 Messages Over TCP
POST: http://localhost:8083/hl7/sendTcp
Description: Sends HL7 messages over TCP to the system, including ADT messages with patient and visit data.
Send ADT Message in HTTP Format
POST: http://localhost:8083/sendHttp
Description: Sends an ADT message in HTTP format for processing patient data and visit information.
Book a New Appointment
POST: http://localhost:8083/appointment/book
Description: Books a new appointment for a patient, including patient details, visit information, insurance details, and appointment timings.
Process No-Show Reminders
GET: http://localhost:8083/appointment/process-no-show-reminders
Description: Processes no-show reminders for appointments to ensure patients are reminded about their upcoming appointments.
License
This project is licensed under the MIT License - see the LICENSE.md file for details.
