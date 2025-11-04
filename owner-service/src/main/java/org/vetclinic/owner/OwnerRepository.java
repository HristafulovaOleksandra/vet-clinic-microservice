package org.vetclinic.owner;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class OwnerRepository {
    
    private final Map<Long, Owner> owners = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public OwnerRepository() {
        save(new Owner(null, "John", "Peterson", "john.peterson@example.com", "(555) 123-4567", "0 Main St, New York, NY 10001"));
        save(new Owner(null, "Mary", "Smith", "mary.smith@example.com", "(555) 234-5678", "25 Park Ave, Chicago, IL 60601"));
        save(new Owner(null, "Alex", "Johnson", "alex.johnson@example.com", "(555) 345-6789", "5 University St, Los Angeles, CA 90001"));
    }
    public List<Owner> findAll() {
        return new ArrayList<>(owners.values());
    }

    public Owner findById(Long id) {
        return owners.get(id);
    }

    public Owner save(Owner owner) {
        if (owner.getId() == null) {
            owner.setId(idGenerator.incrementAndGet());
        }
        owners.put(owner.getId(), owner);
        return owner;
    }

    public Owner update(Owner owner) {
        if (owner.getId() != null && owners.containsKey(owner.getId())) {
            owners.put(owner.getId(), owner);
            return owner;
        }
        return null;
    }

    public boolean delete(Long id) {
        return owners.remove(id) != null;
    }
}
