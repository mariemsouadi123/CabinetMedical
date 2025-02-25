package tn.pi.Repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.entities.Doctor;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);

    // Add this method to search by username
    Doctor findByUsername(String username);

}
