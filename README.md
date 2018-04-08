# **I**nternet **o**f **T**hings **D**ata as a **S**ervice **M**iddleware Service (IoTDSM-S)

<p align="center"><img src="assets/logo/iot-dsm-logo.png"/></p>

The **I**nternet **o**f **T**hings **D**ata as a **S**ervice **M**iddleware **S**ervice (IoTDSM-S) is a project developed in the Laboratory of Distributed Systems and Concurrent Programming (LaSDPC) of the University of São Paulo (USP). This project aims to simplify the acquisition and storage of sensor data in the Internet of Things (IoT) context. For this, IoTDSM supports different storage strategies in SQL and NoSQL databases. Also, this tool provides a RESTful API for communication and data management of sensor networks.

## Getting Started

The IoTDSM aims to provide data from objects belonging to one or more intelligent environments in the context of IoT.
This tool provides a simplified interface for connecting to different sensor network data sources, providing support for different types of databases, whether SQL or NoSQL.

### Installing

To install IoTDSM, compile the project with the command:

```bash
#Compile the project with all dependencies disregarding the execution of tests
~$ gradle build fatJar -x test --parallel
```

This command will generate a .jar file with all the dependencies required to run the project.
The generated .jar file is located in the directory:

```bash
#File: iotdsm-services-all-1.0.0.jar
~$ build/libs
```

If you only need to download the dependencies, execute the command:

```bash
~$ gradle build --refresh-dependencies
```

To run the RESTFul API server from IoTDSM just run the command:

```bash
#Executes RESTFul API Service from IoTDSM
~$ java -jar iotdsm-services-all-1.0.0.jar <args>
```

## Running Tests

To run the unit tests just run the command:

```bash
#Perform Unit Tests
~$ gradle test
```

## Running and Deployment

The IoTDSM provides an input interface for different configuration parameters. These parameters are passed through system-defined *flags*. To view the available parameters, execute the command:

```bash
#Shows the options parameters available
~$ java -jar iotdsm-services-all-1.0.0.jar -help
```

The available parameters are as follows:

```bash
 -c,--configuration <arg>   Path to configuration file [config.properties].
 -h,--help                  Show help [true, false].
 -l,--log <arg>             Enable or disable log [true, false].
 -lf,--logfile <arg>        Log4J Configuration File [log4j.properties].
 -v,--log-level <arg>       Changes the log level [OFF, TRACE, DEBUG, WARN, ERROR, FATAL, ALL].
```
In addition to the default parameters other settings can be defined by means of a configuration file.
This file specifies the IoTDSM Database and Web Server configuration.
The table of available arguments is shown below.

<center>

**Table 1**: Web Server input variables.

|          Variável         |                   Descrição                  |
|:-------------------------:|:--------------------------------------------:|
| HOST                      | Database Host Address                           |
| PORT                      | Database Host Port                           |
| DATABASE                  | Database Name                                   |
| USER                      | Database User Name                           |
| PASSWORD                  | Database Password                               |
| DRIVER                    | Driver JDBC                                  |
| SQL_DEBUG                 | SQL Debug Mode                               |
| DATABASETYPE              | Database Type (mysql, pgsql, mongo)           |
| USEHIBERNATE              | Using Hibernate ORM                          |
| SPARK_THREAD_POOL         | Use Threads Pool on Server                   |
| SPARK_THREAD_POOL_TIMEOUT | Web Server Threads Pool Timeout               |
| SPARK_THREAD_POOL_MIN     | Min Pool Size Web Server                       |
| SPARK_THREAD_POOL_MAX     | Max Pool Size Web Server                       |
| APIPORT                   | Web Server Port Number                       |
| DIALECT                   | Database Dialect                               |

</center>

These variables are passed through a configuration file (config.properties) through the command **-c= ${config_file}**.

```bash
~$ java -cp iotdsm-services-all-1.0.0.jar EmbeddedServletMain -c=${config_file}
```

## Docker Images

This project provides docker images for use in production. The following are the official docker files available.
To compile the images in Docker from IoTDSM just run the command:

```bash
#To Compile Image with MySQL Database
~$ docker build -f iotdsm-mysql.dockerfile -t iotdsm-mysql .
```

or

```bash
#To Compile Image with PgSQL Database
~$ docker build -f iotdsm-pgsql.dockerfile -t iotdsm-pgsql .
```

or

```bash
#To Compile Image with Mongo Database
~$ docker build -f iotdsm-mongo.dockerfile -t iotdsm-mongo .
```

Finally, to execute the desired image, just execute the command:

```bash
#To run IoTDSM given a type of Database
~$ docker run -dp 8081:8081 iotdsm-${db_type}:latest sh iotdsm-start.sh ${db_type}
```

## Built With

* [Gradle](https://gradle.org/) - Dependency Manager.

## Documentation

The IoTDSM provides a web page with detailed documentation on the system's RESTFul API.
To access this documentation, just go to the link:

```url
http://<HOST>:<PORT>/index.html
```

In addition, for more information about the project visit the [WIKI](https://github.com/v4ires/iotdsm-edu.usp.icmc.lasdpc.iotdsm.services/wiki) page of the project.

## Contributing

Please read the [CONTRIBUTING.md](CONTRIBUTING.md) file for more details on how to contribute to this project.

## Authors

* **Vinicius Aires Barros** - *Initial Project Initializer* - [@v4ires](https://github.com/v4ires)
* **Leonardo Beck Prates**  - *Collaborator* - [@leobeckp](https://github.com/leobeckp)

## License

This project is licensed under the MIT license - see the [LICENSE](LICENSE) file for more details.

## Acknowledgments

* University of São Paulo (USP)
* Institute of Mathematical and Computer Sciences (ICMC)
* Laboratory of Distributed Systems and Concurrent Programming (LaSDPC)
