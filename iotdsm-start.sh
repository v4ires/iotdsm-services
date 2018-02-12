#!/bin/bash

db_type="$1"

start_pgsql(){
    SERVICE=postgres
    if P=$(pgrep $SERVICE)
    then
        echo "$SERVICE is already running"
    else
        echo "starting postgresql"    
        /etc/init.d/postgresql start
    fi
}

start_mysql(){
    SERVICE=mysql
    if P=$(pgrep $SERVICE)
    then
        echo "$SERVICE is already running"
    else
        echo "starting mysql"
        /etc/init.d/mysql start
    fi
}

start_mongo(){
    SERVICE=mongod
    if P=$(pgrep $SERVICE)
    then
        echo "$SERVICE is already running"
    else
        echo "starting mongod"
        /usr/bin/mongod --config /etc/mongod.conf &
    fi
}

start_iotdsm(){

    if [ ! -f "build/libs/iotdsm-services-all-1.0.0.jar" ]; then
        bash -c "gradle build fatJar -x test --parallel"
    fi

    properties_file=""
    if [ "$db_type" = "pgsql" ]; then
        echo "init pgsql database..."
        start_pgsql
        properties_file="pgsql-hb.properties"
    elif [ "$db_type" = "mysql" ]; then
        echo "init mysql database..."
        start_mysql
        properties_file="mysql-hb.properties"
    elif [ "$db_type" = "mongo" ]; then
        echo "init mongo database..."
        start_mongo
        properties_file="mongo.properties"
    fi
    
    echo "init iotdsm application..."
    bash -c "java -jar build/libs/iotdsm-services-all-1.0.0.jar -c=$properties_file"
}

#edu.usp.icmc.lasdpc.Main
sh clean-log.sh
start_iotdsm
