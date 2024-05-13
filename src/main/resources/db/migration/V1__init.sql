-- Create the parameters table
CREATE TABLE if not exists parameters
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    abbreviation VARCHAR(255) NOT NULL UNIQUE
);

-- Create the functions table
CREATE TABLE if not exists functions
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_point         DOUBLE NOT NULL,
    end_point           DOUBLE NOT NULL,
    expression          TEXT   NOT NULL,
    parent_parameter_id BIGINT,
    FOREIGN KEY (parent_parameter_id) REFERENCES parameters (id)

);

INSERT INTO parameters (name, description, abbreviation)
VALUES ('Temperature', 'Surface temperature in degrees Celsius', 'T');

INSERT INTO functions (parent_parameter_id, start_point, end_point, expression)
VALUES ((SELECT id FROM parameters WHERE abbreviation = 'T'), 0, 32500,
        '3 * 10 ^ -24 * t ^ 6 - 9 * 10 ^ -19 * t ^ 5 + 7 * 10 ^ -14 * t ^ 4 - 2 * 10 ^ -09 * t ^ 3 + 4 * 10 ^ -05 * t ^ 2 - 0.2782 * t + 2996.3');

INSERT INTO functions (parent_parameter_id, start_point, end_point, expression)
VALUES ((SELECT id FROM parameters WHERE abbreviation = 'T'), 32500, 33530, '-6.5497 * t + 219824');

INSERT INTO functions (parent_parameter_id, start_point, end_point, expression)
VALUES ((SELECT id FROM parameters WHERE abbreviation = 'T'), 33530, 47100,
        '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * t ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * t ^ 3 - 0.4342 * t ^ 2 + 6772.5 * t ^  - 4 * 10 ^ 07');
