package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.OriginalProduct;

@Repository
public interface OriginalRepo extends JpaRepository<OriginalProduct, String> {
    OriginalProduct findByProductID(String productID);
}
