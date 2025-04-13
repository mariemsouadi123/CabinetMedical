package tn.pi.controllers;

import jakarta.servlet.http.HttpSession;
import tn.pi.Repostory.AppointmentRepository;
import tn.pi.Repostory.DoctorRepository;
import tn.pi.Services.UserService;
import tn.pi.entities.Appointment;
import tn.pi.entities.Doctor;
import tn.pi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserService userService;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL = "mariem@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    public HomeController(UserService userService, AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.userService = userService;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        model.addAttribute("user", new User());

        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }

        return "register";
    }

    @PostMapping("/createUser")
    public String createUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "register";
        }

        boolean emailExists = userService.checkEmail(user.getEmail());
        if (emailExists) {
            session.setAttribute("msg", "Email Id already exists");
            return "redirect:/register";
        }

        User savedUser = userService.createUser(user);
        if (savedUser != null) {
            session.setAttribute("msg", "Registration successful! Please log in.");
            return "redirect:/login";
        } else {
            session.setAttribute("msg", "Registration failed");
            return "redirect:/register";
        }
    }

    @PostMapping("/signin")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {
        // Check if the user is the admin
        if (ADMIN_EMAIL.equals(email)) {
            if (ADMIN_PASSWORD.equals(password)) {
                User adminUser = new User();
                adminUser.setFullName("Admin");
                adminUser.setEmail(ADMIN_EMAIL);
                session.setAttribute("loggedInUser", adminUser);
                return "redirect:/admin/dashboard";
            } else {
                session.setAttribute("msg", "Invalid email or password.");
                return "redirect:/login";
            }
        }

        // Regular user login logic
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", user);
                return "redirect:/dashboard";
            }
        }

        session.setAttribute("msg", "Invalid email or password.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentRepository.findByUser(loggedInUser);
        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctors);
        model.addAttribute("user", loggedInUser);
        return "dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !ADMIN_EMAIL.equals(loggedInUser.getEmail())) {
            return "redirect:/login"; // redirect to login if not the admin
        }

        List<Doctor> doctors = doctorRepository.findAll();
        List<Appointment> allAppointments = appointmentRepository.findAll();
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("doctors", doctors);
        model.addAttribute("admin", loggedInUser);
        return "admin-dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}