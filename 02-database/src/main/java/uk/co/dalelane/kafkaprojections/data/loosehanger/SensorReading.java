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

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name="sensorreadings")
@NamedQuery(
    name = "SensorReading.findSensorReading",
    query = "SELECT e FROM SensorReading e WHERE e.sensorid = :sensorid"
)
public class SensorReading implements Serializable {

    @NotNull
    @Id
    @Column(name = "key")
    private String sensorid;

    @NotNull
    @Column(name = "sensortime")
    private String sensortime;

    @NotNull
    @Column(name = "temperature")
    private Double temperature;

    @NotNull
    @Column(name = "humidity")
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

    public void setSensortime(String sensortime) {
        this.sensortime = sensortime;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sensortime == null) ? 0 : sensortime.hashCode());
        result = prime * result + ((sensorid == null) ? 0 : sensorid.hashCode());
        result = prime * result + ((temperature == null) ? 0 : temperature.hashCode());
        result = prime * result + ((humidity == null) ? 0 : humidity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensorReading other = (SensorReading) obj;
        if (sensortime == null) {
            if (other.sensortime != null)
                return false;
        } else if (!sensortime.equals(other.sensortime))
            return false;
        if (sensorid == null) {
            if (other.sensorid != null)
                return false;
        } else if (!sensorid.equals(other.sensorid))
            return false;
        if (temperature == null) {
            if (other.temperature != null)
                return false;
        } else if (!temperature.equals(other.temperature))
            return false;
        if (humidity == null) {
            if (other.humidity != null)
                return false;
        } else if (!humidity.equals(other.humidity))
            return false;
        return true;
    }
}
