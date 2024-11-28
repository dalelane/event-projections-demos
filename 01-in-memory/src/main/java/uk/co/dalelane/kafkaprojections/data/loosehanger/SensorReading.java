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
package uk.co.dalelane.kafkaprojections.data.loosehanger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Events on the "SENSOR.READINGS" topic are JSON strings that look like this:
 *
 *  {
 *      "sensortime": "Tue Nov 26 10:53:41 GMT 2024",
 *      "sensorid": "G-3-14",
 *      "temperature": 19.9,
 *      "humidity": 50
 *  }
 */
public class SensorReading {

    @SerializedName("sensortime")
    @Expose
    private String sensortime;

    @SerializedName("sensorid")
    @Expose
    private String sensorid;

    @SerializedName("temperature")
    @Expose
    private Double temperature;

    @SerializedName("humidity")
    @Expose
    private Integer humidity;


    public String getSensorTime() {
        return sensortime;
    }

    public String getSensorId() {
        return sensorid;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }
}
