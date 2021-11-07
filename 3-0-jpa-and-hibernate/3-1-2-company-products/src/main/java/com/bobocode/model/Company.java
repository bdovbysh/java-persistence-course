package com.bobocode.model;

import com.bobocode.util.ExerciseNotCompletedException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * todo:
 * - make setter for field {@link Company#products} private
 * - initialize field {@link Company#products} as new {@link ArrayList}
 * - implement a helper {@link Company#addProduct(Product)} that establishes a relation on both sides
 * - implement a helper {@link Company#removeProduct(Product)} that drops a relation on both sides
 * <p>
 * - configure JPA entity
 * - specify table name: "company"
 * - configure auto generated identifier
 * - configure mandatory column "name" for field {@link Company#name}
 * <p>
 * - configure one to many relationship as mapped on the child side
 */
@NoArgsConstructor
@Getter
@Setter
@Table(name = "company")
@Entity
@EqualsAndHashCode(of = "id")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "company")
    @Setter(AccessLevel.PRIVATE)
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        product.setCompany(this);
        products.add(product);
    }

    public void removeProduct(Product product) {
        product.setCompany(null);
        products.remove(product);
    }

}