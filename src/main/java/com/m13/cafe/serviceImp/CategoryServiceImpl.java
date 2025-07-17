package com.m13.cafe.serviceImp;

import com.google.common.base.Strings;
import com.m13.cafe.constants.CafeConstants;
import com.m13.cafe.dao.CategoryDAO;
import com.m13.cafe.jwt.JwtFilter;
import com.m13.cafe.model.Category;
import com.m13.cafe.service.CategoryService;
import com.m13.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap,false)){
                    categoryDAO.save(getCategoryFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Category addes Succesfully",HttpStatus.OK);

                }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }






//    Why Use filterValue Like This?
//    This is common in REST APIs when clients (like the frontend) want to optionally filter results using a query param.
//    For example:
//    GET /categories?filterValue=true → only active or usable categories- this is for users shows
//    in fronted only for users where active categories
//
//    GET /categories → all categories (even archived ones) for admins to show all categories

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{

            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside if b");
                return new ResponseEntity<List<Category>>(categoryDAO.getAllCategory(),HttpStatus.OK);

            }

            return new ResponseEntity<>(categoryDAO.findAll(),HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap,true)){
                   Optional optional= categoryDAO.findById(Long.parseLong(requestMap.get("id")));
                   if (!optional.isEmpty()){
                       categoryDAO.save(getCategoryFromMap(requestMap,true));
                       return CafeUtils.getResponseEntity("Category updated sucessfully",HttpStatus.OK);
                   }
                   else {
                       return CafeUtils.getResponseEntity("Category Id does not exist", HttpStatus.OK);
                   }
                }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("categoryName")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category category=new Category();
        if(isAdd){
            category.setId(Long.parseLong(requestMap.get("id")));

        }
        category.setCategoryName(requestMap.get("categoryName"));
        return category;
    }
}
