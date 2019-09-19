package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Integer> {
}
