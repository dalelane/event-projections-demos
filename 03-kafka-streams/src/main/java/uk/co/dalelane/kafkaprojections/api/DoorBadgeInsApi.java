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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import uk.co.dalelane.kafkaprojections.KafkaProjectionsService;
import uk.co.dalelane.kafkaprojections.api.exceptions.NotFoundException;
import uk.co.dalelane.kafkaprojections.data.loosehanger.DoorBadgeIn;

/**
 * REST API that returns the most recent badge event for a specified door.
 */
@Path("/badgeins")
@Produces(MediaType.APPLICATION_JSON)
public class DoorBadgeInsApi {

    @GET()
    @Path("/{doorid}")
    public DoorBadgeIn getDoorBadgeIn(@PathParam("doorid") String doorid) {
        DoorBadgeIn latest = KafkaProjectionsService.getLatestDoorBadgeInStore().get(doorid);
        if (latest == null) {
            throw new NotFoundException();
        }
        return latest;
    }

}
