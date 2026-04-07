DROP DATABASE IF EXISTS sage;
CREATE DATABASE sage;
USE sage;

CREATE TABLE `User` (
    user_id    INT          PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL,
    phone      VARCHAR(20)  NOT NULL UNIQUE,
    email      VARCHAR(50)  UNIQUE,
    password   VARCHAR(255) NOT NULL,      
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Location (
    location_id INT          PRIMARY KEY AUTO_INCREMENT,
    latitude    DECIMAL(9,6) NOT NULL,
    longitude   DECIMAL(9,6) NOT NULL,
    address     VARCHAR(100),
    recorded_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE User_Location (
    user_id     INT NOT NULL,
    location_id INT NOT NULL,
    shared_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, location_id),
    FOREIGN KEY (user_id)     REFERENCES `User`(user_id),
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

CREATE TABLE SOS_Alert (
    sos_id      INT         PRIMARY KEY AUTO_INCREMENT,
    user_id     INT         NOT NULL,
    location_id INT,
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    timestamp   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES `User`(user_id),
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

CREATE TABLE Emergency_Contact (
    contact_id INT         PRIMARY KEY AUTO_INCREMENT,
    user_id    INT         NOT NULL,
    name       VARCHAR(50) NOT NULL,
    phone      VARCHAR(20) NOT NULL,
    relation   VARCHAR(30),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Emergency_Service (
    service_id   INT          PRIMARY KEY AUTO_INCREMENT,
    type         VARCHAR(20)  NOT NULL CHECK (type IN ('AMBULANCE','POLICE','NGO','OTHER')),
    name         VARCHAR(100) NOT NULL,
    contact      VARCHAR(20),
    availability VARCHAR(50)  DEFAULT 'AVAILABLE',
    location_id  INT,
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);
CREATE TABLE Ambulance (
    service_id     INT PRIMARY KEY,
    vehicle_number VARCHAR(20),
    hospital_name  VARCHAR(100),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id)
);

CREATE TABLE Police_Station (
    service_id   INT PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL,
    area         VARCHAR(100),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id)
);

CREATE TABLE NGO (
    service_id INT PRIMARY KEY,
    ngo_name   VARCHAR(100) NOT NULL,
    focus_area VARCHAR(100),
    website    VARCHAR(200),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id)
);

CREATE TABLE Safety_Product (
    product_id  INT           PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL,
    category    VARCHAR(50),
    price       DECIMAL(10,2),
    description VARCHAR(500),
    link        VARCHAR(255)
);

CREATE TABLE Safety_Tips (
    tip_id      INT          PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(100) NOT NULL,
    category    VARCHAR(50),
    description VARCHAR(500),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Safe_Zone (
    zone_id     INT         PRIMARY KEY AUTO_INCREMENT,
    location_id INT         NOT NULL,
    name        VARCHAR(100),
    type        VARCHAR(50),
    description VARCHAR(300),
    rating      INT         CHECK (rating BETWEEN 1 AND 5),
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);


CREATE TABLE Incident_Report (
    report_id   INT          PRIMARY KEY AUTO_INCREMENT,
    user_id     INT          NOT NULL,
    location_id INT,
    type        VARCHAR(50),
    description VARCHAR(500),
    evidence    VARCHAR(255),
    reported_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES `User`(user_id),
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);


CREATE TABLE Notification (
    notification_id INT          PRIMARY KEY AUTO_INCREMENT,
    user_id         INT          NOT NULL,
    message         VARCHAR(255) NOT NULL,
    n_type          VARCHAR(30)  DEFAULT 'GENERAL',  
    is_read         TINYINT(1)   DEFAULT 0,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Feedback (
    feedback_id  INT         PRIMARY KEY AUTO_INCREMENT,
    user_id      INT         NOT NULL,
    rating       INT         CHECK (rating BETWEEN 1 AND 5),
    comments     VARCHAR(500),
    submitted_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Risk_Analysis (
    analysis_id INT         PRIMARY KEY AUTO_INCREMENT,
    user_id     INT         NOT NULL,
    risk_level  VARCHAR(20) NOT NULL,
    analysed_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);


CREATE TABLE Evidence (
    evidence_id INT          PRIMARY KEY AUTO_INCREMENT,
    sos_id      INT          NOT NULL,
    file_type   VARCHAR(20)  NOT NULL,   
    file_path   VARCHAR(255) NOT NULL,
    uploaded_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sos_id) REFERENCES SOS_Alert(sos_id)
);

CREATE TABLE User_Product (
    user_id    INT,
    product_id INT,
    viewed_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id)    REFERENCES `User`(user_id),
    FOREIGN KEY (product_id) REFERENCES Safety_Product(product_id)
);


CREATE TABLE User_Tips (
    user_id   INT,
    tip_id    INT,
    viewed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, tip_id),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id),
    FOREIGN KEY (tip_id)  REFERENCES Safety_Tips(tip_id)
);

CREATE TABLE SOS_Notification (
    sos_id          INT,
    notification_id INT,
    PRIMARY KEY (sos_id, notification_id),
    FOREIGN KEY (sos_id)          REFERENCES SOS_Alert(sos_id),
    FOREIGN KEY (notification_id) REFERENCES Notification(notification_id)
);


CREATE TABLE SOS_Service (
    sos_id       INT,
    service_id   INT,
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (sos_id, service_id),
    FOREIGN KEY (sos_id)     REFERENCES SOS_Alert(sos_id),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id)
);


CREATE TABLE Contact_Notification (
    contact_id      INT,
    notification_id INT,
    PRIMARY KEY (contact_id, notification_id),
    FOREIGN KEY (contact_id)      REFERENCES Emergency_Contact(contact_id),
    FOREIGN KEY (notification_id) REFERENCES Notification(notification_id)
);
