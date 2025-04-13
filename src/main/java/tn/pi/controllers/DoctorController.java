package tn.pi.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.pi.Services.DoctorService;
import tn.pi.Repostory.AppointmentRepository;
import tn.pi.entities.Doctor;
import tn.pi.entities.Appointment;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public DoctorController(DoctorService doctorService, AppointmentRepository appointmentRepository) {
        this.doctorService = doctorService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        model.addAttribute("doctor", new Doctor());

        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }

        return "doctor-register";
    }

    @PostMapping("/createDoctor")
    public String createDoctor(@Valid @ModelAttribute("doctor") Doctor doctor,
                               BindingResult result,
                               HttpSession session) {
        if (result.hasErrors()) {
            return "doctor-register";
        }

        boolean emailExists = doctorService.checkEmail(doctor.getEmail());
        if (emailExists) {
            session.setAttribute("msg", "Email Id already exists");
            return "redirect:/doctor/register";
        }

        doctorService.createDoctor(doctor);
        session.setAttribute("msg", "Doctor registered successfully!");
        return "redirect:/doctor/login";
    }

    @GetMapping("/login")
    public String doctorLogin(HttpSession session, Model model) {
        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }
        return "doctor-login";
    }

    @PostMapping("/signin")
    public String doctorSignIn(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session) {
        Optional<Doctor> doctorOptional = doctorService.findByEmail(email);

        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();

            // Password verification (⚠️ Hash in production)
            if (doctor.getPassword().equals(password)) {
                session.setAttribute("loggedInDoctor", doctor);
                return "redirect:/doctor/dashboard";
            }
        }

        session.setAttribute("msg", "Invalid email or password.");
        return "redirect:/doctor/login";
    }

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        List<Appointment> appointments = appointmentRepository.findByDoctor(loggedInDoctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctor", loggedInDoctor);

        return "doctor-dashboard";
    }

    @GetMapping("/profile")
    public String doctorProfile(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        model.addAttribute("doctor", loggedInDoctor);
        return "doctor-profile";
    }

    @GetMapping("/profile/edit")
    public String editDoctorProfile(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        model.addAttribute("doctor", loggedInDoctor);
        return "doctor-edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateDoctorProfile(@Valid @ModelAttribute("doctor") Doctor updatedDoctor,
                                      BindingResult result,
                                      HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        if (result.hasErrors()) {
            // Log validation errors
            result.getAllErrors().forEach(error -> {
                System.out.println("Validation Error: " + error.getDefaultMessage());
            });
            return "doctor-edit-profile";
        }

        loggedInDoctor.setUsername(updatedDoctor.getUsername());
        loggedInDoctor.setEmail(updatedDoctor.getEmail());
        loggedInDoctor.setSpecialization(updatedDoctor.getSpecialization());

        doctorService.updateDoctor(loggedInDoctor);
        session.setAttribute("loggedInDoctor", loggedInDoctor);

        return "redirect:/doctor/profile";
    }
    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable int id, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");

        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        appointmentRepository.deleteById((long) id);
        return "redirect:/doctor/dashboard";
    }

    @GetMapping("/logout")
    public String doctorLogout(HttpSession session) {
        session.removeAttribute("loggedInDoctor");
        session.invalidate();
        return "redirect:/doctor/login";
    }
}