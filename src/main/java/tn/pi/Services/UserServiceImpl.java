package tn.pi.Services;

import jakarta.transaction.Transactional;
import tn.pi.Repostory.AppointmentRepository;
import tn.pi.Repostory.UserRepository;
import tn.pi.entities.Appointment;
import tn.pi.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Boolean checkEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        userRepo.deleteByEmail(email);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userRepo.save(user);
    }
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    @Transactional
    public void save(User user) {
        userRepo.save(user);
        userRepo.flush();
    }
    @Override
    @Transactional
    public void deleteById(Long id) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepo.delete(user);
    }
}