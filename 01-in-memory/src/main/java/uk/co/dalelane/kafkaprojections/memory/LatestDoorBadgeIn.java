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
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import uk.co.dalelane.kafkaprojections.data.loosehanger.DoorBadgeIn;
import uk.co.dalelane.kafkaprojections.data.serdes.ProjectionsSerdes;
import uk.co.dalelane.kafkaprojections.utils.KafkaConfig;

/**
 * Kafka consumer to maintain an in-memory key/value map with
 *  the latest badgein event for every door.
 *
 * -------------------------------------------
 * Input:
 *   Summary:
 *      Door-badgein events that are emitted each time someone
 *       swipes their badge at a door reader
 *   Contains:
 *      Information about the door
 *      Information about the employee who swiped their badge
 *   Events are keyed by:
 *      Unique event id
 *
 * -------------------------------------------
 * Job maintains:
 *      Latest event recorded for each door
 *
 */
@ApplicationScoped
public class LatestDoorBadgeIn {

    private static Logger log = LoggerFactory.getLogger(LatestSensorReading.class);

    @Resource
    private ManagedExecutorService executorService;

    /** Name of the topic to consume events from */
    private static final String INPUT_TOPIC  = "DOOR.BADGEIN";

    /** In-memory map of the latest badge events, keyed by door id */
    private Map<String, DoorBadgeIn> store = new HashMap<>();

    private volatile boolean isRunning = false;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        log.info("Starting projection for {}", INPUT_TOPIC);
        executorService.execute(this::run);
    }

    private void run() {
        try (KafkaConsumer<byte[], DoorBadgeIn> consumer = prepareKafkaConsumer()) {
            isRunning = true;
            while (isRunning) {
                ConsumerRecords<byte[], DoorBadgeIn> records = consumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<byte[], DoorBadgeIn> record : records) {
                    DoorBadgeIn badgeEvent = record.value();
                    store.put(badgeEvent.getDoorId(), badgeEvent);
                }
            }
        }
        catch (KafkaException exc) {
            log.error("Failed to read door badge events", exc);
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


    public Map<String, DoorBadgeIn> getStore() {
        return store;
    }


    private KafkaConsumer<byte[], DoorBadgeIn> prepareKafkaConsumer() throws KafkaException {
        log.debug("preparing consumer properties to read from {}", INPUT_TOPIC);
        Properties consumerProperties = KafkaConfig.getKafkaClientConfiguration();
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                               ByteArrayDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                               ProjectionsSerdes.DOOR_BADGEIN_SERDES.deserializer().getClass().getName());

        log.debug("Creating Kafka consumer for {}", INPUT_TOPIC);
        KafkaConsumer<byte[], DoorBadgeIn> kafkaConsumer = new KafkaConsumer<>(consumerProperties,
                                                                               ProjectionsSerdes.IGNORE.deserializer(),
                                                                               ProjectionsSerdes.DOOR_BADGEIN_SERDES.deserializer());

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
