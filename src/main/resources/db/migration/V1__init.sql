/*
--For MySQL
-- Create the parameters table
CREATE TABLE if not exists parameters
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    abbreviation VARCHAR(255) NOT NULL
    );

-- Create the functions table
CREATE TABLE if not exists functions
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_point             DOUBLE NOT NULL,
    end_point               DOUBLE NOT NULL,
    expression              TEXT   NOT NULL,
    parent_parameter_id     BIGINT NOT NULL,
    related_parameter_id    BIGINT,

    FOREIGN KEY (parent_parameter_id) REFERENCES parameters (id)
    );

-- Промежуточная таблица для связи функций и параметров
CREATE TABLE if not exists function_parameters
(
    function_id    BIGINT NOT NULL,
    parameter_id   BIGINT NOT NULL,

    FOREIGN KEY (function_id) REFERENCES functions (id),
    FOREIGN KEY (parameter_id) REFERENCES parameters (id)
    );



INSERT INTO parameters (id, name, description, abbreviation)
VALUES (1, 'Temperature', 'Surface temperature in degrees Celsius', 'T');

INSERT INTO parameters (id, name, description, abbreviation)
VALUES (2, 'Test parameter A', 'Test parameter depended on temperature and time', 'Ta');

INSERT INTO parameters (id, name, description, abbreviation)
VALUES (3, 'Test parameter B', 'Test parameter depended on temperature and parameter A', 'Tb');
*/

-- Create the units table
CREATE TABLE IF NOT EXISTS units
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    abbreviation VARCHAR(255) NOT NULL
);
-- Create the parameters table
CREATE TABLE IF NOT EXISTS parameters
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    abbreviation VARCHAR(255) NOT NULL,
    unit_id      BIGINT,
    FOREIGN KEY (unit_id) REFERENCES units (id)
);
CREATE TABLE IF NOT EXISTS models (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   description TEXT
);

CREATE TABLE IF NOT EXISTS parameter_model (
   parameter_id BIGINT,
   model_id BIGINT,
   PRIMARY KEY (parameter_id, model_id),
   FOREIGN KEY (parameter_id) REFERENCES parameters(id),
   FOREIGN KEY (model_id) REFERENCES models(id)
);

-- Create the functions table
CREATE TABLE IF NOT EXISTS functions
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_point          DOUBLE NOT NULL,
    end_point            DOUBLE NOT NULL,
    expression           TEXT   NOT NULL,
    parent_parameter_id  BIGINT NOT NULL,
    related_parameter_id BIGINT,
    FOREIGN KEY (parent_parameter_id) REFERENCES parameters (id)
);

-- Create the function_parameters table for mapping functions to parameters
CREATE TABLE IF NOT EXISTS function_parameters
(
    function_id  BIGINT NOT NULL,
    parameter_id BIGINT NOT NULL,
    FOREIGN KEY (function_id) REFERENCES functions (id),
    FOREIGN KEY (parameter_id) REFERENCES parameters (id)
);
CREATE TABLE IF NOT EXISTS points
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    x                    DOUBLE NOT NULL,
    y                    DOUBLE NOT NULL,
    parent_parameter_id  BIGINT NOT NULL,
    related_parameter_id BIGINT,
    FOREIGN KEY (parent_parameter_id) REFERENCES parameters (id),
    FOREIGN KEY (related_parameter_id) REFERENCES parameters (id)
);

-- Create indexes to speed up searching
CREATE INDEX idx_functions_parent_parameter ON functions (parent_parameter_id);
CREATE INDEX idx_functions_related_parameter ON functions (related_parameter_id);
CREATE INDEX idx_points_parent_parameter ON points (parent_parameter_id);
CREATE INDEX idx_points_related_parameter ON points (related_parameter_id);

-- Insert data into the units table
INSERT INTO units (name, abbreviation)
VALUES ('Kelvin', 'K');
INSERT INTO units (name, abbreviation)
VALUES ('Pascal', 'Pa');

INSERT INTO parameters (name, description, abbreviation, unit_id)
VALUES ('Temperature', 'Surface temperature in degrees Celsius on the Earth starting since Moon formation impact', 'T', 1),
       ('Test parameter A', 'Test parameter depended on temperature and time', 'Ta', 1),
       ('Test parameter B', 'Test parameter depended on temperature and parameter A', 'Tb', 1),
       ('Surface pressure',
        'Surface pressure in Pa on the Earth starting since Moon formation impact and depending on surface temperature.' ||
        ' The ideal gas low is used here. Volume of the Earth atmosphere is taken as 4.2*10^15 m^3.' ||
        ' Amount of substance is 28.97 g/mole. R = 8.314 J/(mole*K).', 'P', 2);


-- Insert data into the functions table
INSERT INTO functions (parent_parameter_id, start_point, end_point, expression)
VALUES
    //(1, 1, 0, 32500, '3 * 10 ^ -24 * t ^ 6 - 9 * 10 ^ -19 * t ^ 5 + 7 * 10 ^ -14 * t ^ 4 - 2 * 10 ^ -09 * t ^ 3 + 4 * 10 ^ -05 * t ^ 2 - 0.2782 * t + 2996.3'),
    //(2, 1, 32500, 33530, '( t + 1 ) ^ ( t + 2 )'),
    //(3, 1, 33530, 47100, '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * t ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * t ^ 3 - 0.4342 * t ^ 2 + 6772.5 * t - 4 * 10 ^ 07'),
    (2, 0, 47100,
     '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * T ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * t ^ 2 + 6772.5 * T - 4 * 10 ^ 07'),
    (2, 50000, 100000,
     '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * T ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * t ^ 2 + 6772.5 * T - 4 * 10 ^ 07'),
    (3, 0, 20000,
     '-1 * 10 ^ -20 * Ta ^ 6 + 3 * 10 ^ -15 * Ta ^ 5 - 3 * 10 ^ -10 * Ta ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * Ta ^ 2 + 6772.5 * T - 4 * 10 ^ 07'),
    (4, 0, 40000, '( 28.97 * 8.314 * T ) / ( 4.2 * 10 ^ 15 )'),
    (3, 20000, 47100,
     '-1 * 10 ^ -20 * T ^ 6 + 3 * 10 ^ -15 * T ^ 5 - 3 * 10 ^ -10 * T ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * Ta ^ 2 + 6772.5 * T - 4 * 10 ^ 07');

-- Insert data into the function_parameters table
INSERT INTO function_parameters (function_id, parameter_id)
VALUES (1, 1),
       (2, 1),
       (2, 2),
       (3, 2),
       (3, 1),
       (4, 1),
       (5, 1);


INSERT INTO points (x, y, parent_parameter_id)
VALUES
       (0, 6000, 1),
       (4.24, 2961.93, 1),
       (3223.77, 2392.92, 1),
       (5421.22, 2219.01, 1),
       (25658.23, 2098.45, 1),
       (27906.79, 2038.47, 1),
       (30155.35, 1928.86, 1),
       (32506.11, 1699.61, 1),
       (33528.18, 225.01, 1);

-- Create a new model
INSERT INTO models (name, description)
VALUES ('Earth after Moon formation', 'A model describing Earth after the formation of the Moon, including parameters for temperature, test parameter Ta, and pressure');

-- Retrieve the IDs of the parameters to be added to the model
SELECT id FROM parameters WHERE abbreviation IN ('T', 'Ta', 'P');

-- Assuming the retrieved IDs are 1, 2, and 4 for the parameters 'T', 'Ta', and 'P' respectively
INSERT INTO parameter_model (parameter_id, model_id)
VALUES
    (1, 1),
    (2, 1),
    (4, 1);

