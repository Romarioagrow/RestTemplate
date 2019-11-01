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
import java.util.LinkedList;
import java.util.Map;

@Log
@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    public boolean addProductToOrder(String productID, User user) {
        Product product = getProduct(productID);
        Order order = getActiveOrder(user);
        order.getOrderedProducts().put(productID, 1);
        order.setTotalPrice(order.getTotalPrice() + product.getFinalPrice());
        order.setTotalBonus(order.getTotalBonus() + product.getBonus());
        orderRepo.save(order);
        return true;
    }

    public LinkedList<Object> deleteProductFromOrder(String productID, User user) {
        Product product = getProduct(productID);
        Order order = getActiveOrder(user);
        int amount = order.getOrderedProducts().get(productID);

        order.getOrderedProducts().remove(productID);
        order.setTotalPrice(order.getTotalPrice() - product.getFinalPrice() * amount);
        order.setTotalBonus(order.getTotalBonus() - product.getBonus() * amount);
        orderRepo.save(order);
        return payloadOrderData(order);
    }

    public LinkedList<Object> increaseAmount(String productID, User user) {
        Product product = getProduct(productID);
        Order order = getActiveOrder(user);
        int amount = order.getOrderedProducts().get(productID);

        order.getOrderedProducts().put(productID, amount + 1);
        order.setTotalPrice(order.getTotalPrice() + product.getFinalPrice());
        order.setTotalBonus(order.getTotalBonus() + product.getBonus());
        orderRepo.save(order);
        return payloadOrderData(order);
    }

    public LinkedList<Object> decreaseAmount(String productID, User user) {
        Product product = getProduct(productID);
        Order order = getActiveOrder(user);
        int amount = order.getOrderedProducts().get(productID);

        order.getOrderedProducts().put(productID, amount - 1);
        order.setTotalPrice(order.getTotalPrice() - product.getFinalPrice());
        order.setTotalBonus(order.getTotalBonus() - product.getBonus());
        orderRepo.save(order);
        return payloadOrderData(order);
    }

    public LinkedList<Object> getOrderData(User user) {
        Order order = getActiveOrder(user);
        return payloadOrderData(order);
    }

    private LinkedList<Object> payloadOrderData(Order order) {
        LinkedList<Object> payload = new LinkedList<>();
        Map<String, Integer> orderedProducts = new LinkedHashMap<>();
        order.getOrderedProducts().forEach((productID, amount) -> {
            try {
                String productJson = new ObjectMapper().writeValueAsString(getProduct(productID));
                orderedProducts.put(productJson, amount);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        payload.add(order);
        payload.add(orderedProducts);
        return payload;
    }

    private Order getActiveOrder(User user) {
        if (user != null) {
            Order userOrder = orderRepo.findByUserAndAcceptedFalse(user);
            return userOrder != null ? userOrder : new Order(user);
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
