package server.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Log
@Data
@Entity
@Table(name = "ordr")
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderID;

    @Column(name = "session_id")
    private String sessionID;

    @Column(name = "user_id")
    private Long userID;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> orderedProducts = new LinkedHashMap<>();

    @ManyToOne
    private User user;

    private Boolean accepted = false;

    private Integer totalPrice = 0, totalBonus = 0;

    public Order(String sessionID) {
        this.setSessionID(sessionID);
    }
}
