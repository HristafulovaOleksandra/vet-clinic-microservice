package org.vetclinic.veterinarian;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class VeterinarianRepository {
    
    private final Map<Long, Veterinarian> veterinarians = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public VeterinarianRepository() {
    	save(new Veterinarian(null, "Helen", "Miller", "Surgeon", "helen.miller@vetclinic.com", "(555) 123-4567", true));
    	save(new Veterinarian(null, "Andrew", "Smith", "General Practitioner", "andrew.smith@vetclinic.com", "(555) 234-5678", true));
    	save(new Veterinarian(null, "Natalie", "Taylor", "Dermatologist", "natalia.taylor@vetclinic.com", "(555) 345-6789", false));
}

    public List<Veterinarian> findAll() {
        return new ArrayList<>(veterinarians.values());
    }

    public Veterinarian findById(Long id) {
        return veterinarians.get(id);
    }

    public Veterinarian save(Veterinarian veterinarian) {
        if (veterinarian.getId() == null) {
            veterinarian.setId(idGenerator.incrementAndGet());
        }
        veterinarians.put(veterinarian.getId(), veterinarian);
        return veterinarian;
    }

    public Veterinarian update(Veterinarian veterinarian) {
        if (veterinarian.getId() != null && veterinarians.containsKey(veterinarian.getId())) {
            veterinarians.put(veterinarian.getId(), veterinarian);
            return veterinarian;
        }
        return null;
    }

    public boolean delete(Long id) {
        return veterinarians.remove(id) != null;
    }
}
