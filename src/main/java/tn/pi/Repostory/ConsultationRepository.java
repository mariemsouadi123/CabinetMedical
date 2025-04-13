package tn.pi.Repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.entities.Consultation;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByAppointmentId(Long appointmentId);
    List<Consultation> findByUserId(Long userId);
    List<Consultation> findByDoctorId(Long doctorId);
}
