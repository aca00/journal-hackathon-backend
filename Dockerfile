FROM openjdk:17
ADD target/journal-0.0.1-SNAPSHOT.jar .
ENTRYPOINT java -jar journal-0.0.1-SNAPSHOT.jar
