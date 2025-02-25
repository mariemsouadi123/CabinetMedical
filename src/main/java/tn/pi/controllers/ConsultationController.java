package tn.pi.controllers;

import tn.pi.entities.Consultation;
import tn.pi.entities.Doctor;
import tn.pi.entities.Appointment;
import tn.pi.entities.User;
import tn.pi.repository.ConsultationRepository;
import tn.pi.Repostory.AppointmentRepository;
import tn.pi.Repostory.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // ✅ Dashboard Médecin
    @GetMapping("/doctor-dashboard")
    public String showDoctorDashboard(Model model, HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (doctor == null) {
            return "redirect:/doctor/login";
        }

        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);
        model.addAttribute("appointments", appointments);
        return "doctor-dashboard";
    }

    // ✅ Formulaire d'ajout Consultation
    @GetMapping("/add/{appointmentId}")
    public String showAddConsultationForm(@PathVariable Long appointmentId, Model model, HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (doctor == null) {
            return "redirect:/doctor/login";
        }

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return "redirect:/doctor-dashboard";
        }

        Appointment appointment = appointmentOpt.get();
        Consultation consultation = new Consultation();
        consultation.setAppointment(appointment);
        consultation.setDoctor(doctor);
        consultation.setUser(appointment.getUser());

        // Pass the user's full name to the model
        model.addAttribute("consultation", consultation);
        model.addAttribute("userFullName", appointment.getUser().getFullName());

        return "add-consultation";
    }

    // ✅ Sauvegarde Consultation
    @PostMapping("/save")
    public String saveConsultation(@ModelAttribute("consultation") Consultation consultation, HttpSession session) {
        Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (doctor == null) {
            return "redirect:/doctor/login";
        }

        if (consultation.getAppointment() == null || consultation.getAppointment().getId() == null) {
            return "redirect:/doctor/dashboard";
        }

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(consultation.getAppointment().getId());

        if (appointmentOpt.isEmpty()) {
            return "redirect:/doctor/dashboard";
        }

        Appointment appointment = appointmentOpt.get();

        // 🔹 Vérifier que l'appointment contient bien un user
        if (appointment.getUser() == null) {
            return "redirect:/doctor/dashboard"; // ou gérer l'erreur avec un message
        }

        consultation.setAppointment(appointment);
        consultation.setDoctor(doctor);
        consultation.setUser(appointment.getUser()); // ✅ Correction : affecter l'user
        consultation.setConsultationDate(LocalDateTime.now());

        consultationRepository.save(consultation);

        return "redirect:/doctor/dashboard";
    }


    // ✅ Consultation Vue pour un Patient
    @GetMapping("/view/{appointmentId}")
    public String viewConsultation(@PathVariable Long appointmentId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Optional<Consultation> consultationOpt = consultationRepository.findByAppointmentId(appointmentId);

        if (consultationOpt.isPresent()) {
            model.addAttribute("consultation", consultationOpt.get());
            return "view-consultation";
        } else {
            return "redirect:/appointments"; // Redirection si la consultation n'existe pas
        }
    }

}
