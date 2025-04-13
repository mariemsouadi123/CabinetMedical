package tn.pi.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.pi.Services.UserService;
import tn.pi.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final UserService userService;

    public PatientController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Display profile
    @GetMapping
    public String showProfile(Model model, @SessionAttribute(value = "loggedInUser", required = false) User loggedInUser) {
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, @SessionAttribute(value = "loggedInUser", required = false) User loggedInUser) {
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", loggedInUser);
        return "edit-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser,
                                BindingResult result,
                                HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            logger.info("Validation errors: {}", result.getAllErrors());
            return "edit-profile";
        }

        logger.info("Updating profile for user: {}", loggedInUser.getEmail());

        Optional<User> userFromDb = userService.findById(loggedInUser.getId());

        if (userFromDb.isPresent()) {
            User user = userFromDb.get();

            // Mise à jour des champs modifiés
            user.setFullName(updatedUser.getFullName());
            user.setAge(updatedUser.getAge());
            user.setPhone(updatedUser.getPhone());
            user.setAddress(updatedUser.getAddress());
            user.setGender(updatedUser.getGender());


            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }

            userService.save(user);

            session.setAttribute("loggedInUser", user);
            logger.info("Session updated successfully.");
        } else {
            logger.warn("User not found in the database!");
            return "redirect:/profile?error=user_not_found";
        }

        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteProfile(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        logger.info("Deleting profile for user: {}", loggedInUser.getEmail());

        userService.deleteByEmail(loggedInUser.getEmail());
        session.invalidate();

        return "redirect:/login";
    }
}