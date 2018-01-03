#!/bin/bash

/etc/init.d/mysql start
mysql -e 'CREATE SCHEMA `iot-repository`;'
mysqladmin -u root password "qwe1234@"
mysql -u root -pqwe1234@ iot-repository < scripts/sql/iot-repository-mysql.sql
java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=mysql-hb.properties