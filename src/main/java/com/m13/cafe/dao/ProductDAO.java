package com.m13.cafe.dao;

import com.m13.cafe.DTO.ProductDTO;
import com.m13.cafe.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {

    @Query("select new com.m13.cafe.DTO.ProductDTO(p.id, p.name, p.description, p.price, p.status, c.id, c.categoryName) from Product p JOIN p.category c")
    List<ProductDTO> getAllProducts();


    //In Spring Data JPA, when you define a modifying query (i.e., a method annotated with @Modifying), the method
    // must return void, int, or Integer. It cannot return Long, Boolean, or other types.
    @Modifying
    @Transactional
    @Query("update Product p set p.status=:status where p.id=:id")
    Integer updateProductStatus(@Param("status") String status, @Param("id") long id);

    @Query("select new com.m13.cafe.DTO.ProductDTO(p.id, p.name) from Product p where p.category.id=:id and p.status='true'")
    List<ProductDTO> getProductByCategory(@Param("id") Long id);
}

