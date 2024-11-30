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

import uk.co.dalelane.kafkaprojections.data.loosehanger.DoorBadgeIn;
import uk.co.dalelane.kafkaprojections.data.serdes.ProjectionsSerdes;

/**
 * Kafka Streams job to maintain a persistent key/value store with
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
public class LatestDoorBadgeIn {

    private static Logger log = LoggerFactory.getLogger(LatestSensorReading.class);

    /** Name of the topic to consume events from */
    private static final String INPUT_TOPIC  = "DOOR.BADGEIN";

    /** Name to give to the key/value store to maintain */
    private static final String STORE_NAME = "LatestDoorBadgeInStore";

    /** Key/Value store of door-badge events, keyed by the door id. */
    public static final StoreQueryParameters<ReadOnlyKeyValueStore<String, DoorBadgeIn>> STORE =
        StoreQueryParameters.fromNameAndType(STORE_NAME, QueryableStoreTypes.keyValueStore());



    public static void create(final StreamsBuilder builder) {
        log.info("Creating latest door badge events stream");

        builder
            // consume the input topic with door badge events
            //   note that the existing key (unique record ids) are not helpful to us
            //    so we will be ignoring it
            .stream(INPUT_TOPIC, Consumed.with(ProjectionsSerdes.IGNORE,
                                               ProjectionsSerdes.DOOR_BADGEIN_SERDES))
            // re-key the events, so that they are grouped by the ID of the door
            //   where the event was recorded
            .groupBy((recordId, doorBadgeIn) -> doorBadgeIn.getDoorId())
            // keep the latest event from each door
            .reduce((olderEvent, laterEvent) -> laterEvent,
                    Named.as("keep_latest_door_badgeins"),
                    Materialized.<String, DoorBadgeIn,
                                  KeyValueStore<Bytes, byte[]>> as (STORE_NAME));
    }
}
