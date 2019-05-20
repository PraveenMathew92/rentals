FROM openjdk:8-jre-alpine
WORKDIR /rentals
VOLUME /tmp
COPY build/libs/rentals-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
RUN exec echo $(pwd)
ENTRYPOINT ["java","-jar","rentals-0.0.1-SNAPSHOT.jar"]