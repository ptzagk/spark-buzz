# job buzz

demonstrates features of spark structured streaming.

## subprojects

### twitter

the twitter project contains a program that opens a connection to the twitter status stream with a set of track filters and writes all received tweets to a kafka topic.

* track filters are configurable through the `--tweet-filters` parameter (the default is *job*, *jobs*, *hire*, *career*).
* the kafka topic is configurable through the `--kafka-topic` parameter (the default is *job-tweets*).

### spark

the spark project contains a spark structured streaming application that connects to the kafka topic and attaches a few write streams 

* the *all tweets* query demonstrates a basic append stream on a file sink. it writes the `id_str` and `text` properties of every received tweet to a json file sink.
* the *ngram counts* query demonstrates the complete output mode as well as the foreach sink. 
* the *company counts* query demonstrates joining a streaming data frame to a static data frame.

all demonstrated write streams utilize checkpointing for reliability. *ngram counts* and *company counts* use triggers to delay writes for a period of time.

---

## docker



---

## bin


