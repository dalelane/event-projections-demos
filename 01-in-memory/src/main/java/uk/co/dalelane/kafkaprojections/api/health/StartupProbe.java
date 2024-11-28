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
import org.eclipse.microprofile.health.Startup;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

/**
 * Waits for the Kafka consumer app to be created before declaring
 * that startup is complete.
 */
@Startup
@ApplicationScoped
public class StartupProbe implements HealthCheck {

    private static final String STARTUP_CHECK = "started";

    private static boolean started = false;

    @Override
    public HealthCheckResponse call() {
        return started ?
            HealthCheckResponse.up(STARTUP_CHECK) :
            HealthCheckResponse.down(STARTUP_CHECK);
    }

    public static void setStarted(boolean isStarted) {
        started = isStarted;
    }
}
