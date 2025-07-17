package com.m13.cafe.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

//Why do we write SELECT u FROM User u instead of SELECT * in JPQL?
//Because JPQL (Java Persistence Query Language) is object-oriented, not SQL-based.
// It works with Java classes and objects, not database tables and columns.
//SELECT u returns User objects.
//at time of write query we use table as Entity name(i.e User) of class not from the database table name(i.e user)
@NamedQuery(name = "User.findByEmailId",query = "select u from User u where u.email= :email")

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
public class User implements Serializable {

    private static final Long serialVersionID= 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String name;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    private String password;

    private String status;

    private String role;

}
