package server.services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import server.domain.Order;
import server.domain.Product;
import server.domain.User;
import server.repos.OrderRepo;
import server.repos.ProductRepo;
import java.util.LinkedHashMap;
import java.util.Map;

@Log
@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    public boolean addProductToOrder(String productID, User user) {
        log.info("id: " + productID);
        log.info("user: " + user);

        Product product = getProduct(productID);
        Order order = getActiveOrder(user);
        order.getOrderedProducts().put(productID, 1);
        order.setTotalPrice(order.getTotalPrice() + product.getFinalPrice());
        order.setTotalBonus(order.getTotalBonus() + product.getBonus());
        orderRepo.save(order);

        log.info("order: " + order.toString());
        return true;
    }

    public Map<String, Integer> getOrderedProducts(User user) {
        Map<String, Integer> orderedProducts = new LinkedHashMap<>();
        Order order = getActiveOrder(user);

        log.info(order + "");
        order.getOrderedProducts().forEach((productID, amount) -> {
            try
            {
                String productJson = new ObjectMapper().writeValueAsString(getProduct(productID));
                orderedProducts.put(productJson, amount);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        log.info("oP:" + orderedProducts);
        return orderedProducts;
    }

    public Order getActiveOrder(User user) {
        if (user != null) {
            Long userID = user.getUserID();
            Order userOrder = orderRepo.findByUserIDAndAcceptedFalse(userID);
            return userOrder != null ? userOrder : new Order();
        }
        else {
            String sessionID = RequestContextHolder.currentRequestAttributes().getSessionId();
            Order sessionOrder = orderRepo.findBySessionIDAndAcceptedFalse(sessionID);
            return sessionOrder != null ? sessionOrder : new Order(sessionID);
        }
    }

    private Product getProduct(String productID) {
        return productRepo.findByProductID(productID.replaceAll("=", ""));
    }
}
