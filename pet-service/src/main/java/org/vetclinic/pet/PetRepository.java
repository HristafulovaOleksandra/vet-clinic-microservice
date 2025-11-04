package org.vetclinic.pet;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ApplicationScoped
public class PetRepository {
    
    private final Map<Long, Pet> pets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public PetRepository() {
        save(new Pet(null, "Whiskers", "Cat", "Persian", LocalDate.of(2020, 3, 15), "Male", 1L, "Orange", 9.9));
	save(new Pet(null, "Rex", "Dog", "German Shepherd", LocalDate.of(2019, 7, 20), "Male", 2L, "Black and Tan", 77.2));
	save(new Pet(null, "Lucy", "Cat", "British Shorthair", LocalDate.of(2021, 1, 10), "Female", 1L, "Gray", 8.4));
    }

    public List<Pet> findAll() {
        return new ArrayList<>(pets.values());
    }

    public Pet findById(Long id) {
        return pets.get(id);
    }

    public List<Pet> findByOwnerId(Long ownerId) {
        return pets.values().stream()
            .filter(pet -> pet.getOwnerId().equals(ownerId))
            .collect(Collectors.toList());
    }

    public Pet save(Pet pet) {
        if (pet.getId() == null) {
            pet.setId(idGenerator.incrementAndGet());
        }
        pets.put(pet.getId(), pet);
        return pet;
    }

    public Pet update(Pet pet) {
        if (pet.getId() != null && pets.containsKey(pet.getId())) {
            pets.put(pet.getId(), pet);
            return pet;
        }
        return null;
    }

    public boolean delete(Long id) {
        return pets.remove(id) != null;
    }
}
