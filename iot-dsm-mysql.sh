#!/bin/bash

/etc/init.d/mysql start
mysql -e 'CREATE SCHEMA `iotdsm-edu.usp.icmc.lasdpc.iotdsm.services`;'
mysqladmin -u root password "qwe1234@"
mysql -u root -pqwe1234@ iotdsm-edu.usp.icmc.lasdpc.iotdsm.services < scripts/sql/iotdsm-edu.usp.icmc.lasdpc.iotdsm.services-mysql.sql
java -cp iotdsm-edu.usp.icmc.lasdpc.iotdsm.services-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=mysql-hb.properties