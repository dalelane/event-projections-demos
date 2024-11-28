FROM --platform=linux/amd64 icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

COPY --chown=1001:0 src/main/liberty/config/server.xml                             /config/server.xml
COPY --chown=1001:0 target/liberty/wlp/usr/shared/resources/postgresql-42.7.4.jar  /opt/ol/wlp/usr/shared/resources/postgresql-42.7.4.jar

RUN features.sh

COPY --chown=1001:0 target/kafka-projection-database.war /config/apps

RUN configure.sh
