package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUserID(Long userID);
    User findByUsername(String username);
}
