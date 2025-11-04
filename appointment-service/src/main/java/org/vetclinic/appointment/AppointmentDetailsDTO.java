package org.vetclinic.appointment;

public class AppointmentDetailsDTO {
    private Appointment appointment;
    private String veterinarianName;
    private String veterinarianSpecialization;
    private String petName;
    private String petSpecies;

    public AppointmentDetailsDTO() {}

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    
    public String getVeterinarianName() { return veterinarianName; }
    public void setVeterinarianName(String veterinarianName) { this.veterinarianName = veterinarianName; }
    
    public String getVeterinarianSpecialization() { return veterinarianSpecialization; }
    public void setVeterinarianSpecialization(String veterinarianSpecialization) { 
        this.veterinarianSpecialization = veterinarianSpecialization; 
    }
    
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    
    public String getPetSpecies() { return petSpecies; }
    public void setPetSpecies(String petSpecies) { this.petSpecies = petSpecies; }
}
