package server.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Log
@Data
@Entity
@Table(name = "ordr")
@AllArgsConstructor
class Order implements Serializable {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderID;

    @Column(name = "session_uuid")
    private String sessionUUID;

    @Column(name = "user_id")
    private Long userID;

    @ElementCollection
    private List<String> orderedProducts;

    @ManyToOne
    private User user;
}
