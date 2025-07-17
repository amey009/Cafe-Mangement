package com.m13.cafe.serviceImp;

import com.m13.cafe.dao.BillDAO;
import com.m13.cafe.dao.CategoryDAO;
import com.m13.cafe.dao.ProductDAO;
import com.m13.cafe.dao.UserDAO;
import com.m13.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    ProductDAO productDAO;

    @Autowired
    BillDAO billDAO;

    @Override
    public ResponseEntity<Map<String, Object>> getDetails() {
        Map<String,Object> map=new HashMap<>();
        map.put("category",categoryDAO.count());
        map.put("product",productDAO.count());
        map.put("bill",billDAO.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
