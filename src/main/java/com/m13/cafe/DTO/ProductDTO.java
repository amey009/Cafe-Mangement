package com.m13.cafe.DTO;

import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    private String name;

    private String description;

    private Integer price;


    private String status;

    private Long categoryId;

    private String categoryName;

    public ProductDTO() {
    }

    public ProductDTO(Long id,String name) {
        this.id = id;
        this.name = name;
    }

    public ProductDTO(Long id, String name, String description, Integer price, String status, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }


}
