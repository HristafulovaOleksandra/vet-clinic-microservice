package org.vetclinic.appointment;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.vetclinic.appointment.client.PetRestClient;
import org.vetclinic.appointment.client.PetDTO;
import org.vetclinic.grpc.*;
import io.quarkus.grpc.GrpcClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/api/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentResource {

    private final AppointmentRepository appointmentRepository;

    @RestClient
    PetRestClient petRestClient;

    @GrpcClient("veterinarian-service")
    VeterinarianService veterinarianGrpcClient;

    public AppointmentResource(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GET
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getAppointmentById(@PathParam("id") Long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(appointment).build();
    }

    @POST
    public Response createAppointment(Appointment appointment) {
        System.out.println("=== Creating appointment ===");
        System.out.println("Pet ID: " + appointment.getPetId());
        System.out.println("Veterinarian ID: " + appointment.getVeterinarianId());


        try {
            System.out.println("Calling Pet Service...");
            PetDTO pet = petRestClient.getPetById(appointment.getPetId());
            System.out.println("Pet received: " + pet);

            if (pet == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Pet not found")
                        .build();
            }

            System.out.println("Pet found: " + pet.getName());

        } catch (Exception e) {
            System.err.println("Error calling Pet Service: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error checking pet: " + e.getClass().getName() + " - " + e.getMessage())
                    .build();
        }


        try {
            System.out.println("Calling Veterinarian Service via gRPC...");
            AvailabilityRequest availRequest = AvailabilityRequest.newBuilder()
                    .setVeterinarianId(appointment.getVeterinarianId())
                    .setDate(appointment.getAppointmentDate().toString())
                    .setTime(appointment.getAppointmentTime())
                    .build();

            AvailabilityResponse availResponse = veterinarianGrpcClient
                    .checkAvailability(availRequest)
                    .await().indefinitely();

            System.out.println("Veterinarian availability: " + availResponse.getAvailable());
            System.out.println("Message: " + availResponse.getMessage());

            if (!availResponse.getAvailable()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Veterinarian not available: " + availResponse.getMessage())
                        .build();
            }

        } catch (Exception e) {
            System.err.println("Error calling Veterinarian Service: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error checking veterinarian: " + e.getMessage())
                    .build();
        }


        Appointment created = appointmentRepository.save(appointment);
        System.out.println("Appointment created with ID: " + created.getId());

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}/details")
    public CompletionStage<Response> getAppointmentDetails(@PathParam("id") Long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            return CompletableFuture.completedFuture(
                    Response.status(Response.Status.NOT_FOUND).build()
            );
        }


        VeterinarianRequest vetRequest = VeterinarianRequest.newBuilder()
                .setId(appointment.getVeterinarianId())
                .build();

        return veterinarianGrpcClient.getVeterinarian(vetRequest)
                .onItem().transform(vetResponse -> {
                    AppointmentDetailsDTO details = new AppointmentDetailsDTO();
                    details.setAppointment(appointment);
                    details.setVeterinarianName(vetResponse.getFirstName() + " " + vetResponse.getLastName());
                    details.setVeterinarianSpecialization(vetResponse.getSpecialization());


                    try {
                        var pet = petRestClient.getPetById(appointment.getPetId());
                        if (pet != null) {
                            details.setPetName(pet.getName());
                            details.setPetSpecies(pet.getSpecies());
                        }
                    } catch (Exception e) {
                        System.err.println("Error fetching pet details: " + e.getMessage());
                    }

                    return Response.ok(details).build();
                })
                .subscribeAsCompletionStage();
    }

    @PUT
    @Path("/{id}")
    public Response updateAppointment(@PathParam("id") Long id, Appointment appointment) {
        Appointment existing = appointmentRepository.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        appointment.setId(id);
        Appointment updated = appointmentRepository.update(appointment);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAppointment(@PathParam("id") Long id) {
        boolean deleted = appointmentRepository.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}