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
package uk.co.dalelane.kafkaprojections;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.dalelane.kafkaprojections.api.health.StartupProbe;
import uk.co.dalelane.kafkaprojections.data.loosehanger.DoorBadgeIn;
import uk.co.dalelane.kafkaprojections.data.loosehanger.SensorReading;
import uk.co.dalelane.kafkaprojections.utils.KafkaConfig;
import uk.co.dalelane.kafkaprojections.streams.LatestDoorBadgeIn;
import uk.co.dalelane.kafkaprojections.streams.LatestSensorReading;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class KafkaProjectionsService extends Application {

    private static Logger log = LoggerFactory.getLogger(KafkaProjectionsService.class);

    private static KafkaStreams streams = null;


    public KafkaProjectionsService() {
        if (streams != null) {
            stop();
        }

        log.info("Getting connection details for Kafka");
        final Properties props = KafkaConfig.getKafkaClientConfiguration();

        log.info("Starting Kafka Streams jobs");
        final StreamsBuilder builder = new StreamsBuilder();
        LatestSensorReading.create(builder);
        LatestDoorBadgeIn.create(builder);

        log.info("Preparing Streams application");
        final Topology topology = builder.build();
        log.info(topology.describe().toString());

        log.info("Starting Streams application");
        streams = new KafkaStreams(topology, props);
        streams.start();

        log.info("Marking REST API as ready for queries");
        StartupProbe.setStarted(true);
    }


    @PreDestroy
    public void stop() {
        log.info("Stopping Streams application");
        if (streams != null) {
            streams.close();
            streams = null;
        }
    }


    public static ReadOnlyKeyValueStore<String, SensorReading> getLatestSensorReadingsStore() {
        return streams.store(LatestSensorReading.STORE);
    }
    public static ReadOnlyKeyValueStore<String, DoorBadgeIn> getLatestDoorBadgeInStore() {
        return streams.store(LatestDoorBadgeIn.STORE);
    }

    public static boolean isRunning() {
        return streams != null &&
            KafkaStreams.State.RUNNING.equals(streams.state());
    }
}
