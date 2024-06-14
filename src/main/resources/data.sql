-- -- Insert roles
-- INSERT INTO user_role (name) VALUES ('DOCTOR');
-- INSERT INTO user_role (name) VALUES ('PATIENT');
--
-- -- Insert users
-- INSERT INTO app_user (id, username, password, name, phone_number, user_type)
-- VALUES (1, 'doctor1', '{bcrypt}$2a$10$7QJZBrS1IfHCzq1BQn8D.eBFK0ZB6hqlY3n6GQW/um60Hb/K63Gv2', 'Doctor One', '1234567890', 'DOCTOR');
--
-- INSERT INTO app_user (id, username, password, name, phone_number, user_type)
-- VALUES (2, 'doctor2', '{bcrypt}$2a$10$7QJZBrS1IfHCzq1BQn8D.eBFK0ZB6hqlY3n6GQW/um60Hb/K63Gv2', 'Doctor One', '0987654321', 'DOCTOR');

-- Insert users
INSERT INTO APP_USER (username, password, name, phone_number, user_type, user_role)
VALUES
    ('doctor1', '{bcrypt}$2a$10$7QJZBrS1IfHCzq1BQn8D.eBFK0ZB6hqlY3n6GQW/um60Hb/K63Gv2', 'Doctor One', '1234567890', 'DOCTOR', 'DOCTOR'),
    ('patient1', '{bcrypt}$2a$10$7QJZBrS1IfHCzq1BQn8D.eBFK0ZB6hqlY3n6GQW/um60Hb/K63Gv2', 'Patient One', '0987654321', 'PATIENT', 'PATIENT');
