FROM adoptopenjdk/openjdk11:x86_64-ubuntu-jdk-11.0.3_7

MAINTAINER Kasra Madadipouya <kasra@madadipouya.com>

RUN mkdir -p /user/share/cisapify/static/songs

RUN mkdir -p /user/share/cisapify/bin

ADD /target/cisapify*SNAPSHOT.jar /user/share/cisapify/bin/cisapify.jar

WORKDIR /user/share/cisapify

ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "-Dspring.profiles.active=mysql", "bin/cisapify.jar"]