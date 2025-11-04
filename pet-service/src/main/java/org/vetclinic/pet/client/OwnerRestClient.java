package org.vetclinic.pet.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/owners")
@RegisterRestClient(configKey = "owner-service")
public interface OwnerRestClient {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    OwnerDTO getOwnerById(@PathParam("id") Long id);
}
