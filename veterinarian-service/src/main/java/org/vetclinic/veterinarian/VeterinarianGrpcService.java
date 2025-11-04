package org.vetclinic.veterinarian;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.vetclinic.grpc.*;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class VeterinarianGrpcService implements VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    public VeterinarianGrpcService(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
    }

    @Override
    public Uni<VeterinarianResponse> getVeterinarian(VeterinarianRequest request) {
        return Uni.createFrom().item(() -> {
            Veterinarian vet = veterinarianRepository.findById(request.getId());
            if (vet == null) {
                throw new RuntimeException("Veterinarian not found with id: " + request.getId());
            }
            return mapToResponse(vet);
        });
    }

    @Override
    public Uni<AvailabilityResponse> checkAvailability(AvailabilityRequest request) {
        return Uni.createFrom().item(() -> {
            Veterinarian vet = veterinarianRepository.findById(request.getVeterinarianId());
            if (vet == null) {
                return AvailabilityResponse.newBuilder()
                    .setAvailable(false)
                    .setMessage("Veterinarian not found")
                    .build();
            }
            
            boolean available = vet.isAvailable();
            String message = available ? 
                "Veterinarian " + vet.getFirstName() + " " + vet.getLastName() + " is available" : 
                "Veterinarian " + vet.getFirstName() + " " + vet.getLastName() + " is not available";
            
            return AvailabilityResponse.newBuilder()
                .setAvailable(available)
                .setMessage(message)
                .build();
        });
    }

    @Override
    public Uni<VeterinarianListResponse> listVeterinarians(Empty request) {
        return Uni.createFrom().item(() -> {
            List<Veterinarian> vets = veterinarianRepository.findAll();
            List<VeterinarianResponse> responses = vets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
            
            return VeterinarianListResponse.newBuilder()
                .addAllVeterinarians(responses)
                .build();
        });
    }

    private VeterinarianResponse mapToResponse(Veterinarian vet) {
        return VeterinarianResponse.newBuilder()
            .setId(vet.getId())
            .setFirstName(vet.getFirstName())
            .setLastName(vet.getLastName())
            .setSpecialization(vet.getSpecialization())
            .setEmail(vet.getEmail())
            .setPhone(vet.getPhone())
            .setAvailable(vet.isAvailable())
            .build();
    }
}
