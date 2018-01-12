#!/bin/bash

/usr/bin/mongod --config /etc/mongod.conf &
java -cp iotdsm-edu.usp.icmc.lasdpc.iotdsm.services-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=mongo.properties