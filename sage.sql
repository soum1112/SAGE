CREATE DATABASE sage;
USE sage;

CREATE TABLE `User`(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(50),
    password VARCHAR(255)
);

CREATE TABLE Location (
    location_id INT PRIMARY KEY AUTO_INCREMENT,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    address VARCHAR(100)
);

CREATE TABLE SOS_Alert (
    sos_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    status VARCHAR(20),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Emergency_Contact (
    contact_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(50),
    phone VARCHAR(20),
    relation VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Police_Station (
    station_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    area VARCHAR(50),
    contact VARCHAR(20)
);

CREATE TABLE Hospital (
    hospital_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    contact VARCHAR(20)
);

CREATE TABLE Emergency_Service (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(20),
    contact VARCHAR(20)
);

CREATE TABLE Safety_Product (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    category VARCHAR(50),
    price INT,
    description VARCHAR(200)
);

CREATE TABLE Safety_Tips (
    tip_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50),
    category VARCHAR(50),
    description VARCHAR(200)
);

CREATE TABLE Safe_Zone (
    zone_id INT PRIMARY KEY AUTO_INCREMENT,
    location_id INT,
    type VARCHAR(50),
    rating INT,
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

CREATE TABLE Incident_Report (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    type VARCHAR(50),
    description VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Notification (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    message VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    rating INT,
    comments VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Risk_Analysis (
    analysis_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    risk_level VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id)
);

CREATE TABLE Evidence (
    evidence_id INT PRIMARY KEY AUTO_INCREMENT,
    sos_id INT,
    type VARCHAR(20),
    file_path VARCHAR(100),
    FOREIGN KEY (sos_id) REFERENCES SOS_Alert(sos_id)
);

CREATE TABLE User_Product (
    user_id INT,
    product_id INT,
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id),
    FOREIGN KEY (product_id) REFERENCES Safety_Product(product_id)
);

CREATE TABLE User_Tips (
    user_id INT,
    tip_id INT,
    PRIMARY KEY (user_id, tip_id),
    FOREIGN KEY (user_id) REFERENCES `User`(user_id),
    FOREIGN KEY (tip_id) REFERENCES Safety_Tips(tip_id)
);

CREATE TABLE SOS_Notification (
    sos_id INT,
    notification_id INT,
    PRIMARY KEY (sos_id, notification_id),
    FOREIGN KEY (sos_id) REFERENCES SOS_Alert(sos_id),
    FOREIGN KEY (notification_id) REFERENCES Notification(notification_id)
);

-- ✅ NEW TABLE (LINK SOS WITH SERVICES)
CREATE TABLE SOS_Service (
    sos_id INT,
    service_id INT,
    PRIMARY KEY (sos_id, service_id),
    FOREIGN KEY (sos_id) REFERENCES SOS_Alert(sos_id),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id)
);

CREATE TABLE Service_Police (
    service_id INT,
    station_id INT,
    PRIMARY KEY (service_id, station_id),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id),
    FOREIGN KEY (station_id) REFERENCES Police_Station(station_id)
);

CREATE TABLE Service_Hospital (
    service_id INT,
    hospital_id INT,
    PRIMARY KEY (service_id, hospital_id),
    FOREIGN KEY (service_id) REFERENCES Emergency_Service(service_id),
    FOREIGN KEY (hospital_id) REFERENCES Hospital(hospital_id)
);

-- ================= TRIGGERS =================

DELIMITER //
CREATE TRIGGER duplicate_contact
BEFORE INSERT ON Emergency_Contact 
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM Emergency_Contact 
        WHERE user_id = NEW.user_id AND phone = NEW.phone
    )
    THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Duplicate Contact Not Allowed';
    END IF;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER after_sos  
AFTER INSERT ON SOS_Alert  
FOR EACH ROW  
BEGIN  
    INSERT INTO Notification(user_id, message)  
    VALUES (NEW.user_id, 'SOS Triggered!');
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_risk  
AFTER INSERT ON SOS_Alert  
FOR EACH ROW  
BEGIN  
    INSERT INTO Risk_Analysis(user_id, risk_level)  
    VALUES (NEW.user_id, (SELECT getrisklevel(NEW.user_id)));
END //
DELIMITER ;

-- ================= FUNCTIONS =================

DELIMITER //
CREATE FUNCTION contactcount(u_id INT)
RETURNS INT
DETERMINISTIC 
BEGIN
    DECLARE total INT;
    SELECT COUNT(*) INTO total
    FROM Emergency_Contact
    WHERE user_id = u_id;
    RETURN total;
END //
DELIMITER ;

DELIMITER //
CREATE FUNCTION getrisklevel(u_id INT)
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE sos_count INT;
    DECLARE contact_count INT;
    DECLARE risk VARCHAR(20);
    
    SELECT COUNT(*) INTO sos_count FROM SOS_Alert WHERE user_id = u_id;
    SET contact_count = contactcount(u_id);
    
    IF contact_count = 0 THEN
        SET risk = 'HIGH';
    ELSEIF sos_count > 5 THEN
        SET risk = 'HIGH';
    ELSEIF sos_count BETWEEN 3 AND 5 THEN
        SET risk = 'MEDIUM';
    ELSE
        SET risk = 'LOW';
    END IF;

    RETURN risk;
END //
DELIMITER ;

-- ================= PROCEDURES =================

DELIMITER //
CREATE PROCEDURE createsos(IN u_id INT, IN sos_status VARCHAR(20))
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM `User` WHERE user_id = u_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'User does not exist';
    ELSE
        INSERT INTO SOS_Alert(user_id, status) VALUES (u_id, sos_status);
    END IF;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE SaveLocation(
    IN lat DECIMAL(9,6),
    IN lng DECIMAL(9,6),
    IN addr VARCHAR(100)
)
BEGIN
    INSERT INTO Location(latitude, longitude, address)
    VALUES (lat, lng, addr);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE AddReport(
    IN u_id INT, 
    IN r_type VARCHAR(50),
    IN descr VARCHAR(500)
)
BEGIN
    INSERT INTO Incident_Report(user_id, type, description)
    VALUES(u_id, r_type, descr);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE AddNotification(
    IN u_id INT,
    IN msg VARCHAR(100)
)
BEGIN 
    INSERT INTO Notification(user_id, message)
    VALUES(u_id, msg);
END //
DELIMITER ;
