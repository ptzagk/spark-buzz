FROM java:8

RUN adduser --disabled-password --gecos '' buzz

COPY target/scala-2.11/jobbuzz-twitter.jar /home/buzz/
COPY bin/run /home/buzz/

USER buzz

WORKDIR /home/buzz

ENTRYPOINT ./run
