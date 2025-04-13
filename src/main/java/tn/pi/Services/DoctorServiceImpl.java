package tn.pi.Services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tn.pi.Repostory.DoctorRepository;
import tn.pi.entities.Doctor;

import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        doctor.setPassword(doctor.getPassword());
        doctorRepository.save(doctor);
        return doctor;
    }

    @Override
    public Optional<Doctor> findByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    @Override
    public Boolean checkEmail(String email) {
        return doctorRepository.findByEmail(email).isPresent();
    }
    @Override
    @Transactional
    public void updateDoctor(Doctor doctor){
        doctorRepository.save(doctor);
    }
}
