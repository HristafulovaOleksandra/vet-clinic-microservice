package org.vetclinic.owner;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OwnerResource {

    private final OwnerRepository ownerRepository;

    public OwnerResource(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @GET
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getOwnerById(@PathParam("id") Long id) {
        Owner owner = ownerRepository.findById(id);
        if (owner == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(owner).build();
    }

    @POST
    public Response createOwner(Owner owner) {
        Owner created = ownerRepository.save(owner);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOwner(@PathParam("id") Long id, Owner owner) {
        Owner existing = ownerRepository.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        owner.setId(id);
        Owner updated = ownerRepository.update(owner);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOwner(@PathParam("id") Long id) {
        boolean deleted = ownerRepository.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
