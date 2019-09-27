package server.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import java.util.*;

@Log
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltersList {
    public Set<String> brands = new TreeSet<>(), features = new TreeSet<>();
    public List<Integer> prices = new LinkedList<>();
    public Map<String, TreeSet<String>> digitsFilters = new TreeMap<>(), singletonFilters = new TreeMap<>();

    public void showInfo() {
        brands.forEach(log::info);
        prices.forEach(integer -> log.info(integer + ""));
    }
}
