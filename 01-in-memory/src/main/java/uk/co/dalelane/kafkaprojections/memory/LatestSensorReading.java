/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.dalelane.kafkaprojections.memory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import uk.co.dalelane.kafkaprojections.data.loosehanger.SensorReading;
import uk.co.dalelane.kafkaprojections.data.serdes.ProjectionsSerdes;
import uk.co.dalelane.kafkaprojections.utils.KafkaConfig;

/**
 * Kafka consumer to maintain an in-memory key/value map with
 *  the latest sensor event for every sensor.
 *
 * -------------------------------------------
 * Input:
 *   Summary:
 *      Sensor reading events
 *   Contains:
 *      Temperature and humidity readings
 *      Information about the sensor
 *   Events are keyed by:
 *      Id of the sensor
 *
 * -------------------------------------------
 * Consumer maintains:
 *      Latest sensor reading recorded from each sensor
 *
 */
@ApplicationScoped
public class LatestSensorReading {

    private static Logger log = LoggerFactory.getLogger(LatestSensorReading.class);

    @Resource
    private ManagedExecutorService executorService;

    /** Name of the topic to consume events from */
    private static final String INPUT_TOPIC  = "SENSOR.READINGS";

    /** In-memory map of the latest sensor reading, keyed by sensor id */
    private Map<String, SensorReading> store = new HashMap<>();

    private volatile boolean isRunning = false;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        log.info("Starting projection for {}", INPUT_TOPIC);
        executorService.execute(this::run);
    }


    private void run() {
        try (KafkaConsumer<String, SensorReading> consumer = prepareKafkaConsumer()) {
            isRunning = true;
            while (isRunning) {
                ConsumerRecords<String, SensorReading> records = consumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<String, SensorReading> record : records) {
                    store.put(record.key(), record.value());
                }
            }
        }
        catch (KafkaException exc) {
            log.error("Failed to read sensor reading events", exc);
        }

        isRunning = false;
    }


    public void onStop(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        log.info("Stopping projection for {}", INPUT_TOPIC);
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Map<String, SensorReading> getStore() {
        return store;
    }


    private KafkaConsumer<String, SensorReading> prepareKafkaConsumer() throws KafkaException {
        log.debug("preparing consumer properties to read from {}", INPUT_TOPIC);
        Properties consumerProperties = KafkaConfig.getKafkaClientConfiguration();
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                               StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                               ProjectionsSerdes.SENSOR_READING_SERDES.deserializer().getClass().getName());

        log.debug("Creating Kafka consumer for {}", INPUT_TOPIC);
        KafkaConsumer<String, SensorReading> kafkaConsumer = new KafkaConsumer<>(consumerProperties,
                                                                                 ProjectionsSerdes.SENSOR_ID_SERDES.deserializer(),
                                                                                 ProjectionsSerdes.SENSOR_READING_SERDES.deserializer());

        log.debug("Rewinding to the beginning of the topic partitions");
        List<TopicPartition> partitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : kafkaConsumer.partitionsFor(INPUT_TOPIC)) {
            partitions.add(new TopicPartition(INPUT_TOPIC, partitionInfo.partition()));
        }
        kafkaConsumer.assign(partitions);
        for (TopicPartition partition : partitions) {
            kafkaConsumer.seekToBeginning(Collections.singletonList(partition));
        }

        return kafkaConsumer;
    }
}
