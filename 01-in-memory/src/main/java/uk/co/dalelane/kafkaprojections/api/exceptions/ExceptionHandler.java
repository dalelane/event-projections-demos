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
package uk.co.dalelane.kafkaprojections.api.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Prepare a consistent API response for exceptions and errors.
 */
@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {

    private static Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Uncaught exception", exception);

        final Map<String, String> error = new HashMap<>();
        error.put("type", exception.getClass().getSimpleName());
        error.put("message", exception.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
