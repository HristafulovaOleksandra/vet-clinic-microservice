package org.vetclinic.pet;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.vetclinic.pet.client.OwnerRestClient;
import org.vetclinic.pet.client.OwnerDTO;

import java.util.List;

@Path("/api/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PetResource {

    private final PetRepository petRepository;
    
    @RestClient
    OwnerRestClient ownerRestClient;

    public PetResource(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @GET
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getPetById(@PathParam("id") Long id) {
        Pet pet = petRepository.findById(id);
        if (pet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(pet).build();
    }

    @GET
    @Path("/owner/{ownerId}")
    public List<Pet> getPetsByOwnerId(@PathParam("ownerId") Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    @GET
    @Path("/{id}/with-owner")
    public Response getPetWithOwner(@PathParam("id") Long id) {
        Pet pet = petRepository.findById(id);
        if (pet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            OwnerDTO owner = ownerRestClient.getOwnerById(pet.getOwnerId());
            PetWithOwnerDTO result = new PetWithOwnerDTO(pet, owner);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("Cannot fetch owner information")
                .build();
        }
    }

    @POST
    public Response createPet(Pet pet) {
        if (pet.getOwnerId() != null) {
            try {
                OwnerDTO owner = ownerRestClient.getOwnerById(pet.getOwnerId());
                if (owner == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Owner not found")
                        .build();
                }
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cannot verify owner")
                    .build();
            }
        }
        
        Pet created = petRepository.save(pet);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePet(@PathParam("id") Long id, Pet pet) {
        Pet existing = petRepository.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        pet.setId(id);
        Pet updated = petRepository.update(pet);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePet(@PathParam("id") Long id) {
        boolean deleted = petRepository.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    public static class PetWithOwnerDTO {
        private Pet pet;
        private OwnerDTO owner;

        public PetWithOwnerDTO() {}

        public PetWithOwnerDTO(Pet pet, OwnerDTO owner) {
            this.pet = pet;
            this.owner = owner;
        }

        public Pet getPet() { return pet; }
        public void setPet(Pet pet) { this.pet = pet; }
        
        public OwnerDTO getOwner() { return owner; }
        public void setOwner(OwnerDTO owner) { this.owner = owner; }
    }
}
