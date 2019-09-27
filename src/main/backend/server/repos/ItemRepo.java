package server.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.Item;

import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, String> {
    List<Item> findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(String group);

    List<Item> findByProductCategoryIgnoreCase(String category);

    Item findFirstByProductGroupAndOriginalPicIsNotNull(String group);


}
