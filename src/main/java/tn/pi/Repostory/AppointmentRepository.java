package tn.pi.Repostory;

import tn.pi.entities.Appointment;
import tn.pi.entities.Doctor;
import tn.pi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByUser(User user);
}
