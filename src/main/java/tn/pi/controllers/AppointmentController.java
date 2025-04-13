package tn.pi.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.pi.Repostory.AppointmentRepository;
import tn.pi.Repostory
        .DoctorRepository;
import tn.pi.entities.Appointment;
import tn.pi.entities.Doctor;
import tn.pi.entities.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public String listDoctors(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("user", loggedInUser);
        return "appointments";
    }

    @GetMapping("/book/{doctorId}")
    public String showAppointmentForm(@PathVariable Long doctorId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return "redirect:/appointments";

        model.addAttribute("doctor", doctorOpt.get());
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("user", loggedInUser);

        return "appointment_form";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam String date,
                                  @RequestParam String time,
                                  HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return "redirect:/appointments";

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctorOpt.get());
        appointment.setUser(loggedInUser);
        appointment.setDate(LocalDate.parse(date));
        appointment.setTime(LocalTime.parse(time));
        appointment.setStatus("Scheduled");
        appointment.setPrice(50.0);

        appointmentRepository.save(appointment);
        return "redirect:/dashboard";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.get().getUser().getId() != loggedInUser.getId()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("appointment", appointmentOpt.get());
        return "update-appointment";
    }

    @PostMapping("/update/{id}")
    public String updateAppointment(@PathVariable Long id,
                                    @RequestParam String date,
                                    @RequestParam String time,
                                    @RequestParam String status,
                                    HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.get().getUser().getId() != loggedInUser.getId()) {
            return "redirect:/dashboard";
        }

        Appointment appointment = appointmentOpt.get();
        appointment.setDate(LocalDate.parse(date));
        appointment.setTime(LocalTime.parse(time));
        appointment.setStatus(status);

        appointmentRepository.save(appointment);
        return "redirect:/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent() && appointmentOpt.get().getUser().getId()==(loggedInUser.getId())) {
            appointmentRepository.deleteById(id);
        }

        return "redirect:/dashboard";
    }

}
