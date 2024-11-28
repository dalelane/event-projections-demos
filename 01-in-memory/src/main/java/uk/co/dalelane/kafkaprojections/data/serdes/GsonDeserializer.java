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

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer<T> implements Deserializer<T> {

    private Class<T> target;
    private Gson jsonParser;

    public GsonDeserializer(Class<T> targetClass) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        jsonParser = gsonBuilder.create();
        target = targetClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        String stringData = new String(data, StandardCharsets.UTF_8);
        return jsonParser.fromJson(stringData, target);
    }
}