FROM ubuntu:latest

ENV JAVA_VERSION 8
ENV PGSQL_VERSION 9.6
ENV GRADLE_VERSION 4.4
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

EXPOSE 3306
EXPOSE 8081

# Create Log Directory
RUN mkdir -p /var/log/iot-repository

# Update APT Repository
RUN apt-get -y update \
&& apt-get install -y curl \
&& apt-get install -y unzip \
&& apt-get install -y htop \
&& apt-get install -y wget

# Install OpenJDK 8
RUN apt-get install -y openjdk-${JAVA_VERSION}-jdk
RUN echo "export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64" >> ~/.bashrc

# Install Gradle
RUN cd /usr/lib \
&& curl -fl https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle-bin.zip \
&& unzip "gradle-bin.zip" \
&& ln -s "/usr/lib/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle \
&& rm "gradle-bin.zip"

# Install MySQL/MariaDB
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server

# Running IoT Repository Module
ADD . $HOME/iot-repository
WORKDIR iot-repository 
RUN gradle build fatJar -x test \
&& cp build/libs/iot-repository-all-1.0-SNAPSHOT.jar .

# Clean Up
RUN apt-get clean \
&& rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*