#!/bin/bash

/usr/bin/mongod --config /etc/mongod.conf &
java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=mongo.properties