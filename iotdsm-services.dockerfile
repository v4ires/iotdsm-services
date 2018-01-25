FROM ubuntu:latest

ENV JAVA_VERSION 8
ENV GRADLE_VERSION 4.4
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

EXPOSE 8081

# Create Log Directory
RUN mkdir -p /var/log/iotdsm-services

# Update APT Repository
RUN apt-get -y update \
&& apt-get install -y curl \
&& apt-get install -y unzip \
&& apt-get install -y htop \
&& apt-get install -y wget \
&& apt-get install -y vim

# Install OpenJDK 8
RUN apt-get install -y openjdk-${JAVA_VERSION}-jdk
RUN echo "export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64" >> ~/.bashrc

# Install Gradle
RUN cd /usr/lib \
&& curl -fl https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle-bin.zip \
&& unzip "gradle-bin.zip" \
&& ln -s "/usr/lib/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle \
&& rm "gradle-bin.zip"

# Running IoT Repository Module
ADD . $HOME/iotdsm-services
WORKDIR iotdsm-services
RUN gradle build fatJar -x test --parallel

VOLUME $HOME/.gradle/
VOLUME $HOME/iotdsm-services