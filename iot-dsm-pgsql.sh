#!/bin/bash

/etc/init.d/postgresql start
java -cp iotdsm-edu.usp.icmc.lasdpc.iotdsm.services-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=pgsql-hb.properties