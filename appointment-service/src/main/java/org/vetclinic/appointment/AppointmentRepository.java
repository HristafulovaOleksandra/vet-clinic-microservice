package org.vetclinic.appointment;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class AppointmentRepository {
    
    private final Map<Long, Appointment> appointments = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public AppointmentRepository() {
        save(new Appointment(null, 1L, 1L, LocalDate.now().plusDays(1), "10:00", 
    "Vaccination", "Scheduled", "Routine vaccination"));
save(new Appointment(null, 2L, 2L, LocalDate.now().plusDays(2), "14:30", 
    "Check-up", "Scheduled", "General check-up"));
save(new Appointment(null, 3L, 1L, LocalDate.now().plusDays(3), "11:00", 
    "Surgery", "Scheduled", "Scheduled sterilization"));
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments.values());
    }

    public Appointment findById(Long id) {
        return appointments.get(id);
    }

    public Appointment save(Appointment appointment) {
        if (appointment.getId() == null) {
            appointment.setId(idGenerator.incrementAndGet());
        }
        appointments.put(appointment.getId(), appointment);
        return appointment;
    }

    public Appointment update(Appointment appointment) {
        if (appointment.getId() != null && appointments.containsKey(appointment.getId())) {
            appointments.put(appointment.getId(), appointment);
            return appointment;
        }
        return null;
    }

    public boolean delete(Long id) {
        return appointments.remove(id) != null;
    }
}
