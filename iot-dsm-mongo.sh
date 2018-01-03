#!/bin/bash

nohup /usr/bin/mongod --config /etc/mongod.conf 2>&1 &
java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=mongo.properties