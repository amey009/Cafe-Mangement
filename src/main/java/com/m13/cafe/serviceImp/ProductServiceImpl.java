package com.m13.cafe.serviceImp;

import com.m13.cafe.DTO.ProductDTO;
import com.m13.cafe.constants.CafeConstants;
import com.m13.cafe.dao.ProductDAO;
import com.m13.cafe.jwt.JwtFilter;
import com.m13.cafe.model.Category;
import com.m13.cafe.model.Product;
import com.m13.cafe.service.ProductsService;
import com.m13.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductsService {

    @Autowired
    JwtFilter jwtFilter;// add this for admin can add new product

    @Autowired
    ProductDAO productDAO;//this for database operation

    @Override
    public ResponseEntity<String> addProduct(Map<String, String> requestMap) {
        try{
                if (jwtFilter.isAdmin()){
                    if (validateProductMap(requestMap,false)){
                        productDAO.save(getProductFromMap(requestMap,false));
                        return CafeUtils.getResponseEntity("Product Adeed Sucessfully",HttpStatus.OK);
                    }
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
                }else {
                    return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //why passing the boolean value- beacuse of for adding product and update product we use same method
    //with help of this boolean value we indentify id is primary key or not of product
    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        //This line checks whether the map (in this case, requestMap) contains a key named "name".
        //containsKey-Checks whether the map contains a specific key.
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId ){
                return true;
            } else if (!validateId) {
                return true;
                
            }
        }
        return false;

    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category=new Category();
        category.setId(Long.parseLong(requestMap.get("categoryId")));

        Product product=new Product();
        if (isAdd){
            product.setId(Long.parseLong(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try{
            return new ResponseEntity<>(productDAO.getAllProducts(),HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap,true)){
                    Optional<Product> optionalProduct=productDAO.findById(Long.parseLong(requestMap.get("id")));
                    if (!optionalProduct.isEmpty()){
                        Product product=getProductFromMap(requestMap,true);
                        product.setStatus(optionalProduct.get().getStatus());
                        productDAO.save(product);
                        return CafeUtils.getResponseEntity("Productupdated Sucessfully", HttpStatus.OK);

                    }
                    else{
                        return CafeUtils.getResponseEntity("Product ID is not exist",HttpStatus.OK);
                    }
                }else{
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
                }
            }

            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        try{
                if (jwtFilter.isAdmin()){
                    Optional optional=productDAO.findById(id);

                    if (!optional.isEmpty()){
                        productDAO.deleteById(id);
                        return CafeUtils.getResponseEntity("Product Deleted Sucessfully",HttpStatus.OK);
                    }
                    else{
                        return CafeUtils.getResponseEntity("Product ID does not exist",HttpStatus.OK);
                    }
                }else{
                    return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional=productDAO.findById(Long.parseLong(requestMap.get("id")));

                if (!optional.isEmpty()){
                    productDAO.updateProductStatus(requestMap.get("status"),Long.parseLong(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product Sttatus updated Sucessfully",HttpStatus.OK);
                }else{
                    return CafeUtils.getResponseEntity("Product does not exist",HttpStatus.OK);
                }

            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getByCategory(Long id) {
        try {
            return new ResponseEntity<>(productDAO.getProductByCategory(id),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
