package itmo.blps.repos;

import itmo.blps.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findUserById(Long id);
    User findUserByEmail(String email);
}