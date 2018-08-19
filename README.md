# KafkaStreaming

@pre

Create topic
------------

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output


Kafka Console Producer 
----------------------

bin/kafka-console-producer.sh  --broker-list localhost:9092  --topic word-count-input


Kafka Console Consumer
--------------------

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic word-count-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
