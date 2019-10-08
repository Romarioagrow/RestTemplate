package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.Product;

public interface OriginalRepo extends JpaRepository<Product, String> {
}
