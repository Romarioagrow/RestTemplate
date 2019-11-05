package server.repos;
import server.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.User;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Order findByOrderID(Long orderID);

    Order findByUserAndAcceptedFalse(User user);

    Order findBySessionIDAndAcceptedFalse(String sessionID);

    List<Order> findAllByAcceptedTrue();

    List<Order> findAllByUserAndAcceptedTrue(User user);
}
