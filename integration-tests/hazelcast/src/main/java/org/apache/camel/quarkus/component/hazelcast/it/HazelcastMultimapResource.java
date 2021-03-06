/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.hazelcast.it;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.runtime.StartupEvent;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.apache.camel.component.hazelcast.HazelcastOperation;

import static org.apache.camel.quarkus.component.hazelcast.it.HazelcastRoutes.MOCK_MULTIMAP_ADDED;
import static org.apache.camel.quarkus.component.hazelcast.it.HazelcastRoutes.MOCK_MULTIMAP_DELETED;

@Path("/hazelcast/multi-map")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HazelcastMultimapResource extends AbstractHazelcastMapResource {

    @Override
    public void init(@Observes StartupEvent startupEvent) {
        endpointUri = "hazelcast-multimap:foo-multimap";
        mockAddedEndpoint = MOCK_MULTIMAP_ADDED;
        mockDeletedEndpoint = MOCK_MULTIMAP_DELETED;
    }

    @GET
    @Path("count/{id}")
    public Integer count(@PathParam("id") String id) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(HazelcastConstants.OPERATION, HazelcastOperation.VALUE_COUNT);
        headers.put(HazelcastConstants.OBJECT_ID, id);
        return producerTemplate.requestBodyAndHeaders(endpointUri, "", headers, Integer.class);
    }

    @DELETE
    @Path("value/{id}")
    public Response removeValue(@PathParam("id") String id, String value) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(HazelcastConstants.OPERATION, HazelcastOperation.REMOVE_VALUE);
        headers.put(HazelcastConstants.OBJECT_ID, id);
        producerTemplate.sendBodyAndHeaders(endpointUri, value, headers);
        return Response.accepted().build();
    }
}
