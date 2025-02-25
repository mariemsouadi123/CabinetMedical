package tn.pi.Services;

import tn.pi.entities.Doctor;
import java.util.Optional;

public interface DoctorService {
    Doctor createDoctor(Doctor doctor);
    Optional<Doctor> findByEmail(String email);
    Boolean checkEmail(String email);
    void updateDoctor(Doctor doctor);
}
