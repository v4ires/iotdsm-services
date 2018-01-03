#!/bin/bash

/etc/init.d/postgresql start
java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=pgsql-hb.properties