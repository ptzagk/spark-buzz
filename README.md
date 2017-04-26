# job buzz

demonstrates features of spark structured streaming.

## projects

### twitter

the twitter project contains a program that opens a connection to the twitter status stream with a set of job related track filters and writes all received tweets to a kafka topic.

* track filters are configurable through the `--tweet-filters` parameter (the default is *job*, *jobs*, *hire*, *career*).
* the kafka topic is configurable through the `--kafka-topic` parameter (the default is *job-tweets*).

### spark

the spark project contains a structured streaming application that connects to the kafka topic and attaches a few write streams 

* the *all tweets* query demonstrates a basic append stream on a file sink. it writes the `id_str` and `text` properties of every received tweet to a json file sink.
* the *ngram counts* query demonstrates the complete output mode as well as the foreach sink. 
* the *company counts* query demonstrates joining a streaming data frame to a static data frame.

all write streams utilize checkpointing for reliability. *ngram counts* and *company counts* use triggers to delay writes for a period of time.

---

## running

install these prerequisites at a minimum

* scala
* sbt 
* docker

then run `cp env.template .env`, register a twitter application and fill in the appropriate environment variables in `.env`.

then...

* from the repo root run `sbt assembly`
* then `bin/run`

