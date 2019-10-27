package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.OriginalProduct;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OriginalRepo extends JpaRepository<OriginalProduct, String> {
    OriginalProduct findByProductID(String productID);

    List<OriginalProduct> findByUpdateDate(LocalDate today);

    List<OriginalProduct> findByLinkToPicNotNull();

    //List<OriginalProduct> findByOriginalTypeAndOriginalAnnotationIsNo(String type);
}
