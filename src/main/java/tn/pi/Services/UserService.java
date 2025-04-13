package tn.pi.Services;

import tn.pi.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Boolean checkEmail(String email);
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
    void updateUser(User user);
    Optional<User> findById(Long id);
    


    List<User> getAllUsers();
    void save(User user);

    void deleteById(Long id);
}
