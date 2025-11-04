package org.vetclinic.veterinarian;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/veterinarians")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VeterinarianResource {

    private final VeterinarianRepository veterinarianRepository;

    public VeterinarianResource(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
}
@GET
public List<Veterinarian> getAllVeterinarians() {
    return veterinarianRepository.findAll();
}

@GET
@Path("/{id}")
public Response getVeterinarianById(@PathParam("id") Long id) {
    Veterinarian vet = veterinarianRepository.findById(id);
    if (vet == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(vet).build();
}

@POST
public Response createVeterinarian(Veterinarian veterinarian) {
    Veterinarian created = veterinarianRepository.save(veterinarian);
    return Response.status(Response.Status.CREATED).entity(created).build();
}

@PUT
@Path("/{id}")
public Response updateVeterinarian(@PathParam("id") Long id, Veterinarian veterinarian) {
    Veterinarian existing = veterinarianRepository.findById(id);
    if (existing == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    veterinarian.setId(id);
    Veterinarian updated = veterinarianRepository.update(veterinarian);
    return Response.ok(updated).build();
}

@DELETE
@Path("/{id}")
public Response deleteVeterinarian(@PathParam("id") Long id) {
    boolean deleted = veterinarianRepository.delete(id);
    if (!deleted) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.noContent().build();
}
}
