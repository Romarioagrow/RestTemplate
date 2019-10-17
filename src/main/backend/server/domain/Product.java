package server.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

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

    @Column(length = 1000)
    private String modelName, fullName, singleTypeName, searchName, groupBrandName, shortModelName, originalName;

    @Column(length = 20000)
    private String pic, pics, annotation, formattedAnnotation;

    private Boolean isAvailable, uniquePrice, coefficientModified, priceModified, isDuplicate, hasDuplicates, mappedJSON = false;

    private String brand, supplier;

    private LocalDate updateDate;
}
