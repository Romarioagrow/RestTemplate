package server.domain;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    private Integer ID;
    private String name, type, attributes;
}
