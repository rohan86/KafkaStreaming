package com.github.rohan86.kafkastream;



import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.security.Key;
import java.util.Arrays;
import java.util.Properties;

 public class Wordcount
  {

      public static void main(String[] args) {
          Properties config = new Properties();
          config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
          config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
          config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
          config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
          config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

          KStreamBuilder builder = new KStreamBuilder();
          // 1 - stream from Kafka

          KStream<String, String> textLines = builder.stream("word-count-input");
          KTable<String, Long> wordCounts = textLines
                  // 2 - map values to lowercase
                  .mapValues(textLine -> textLine.toLowerCase())
                  // can be alternatively written as:
                  // .mapValues(String::toLowerCase)
                  // 3 - flatmap values split by space
                  .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))
                  // 4 - select key to apply a key (we discard the old key)
                  .selectKey((key, word) -> "This is total")
                  // 5 - group by key before aggregation
                  .groupByKey()
                  // 6 - count occurences
                  .count("Counts");

          // 7 - to in order to write the results back to kafka
          wordCounts.to(Serdes.String(), Serdes.Long(), "word-count-output");

          KafkaStreams streams = new KafkaStreams(builder, config);
          streams.start();



          // shutdown hook to correctly close the streams application
          Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

          // Update:
          // print the topology every 10 seconds for learning purposes
          while(true){
              System.out.println(streams.toString());
              try {
                  Thread.sleep(5000);
              } catch (InterruptedException e) {
                  break;
              }
          }


      }
  }