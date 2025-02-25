//package tn.pi.Services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import tn.pi.entities.Consultation;
//import tn.pi.Repostory.ConsultationRepository;
//
//import java.util.List;
//
//@Service
//public class ConsultationService {
//
//    @Autowired
//    private ConsultationRepository consultationRepository;
//
//    public Consultation save(Consultation consultation) {
//        return consultationRepository.save(consultation);
//    }
//
//    public List<Consultation> getConsultationsByPatientId(Long patientId) {
//        return consultationRepository.findByUserId(patientId);
//    }
//}