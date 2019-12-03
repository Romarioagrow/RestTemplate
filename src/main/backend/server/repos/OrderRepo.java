package server.repos;
import server.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.User;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Order findByOrderID(Long orderID);

    Order findByUserAndAcceptedFalse(User user);

    Order findBySessionIDAndAcceptedFalse(String sessionID);

    List<Order> findAllByAcceptedTrueAndCompletedFalse();

    List<Order> findAllByUserAndCompletedTrue(User user);

    List<Order> findAllByUserAndAcceptedTrueAndCompletedFalse(User user);

    List<Order> findAllByCompletedTrue();

    List<Order> findAllByAcceptedFalse();

    List<Order> findByClientMobileContains(String mobile);

    List<Order> findByClientNameContainsIgnoreCase(String name);
}
