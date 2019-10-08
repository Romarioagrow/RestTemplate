package server.domain;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "products")
public class Item {
    @Id
    @Column(name = "product_id")
    private String productID;

    @Column(length = 10000)
    private String originalCategory, originalGroup, originalType, originalName, originalBrand, originalAmount, originalPrice;

    @Column(length = 20000)
    private String originalAnnotation, originalPic;
}
