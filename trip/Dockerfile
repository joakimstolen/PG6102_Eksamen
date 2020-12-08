FROM adoptopenjdk/openjdk11:alpine

COPY target/trip.jar .

CMD java -Dspring.profiles.active=FakeData  -jar trip.jar