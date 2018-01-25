FROM ubuntu:latest

ENV JAVA_VERSION 8
ENV PGSQL_VERSION 9.6
ENV GRADLE_VERSION 4.4
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

EXPOSE 27017
EXPOSE 28017
EXPOSE 8081

# Create Log Directory
RUN mkdir -p /var/log/iotdsm-services

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

# Install MongoDB
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
RUN echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.4.list
RUN apt-get update
RUN apt-get install -y mongodb-org mongodb-org-server mongodb-org-mongos mongodb-org-shell mongodb-org-tools

# Running IoT Repository Module
ADD . $HOME/iotdsm-services
WORKDIR iotdsm-services
RUN gradle build fatJar -x test --parallel

VOLUME $HOME/.gradle/
VOLUME $HOME/iotdsm-services

CMD [ "sh", "iotdsm-start.sh", "mongo" ]