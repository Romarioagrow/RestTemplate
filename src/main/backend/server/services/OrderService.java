package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import server.domain.Order;
import server.domain.Product;
import server.domain.User;
import server.repos.OrderRepo;
import server.repos.ProductRepo;
import java.util.ArrayList;
import java.util.List;

@Log
@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;


    public List<Product> listOrderedProducts() {
        List<Product> orderedProducts = new ArrayList<>();
        orderedProducts.add(productRepo.findByProductID("02.02.04.01.000777"));
        orderedProducts.add(productRepo.findByProductID("01.01.01.000000011"));
        orderedProducts.add(productRepo.findByProductID("03.01.01.01.000427"));
        return orderedProducts;
    }

    public boolean addProductToOrder(String productID, User user) {
        log.info("id: " + productID);
        log.info("user: " + user);
        productID = productID.replaceAll("=", "");

        Product product = productRepo.findByProductID(productID.replaceAll("=", ""));

        Order order = (user != null) ? getActiveUserOrder(user) : getActiveSessionOrder();
        order.getOrderedProducts().put(productID, 1);
        order.setTotalPrice(order.getTotalPrice() + product.getFinalPrice());
        order.setTotalBonus(order.getTotalBonus() + product.getBonus());
        orderRepo.save(order);

        log.info("order: " + order.toString());
        return true;
        //Order order = new Order();
        /*if (user == null) {
            order = getActiveSessionOrder();
            order.getOrderedProducts().put(productID, 1);
        }
        else
        {
            order = getActiveUserOrder(user);
            order.getOrderedProducts().put(productID, 1);
        }*/
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
        //return sessionOrder != null ? sessionOrder : new Order();
    }
}
