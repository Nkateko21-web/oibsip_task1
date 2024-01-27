CREATE DATABASE IF NOT EXISTS reservation_system;
USE reservation_system;

CREATE TABLE IF NOT EXISTS reservation (
id int Auto_INCREMENT PRIMARY KEY,
username VARCHAR(50) NOT NULL,
password VARCHAR(60) NOT NULL,
train_number VARCHAR(10) NOT NULL,
class_type VARCHAR(10) NOT NULL,
date_of_journey DATE NOT NULL,
source VARCHAR(50) NOT NULL,
destination VARCHAR(50) NOT NULL,
pnr_number VARCHAR(10)
);

GRANT ALL PRIVILEGES ON reservation_system.* TO 'sammy @localhost';
FLUSH PRIVILEGES;
