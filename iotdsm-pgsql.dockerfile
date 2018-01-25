FROM ubuntu:latest

ENV JAVA_VERSION 8
ENV PGSQL_VERSION 10
ENV GRADLE_VERSION 4.4
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin
ENV PGPASSWORD qwe1234@

EXPOSE 5432
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

# Install PostgreSQL
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ xenial-pgdg main" >> /etc/apt/sources.list \
&& wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
&& apt-get update \
&& apt-get install -y postgresql-${PGSQL_VERSION} \
&& echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/${PGSQL_VERSION}/main/pg_hba.conf \
&& echo "listen_addresses='*'" >> /etc/postgresql/${PGSQL_VERSION}/main/postgresql.conf

USER postgres
RUN /etc/init.d/postgresql start \
&& psql -d postgres -U postgres --command "ALTER USER postgres with PASSWORD '${PGPASSWORD}';" \
&& createdb -h localhost -p 5432 -U postgres iot-repository

# Running IoT Repository Module
USER root
ADD . $HOME/iotdsm-services
WORKDIR iotdsm-services
RUN gradle build fatJar -x test --parallel

VOLUME $HOME/.gradle/
VOLUME $HOME/iotdsm-services

CMD [ "sh", "iotdsm-start.sh", "pgsql" ]