package tn.pi.Repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
}