package com.m13.cafe.dao;

import com.m13.cafe.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDAO  extends JpaRepository<Category,Long> {

    @Query("select c from Category c where c in (select p.category from Product p where p.status='true')")
    List<Category> getAllCategory();

}
