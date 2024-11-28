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
package uk.co.dalelane.kafkaprojections.api.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import uk.co.dalelane.kafkaprojections.memory.LatestDoorBadgeIn;
import uk.co.dalelane.kafkaprojections.memory.LatestSensorReading;

import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

/**
 * Checks that the Kafka consumer is running before
 *  enabling access to the REST API.
 */
@Readiness
@ApplicationScoped
public class ReadinessProbe implements HealthCheck {

    private static final String READINESS_CHECK = "readiness";

    @Inject
    private LatestSensorReading latestSensorReading;

    @Inject
    private LatestDoorBadgeIn latestDoorBadgeIn;


    @Override
    public HealthCheckResponse call() {
        return latestSensorReading.isRunning() && latestDoorBadgeIn.isRunning() ?
            HealthCheckResponse.up(READINESS_CHECK) :
            HealthCheckResponse.down(READINESS_CHECK);
    }
}
