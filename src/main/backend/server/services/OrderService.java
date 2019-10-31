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

    public Map<String, Integer> listOrderedProducts(User user) {
        Map<String, Integer> orderedProducts = new LinkedHashMap<>();

        Order order = (user != null) ? getActiveUserOrder(user) : getActiveSessionOrder();  ///getActiveOrder(user)
        order.getOrderedProducts().forEach((productID, amount) -> {
            try
            {
                Product product = productRepo.findByProductID(productID);
                String json = new ObjectMapper().writeValueAsString(product);
                orderedProducts.put(json, amount);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return orderedProducts;
    }

    public boolean addProductToOrder(String productID, User user) {
        log.info("id: " + productID);
        log.info("user: " + user);
        productID = productID.replaceAll("=", "");

        Product product = productRepo.findByProductID(productID.replaceAll("=", "")); /// getProduct(productID)
        Order order = (user != null) ? getActiveUserOrder(user) : getActiveSessionOrder(); ///getActiveOrder(user)
        order.getOrderedProducts().put(productID, 1);
        order.setTotalPrice(order.getTotalPrice() + product.getFinalPrice());
        order.setTotalBonus(order.getTotalBonus() + product.getBonus());
        orderRepo.save(order);

        log.info("order: " + order.toString());
        return true;
    }

    private Order getActiveUserOrder(User user) {
        Long userID = user.getUserID();
        Order userOrder = orderRepo.findByUserIDAndAcceptedFalse(userID);
        return userOrder != null ? userOrder : new Order();
    }

    private Order getActiveSessionOrder() {
        String sessionID = RequestContextHolder.currentRequestAttributes().getSessionId();
        Order sessionOrder = orderRepo.findBySessionIDAndAcceptedFalse(sessionID);
        if (sessionOrder != null) {
            log.info(sessionOrder + "");
            return sessionOrder;
        }
        sessionOrder = new Order();
        sessionOrder.setSessionID(sessionID);
        return sessionOrder;
    }
}
