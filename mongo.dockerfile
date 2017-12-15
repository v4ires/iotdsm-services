FROM ubuntu:latest
MAINTAINER Vinicius Aires Barros <v4ires@gmail.com>

ENV JAVA_VERSION 8
ENV PGSQL_VERSION 9.6
ENV GRADLE_VERSION 4.4
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

EXPOSE 5432
EXPOSE 8081

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

# Running IoT Repository Module
USER root
ADD . $HOME/iot-repository
RUN cd iot-repository \
&& gradle build fatJar -x test \
&& cp build/libs/iot-repository-all-1.0-SNAPSHOT.jar .

CMD /etc/init.d/postgresql start
CMD java -jar /iot-repository/iot-repository-all-1.0-SNAPSHOT.jar -c=/iot-repository/mongo.properties -v=ALL