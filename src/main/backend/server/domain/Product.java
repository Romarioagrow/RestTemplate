package server.domain;
import lombok.Data;
import lombok.extern.java.Log;
import javax.persistence.*;

@Log
@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "product_id")
    private String productID;

    private String productCategory, productGroup, productType;

    private Integer finalPrice, bonus;

    private Double defaultCoefficient, customCoefficient;

    @Column(length = 10000)
    private String modelName, fullName, singleTypeName, searchName, groupBrandName;

    @Column(length = 20000)
    private String pic, pics, annotation, formattedAnnotation;

    private Boolean uniquePrice, coefficientModified, priceModified;

    private String brand, supplier;
}
