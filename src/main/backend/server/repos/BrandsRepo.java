package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.UniqueBrand;

@Repository
public interface BrandsRepo extends JpaRepository<UniqueBrand, String> {
    UniqueBrand findByProductID(String productID);
}
