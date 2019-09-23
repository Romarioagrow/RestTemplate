package server.domain;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "products")
public class Item {
    @Id
    @Column(name = "product_id")
    public String productID;
    private String name, type, attributes;

    private String originalCategory, originalType, originalBrand, originalAmount, originalPrice;

    @Column(name = "original_name", length = 10000)
    private String originalName;

    @Column(name = "original_annotation", length = 20000)
    private String originalAnnotation;

    @Column(name = "original_pic", length = 10000)
    private String originalPic;

    private String originalGroup;

    /*ОБЩИЕ ПОЛЯ*/
    private String supplier;

    private LocalDate update;

    /*ДЛЯ ВЫВОДА НА СТРАНИЦУ*/
    private String productGroup, productType, singleType, modelName, fullName, productCategory, groupBrand;

    private Integer finalPrice, bonus;
}
