INSERT INTO APP_USER (username, password, name, phone_number, user_type, user_role)
VALUES ('doctor1', '{bcrypt}$2a$10$7QJZBrS1IfHCzq1BQn8D.eBFK0ZB6hqlY3n6GQW/um60Hb/K63Gv2', 'Doctor One', '1234567890', 'DOCTOR', 'ROLE_DOCTOR');

INSERT INTO APP_USER (username, password, name, phone_number, user_type, user_role)
VALUES ('doctor2', '{bcrypt}$2a$10$7QJZBrS1sfHCzq1BQn8D.ecFK0ZB6hqlY3n6GQW/um60ab/K63Gv2', 'Doctor Two', '1234567890', 'DOCTOR', 'ROLE_DOCTOR');

INSERT INTO APP_USER (username, password, name, phone_number, user_type, user_role)
VALUES ('patient1', '{bcrypt}$2a$10$7QJZBrS1IfHCzq1BQn8D.eBFK0ZB6hqlY3n6GQW/um60Hb/K63Gv2', 'Patient One', '1234567890', 'PATIENT', 'ROLE_PATIENT');
