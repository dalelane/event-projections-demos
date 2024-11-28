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
package uk.co.dalelane.kafkaprojections.api;

import jakarta.ws.rs.PathParam;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import uk.co.dalelane.kafkaprojections.api.exceptions.NotFoundException;
import uk.co.dalelane.kafkaprojections.data.loosehanger.SensorReading;
import uk.co.dalelane.kafkaprojections.memory.LatestSensorReading;

/**
 * REST API that returns the most recent sensor reading for a specified sensor.
 */
@Path("/sensorreadings")
@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingsApi {

    @Inject
    private LatestSensorReading latestSensorReading;

    @GET()
    @Path("/{sensorid}")
    public SensorReading getSensorReading(@PathParam("sensorid") String sensorId) {
        SensorReading latest = latestSensorReading.getStore().get(sensorId);
        if (latest == null) {
            throw new NotFoundException();
        }
        return latest;
    }
}
