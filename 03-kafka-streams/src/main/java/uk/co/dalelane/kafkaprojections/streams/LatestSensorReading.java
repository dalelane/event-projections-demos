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
package uk.co.dalelane.kafkaprojections.streams;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.dalelane.kafkaprojections.data.loosehanger.SensorReading;
import uk.co.dalelane.kafkaprojections.data.serdes.ProjectionsSerdes;

/**
 * Kafka Streams job to maintain a persistent key/value store with
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
 * Job maintains:
 *      Latest sensor reading recorded from each sensor
 *
 */
public class LatestSensorReading {

    private static Logger log = LoggerFactory.getLogger(LatestSensorReading.class);

    /** Name of the topic to consume events from */
    private static final String INPUT_TOPIC  = "SENSOR.READINGS";

    /** Name to give to the key/value store to maintain */
    private static final String STORE_NAME = "LatestSensorReadingsStore";

    /** Key/Value store of sensor readings, keyed by the sensor id. */
    public static final StoreQueryParameters<ReadOnlyKeyValueStore<String, SensorReading>> STORE =
        StoreQueryParameters.fromNameAndType(STORE_NAME, QueryableStoreTypes.keyValueStore());



    public static void create(final StreamsBuilder builder) {
        log.info("Creating latest sensor readings stream");

        builder
            // consume the input topic with sensor reading events
            .stream(INPUT_TOPIC, Consumed.with(ProjectionsSerdes.SENSOR_ID_SERDES,
                                               ProjectionsSerdes.SENSOR_READING_SERDES))
            // group the events by the ID of the sensor that recorded the reading
            .groupByKey()
            // keep the latest event from each sensor
            .reduce((olderEvent, laterEvent) -> laterEvent,
                    Named.as("keep_latest_sensor_readings"),
                    Materialized.<String, SensorReading,
                                  KeyValueStore<Bytes, byte[]>> as (STORE_NAME));
    }
}
