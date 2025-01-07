# Verita-hl7-messaging-api

## to-do:
1. logger implementation
2. mapping using DTO form JPA query in appointment repository
3. refactor types in all objects(eg. Date type,Id type) should be same as the database tables
4. refactor code into service per feature(eg. message service, patient service)
5. rename table inbound message to inbound_hl7_messages and outbound_hl7_messages

## bugs:

## ReminderQuery:
SELECT
a.visit_appointment_id as appointmentId,
a.patient_id as patientId,
a.appointment_date as appointmentDate,
a.provider_id as providerId,
p.specialty as specialty,
a.visit_status_code as visitStatusCode,
a.reminder_message_status as ReminderMessageStatus,
DATEDIFF(CURDATE(), a.appointment_date) as days
FROM appointments a
LEFT JOIN providers p on p.provider_id = a.provider_id
WHERE a.patient_id = '259163' AND p.specialty='Internal Medicine' AND a.visit_status_code = 'N/S' AND DATEDIFF(CURDATE(), a.appointment_date)<29 AND a.reminder_message_status IN ('NONE', 'NO_SHOW', 'NO_SHOW_2_WEEK')


No Show Appointment Count:
GET /hl7/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}

Booked Appointment Count:
Description: This API fetches the count of appointments successfully booked for a specific patient within a given date range.
GET /reports/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}

New Appointment with Patient Summary
Endpoint: GET /reports/findNoShowAppointmentsWithPatients Description: This API returns a summary of no-show appointments, including patient details, for a specific date range.
GET /reports/findNoShowAppointmentsWithPatients?patientId={patientId}&startDate={startDate}&endDate={endDate}

No Show Appointments with Patients
Endpoint: GET /reports/findNoShowAppointmentsWithPatients
Description: This API retrieves no-show appointments along with patient details, filtered by patient ID and date range. 

Booked Appointment by Demographics:
GET /reports/findBookedAppointmentByPatientDemographics?gender={gender}&patientName={patientName}&address={address}&minAge={minAge}&maxAge={maxAge}&startDate={startDate}&endDate={endDate}
Description: This API retrieves booked appointments filtered by patient demographics such as gender, age, address, and name within a specified date range. 
 
Find No-Show Appointments by Patient Demographics
Endpoint: GET /reports/findNoShowAppointmentByPatientDemographics Description: This API retrieves no-show appointments based on patient demographics (gender, name, age, address) within a specified date range. 

Booked Appointment with Providers
Endpoint: GET /reports/findNewAppointmentsWithProviders 
Description: This API retrieves the new appointments scheduled with specific providers based on provider ID, specialty, and date range.

No Show Appointments with Providers
Endpoint: GET /reports/findNoShowAppointmentsWithProviders 
Description: This API retrieves no-show appointments filtered by provider ID and optionally by specialty, allowing you to track missed appointments for specific providers

Find Booked Appointments by Location
Endpoint: GET /reports/findBookedAppointmentWithLocation 
Description: This API retrieves booked appointments filtered by location, allowing you to track appointments for specific locations within a defined date range.

Find No-Show Appointments by Location
Endpoint: GET /reports/findNoShowAppointmentWithLocation 
Description: This API retrieves no-show appointments filtered by location, enabling healthcare providers to track missed appointments at specific locations. 


