package tn.pi.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.pi.Services.DoctorService;
import tn.pi.Services.UserService;
import tn.pi.Repostory.AppointmentRepository;
import tn.pi.Repostory.DoctorRepository;
import tn.pi.entities.Appointment;
import tn.pi.entities.Doctor;
import tn.pi.entities.User;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL = "mariem@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    public AdminController(UserService userService, DoctorService doctorService,
                           AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.userService = userService;
        this.doctorService = doctorService;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    // ==================== USER MANAGEMENT ====================
    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login";
        }

        try {
            userService.deleteById(id);
        } catch (Exception e) {
            // Log the error and redirect with error message
            return "redirect:/admin/users?error=deleteFailed";
        }

        return "redirect:/admin/users?success=true";
    }
    // ==================== DOCTOR MANAGEMENT ====================
    @GetMapping("/doctors")
    public String listDoctors(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login";
        }

        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        return "admin-doctors";
    }

    @PostMapping("/doctors/delete")
    public String deleteDoctor(@RequestParam Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login";
        }

        doctorRepository.deleteById(id);
        return "redirect:/admin/doctors";
    }

    // ==================== APPOINTMENT MANAGEMENT ====================
    @GetMapping("/appointments")
    public String listAppointments(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        model.addAttribute("appointments", appointments);
        return "admin-appointments";
    }

    @PostMapping("/appointments/delete")
    public String deleteAppointment(@RequestParam Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login";
        }

        appointmentRepository.deleteById(id);
        return "redirect:/admin/appointments";
    }

    // ==================== LOGOUT ====================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/login"; // Redirect to the login page
    }
}