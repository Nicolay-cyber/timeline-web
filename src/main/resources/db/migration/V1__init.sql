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



INSERT INTO functions (id, parent_parameter_id, start_point, end_point, expression)
VALUES (1, 1, 0, 32500,
        '3 * 10 ^ -24 * t ^ 6 - 9 * 10 ^ -19 * t ^ 5 + 7 * 10 ^ -14 * t ^ 4 - 2 * 10 ^ -09 * t ^ 3 + 4 * 10 ^ -05 * t ^ 2 - 0.2782 * t + 2996.3');

INSERT INTO functions (id, parent_parameter_id, start_point, end_point, expression)
VALUES (2, 1, 32500, 33530, '( t + 1 ) ^ ( t + 2 )');
/*
INSERT INTO functions (id, parent_parameter_id, start_point, end_point, expression)
VALUES (3, 1, 33530, 47100,
        '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * t ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * t ^ 3 - 0.4342 * t ^ 2 + 6772.5 * t - 4 * 10 ^ 07');
*/

-- Create the parameters table
CREATE TABLE IF NOT EXISTS parameters
(
    id              BIGINT          AUTO_INCREMENT    PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    abbreviation    VARCHAR(255)    NOT NULL);

-- Create the functions table
CREATE TABLE IF NOT EXISTS functions
(
    id                      BIGINT      AUTO_INCREMENT    PRIMARY KEY,
    start_point             DOUBLE      NOT NULL,
    end_point               DOUBLE      NOT NULL,
    expression              TEXT        NOT NULL,
    parent_parameter_id     BIGINT      NOT NULL,
    related_parameter_id    BIGINT,

    FOREIGN KEY (parent_parameter_id) REFERENCES parameters (id)
    );

CREATE TABLE IF NOT EXISTS points
(
    id              BIGINT  AUTO_INCREMENT PRIMARY KEY,
    x               DOUBLE  NOT NULL,
    y               DOUBLE  NOT NULL,
    parameter_id    BIGINT  NOT NULL,
    FOREIGN KEY (parameter_id ) REFERENCES parameters (id)
    );
-- Create the function_parameters table for mapping functions to parameters
CREATE TABLE IF NOT EXISTS function_parameters
    (
    function_id     BIGINT  NOT NULL,
    parameter_id    BIGINT  NOT NULL,

    FOREIGN KEY (function_id)   REFERENCES functions (id),
    FOREIGN KEY (parameter_id)  REFERENCES parameters (id)
    );

-- Insert data into the parameters table
INSERT INTO parameters (id, name, description, abbreviation)
VALUES (1, 'Temperature', 'Surface temperature in degrees Celsius', 'T'),
       (2, 'Test parameter A', 'Test parameter depended on temperature and time', 'Ta'),
       (3, 'Test parameter B', 'Test parameter depended on temperature and parameter A', 'Tb');

-- Insert data into the points table for Temperature parameter
INSERT INTO points (x, y, parameter_id)
VALUES (4.24, 2961.93, 1),
       (3223.77, 2392.92, 1),
       (5421.22, 2219.01, 1),
       (25658.23, 2098.45, 1),
       (27906.79, 2038.47, 1),
       (30155.35, 1928.86, 1),
       (32506.11, 1699.61, 1),
       (33528.18, 225.01, 1);


-- Insert data into the functions table
INSERT INTO functions (id, parent_parameter_id, start_point, end_point, expression)
VALUES
    /*(1, 1, 0, 32500, '3 * 10 ^ -24 * t ^ 6 - 9 * 10 ^ -19 * t ^ 5 + 7 * 10 ^ -14 * t ^ 4 - 2 * 10 ^ -09 * t ^ 3 + 4 * 10 ^ -05 * t ^ 2 - 0.2782 * t + 2996.3'),
    (2, 1, 32500, 33530, '( t + 1 ) ^ ( t + 2 )'),
    */
    (3, 2, 0, 47100,
     '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * T ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * t ^ 2 + 6772.5 * T - 4 * 10 ^ 07'),
    (4, 2, 50000, 100000,
     '-1 * 10 ^ -20 * t ^ 6 + 3 * 10 ^ -15 * T ^ 5 - 3 * 10 ^ -10 * t ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * t ^ 2 + 6772.5 * T - 4 * 10 ^ 07'),
    (5, 3, 0, 20000,
     '-1 * 10 ^ -20 * Ta ^ 6 + 3 * 10 ^ -15 * Ta ^ 5 - 3 * 10 ^ -10 * Ta ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * Ta ^ 2 + 6772.5 * T - 4 * 10 ^ 07'),
    (6, 3, 20000, 47100,
     '-1 * 10 ^ -20 * T ^ 6 + 3 * 10 ^ -15 * T ^ 5 - 3 * 10 ^ -10 * T ^ 4 + 1 * 10 ^ -05 * T ^ 3 - 0.4342 * Ta ^ 2 + 6772.5 * T - 4 * 10 ^ 07');

-- Insert data into the function_parameters table for related parameters in the function expression
INSERT INTO function_parameters (function_id, parameter_id)
VALUES (3, 1),
       (5, 1),
       (5, 2),
       (6, 2),
       (6, 1),
       (4, 1);
