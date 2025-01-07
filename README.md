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
GET /reports/createdAppointmentCount?patientId={patientId}&startDate={startDate}&endDate={endDate}

Appointment Summary by Patient:
GET /reports/findNoShowAppointmentsWithPatients?patientId={patientId}&startDate={startDate}&endDate={endDate}

Booked Appointment by Demographics:
GET /reports/findBookedAppointmentByPatientDemographics?gender={gender}&patientName={patientName}&address={address}&minAge={minAge}&maxAge={maxAge}&startDate={startDate}&endDate={endDate}
