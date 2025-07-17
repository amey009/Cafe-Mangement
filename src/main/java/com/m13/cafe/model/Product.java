package com.m13.cafe.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@DynamicUpdate
@DynamicInsert
@Table(name="product")
public class Product {

    private static final Long serialVersionID= 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_fk",nullable = false)
    //This annotation defines the foreign key column in the owning entity's table
    // that is used to join with another table (usually the primary key of the
    // referenced entity).
    private Category category;

    private String description;

    private Integer price;

    private String status;


}
