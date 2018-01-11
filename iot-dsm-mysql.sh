#!/bin/bash

/etc/init.d/mysql start
mysql -e 'CREATE SCHEMA `iotdsm-services`;'
mysqladmin -u root password "qwe1234@"
mysql -u root -pqwe1234@ iotdsm-services < scripts/sql/iotdsm-services-mysql.sql
java -cp iotdsm-services-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=mysql-hb.properties