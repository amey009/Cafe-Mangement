package com.m13.cafe.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Data
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "bill")
public class Bill implements Serializable {

    private final static Long serialVersionUId=10L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String name;

    private String email;

    @Column(name = "conatctnumber")
    private String contactNumber;

    @Column(name = "paymentmethod")
    private String paymentMethod;

    @Column(name = "totalamount")
    private  Integer totalAmount;

    //complete data of the all product which purchase into th DB uisng form of JSON array
    @Column(name = "productdetails", columnDefinition = "json")
    private String productDetails;

    private String createdBy;






}
