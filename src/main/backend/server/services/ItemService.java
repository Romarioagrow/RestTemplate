package server.services;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import server.domain.Item;
import server.repos.ItemRepo;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepo itemRepo;

    public List<Item> listAllItems() {
        return itemRepo.findAll();
    }
}
