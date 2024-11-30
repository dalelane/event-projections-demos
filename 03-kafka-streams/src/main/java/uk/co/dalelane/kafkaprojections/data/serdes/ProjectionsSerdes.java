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
package uk.co.dalelane.kafkaprojections.data.serdes;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import uk.co.dalelane.kafkaprojections.data.loosehanger.DoorBadgeIn;
import uk.co.dalelane.kafkaprojections.data.loosehanger.SensorReading;

public class ProjectionsSerdes {

    private static <T> Serde<T> createSerdes(Class<T> clazz) {
        GsonSerializer<T> serializer = new GsonSerializer<>(clazz);
        GsonDeserializer<T> deserializer = new GsonDeserializer<>(clazz);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static final Serde<String> SENSOR_ID_SERDES = Serdes.String();
    public static final Serde<SensorReading> SENSOR_READING_SERDES = createSerdes(SensorReading.class);

    public static final Serde<byte[]> IGNORE = Serdes.ByteArray();

    public static final Serde<String> DOOR_ID_SERDES = Serdes.String();
    public static final Serde<DoorBadgeIn> DOOR_BADGEIN_SERDES = createSerdes(DoorBadgeIn.class);
}
