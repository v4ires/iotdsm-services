#!/bin/bash

db_type="$1"

start_pgsql(){
    /etc/init.d/postgresql start
}

start_mysql(){
    /etc/init.d/mysql start
    mysql -e 'CREATE SCHEMA `iot-repository`;'
    mysqladmin -u root password "qwe1234@"
}

start_mongo(){
    /usr/bin/mongod --config /etc/mongod.conf &
}

start_iotdsm(){
    echo "init iotdsm application..."

    if [ "$db_type" = "pgsql" ]; then
        echo "init pgsql database..."
        start_pgsql
        java -jar iotdsm-services-all-1.0.0.jar -c=pgsql-hb.properties
    elif [ "$db_type" = "mysql" ]; then
        echo "init mysql database..."
        start_mysql
        java -jar iotdsm-services-all-1.0.0.jar -c=mysql-hb.properties
    elif [ "$db_type" = "mongo" ]; then
        echo "init mongo database..."
        start_mongo
        java -jar iotdsm-services-all-1.0.0.jar -c=mongo.properties
    fi
}

start_iotdsm