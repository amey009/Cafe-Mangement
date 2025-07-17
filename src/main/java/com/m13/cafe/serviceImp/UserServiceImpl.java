package com.m13.cafe.serviceImp;

import com.google.common.base.Strings;
import com.m13.cafe.constants.CafeConstants;
import com.m13.cafe.dao.UserDAO;
import com.m13.cafe.jwt.CustomerUsersDetailsService;
import com.m13.cafe.jwt.JwtFilter;
import com.m13.cafe.jwt.JwtUtil;
import com.m13.cafe.model.User;
import com.m13.cafe.service.UserService;
import com.m13.cafe.utils.CafeUtils;
import com.m13.cafe.DTO.UserDTO;
import com.m13.cafe.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

// Lombok annotation for logging
@Slf4j
// Marks this class as a Spring service (business logic layer)
@Service
public class UserServiceImpl implements UserService {

    // Injecting the UserDAO to interact with the database
    @Autowired
    UserDAO userDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    /**
     * This method handles the user sign-up logic.
     * It validates the input data, checks for existing user,
     * and registers a new user if email doesn't already exist.
     */
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        // Log the incoming request data
        log.info("inside signup{}", requestMap);

        try {
            // Checks whether name, contactNumber, email, and password are present.
            if (validateSignUpMap(requestMap)) {

                // Check if a user with the given email already exists
                User user = userDAO.findByEmailId(requestMap.get("email"));

                if (Objects.isNull(user)) {
                    // Converts the map into a User object with default values.
                    userDAO.save(getUserFromMap(requestMap));

                    // Return success response
                    return CafeUtils.getResponseEntity("Successfully Register", HttpStatus.OK);
                } else {
                    // Return response if email already exists
                    return CafeUtils.getResponseEntity("Email already exist", HttpStatus.BAD_REQUEST);
                }
            }
            //If any field is missing, return 400 Bad Request with message: "Invalid Data".
            else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        }

        // Return a generic error if something goes wrong
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    /**
     * This method checks whether the required fields
     * (name, contactNumber, email, password) are present in the requestMap.
     */
    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

    /**
     * This method converts the incoming map data into a User object
     * to be saved in the database.
     */
    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));

        // By default, status is false (not active)
        user.setStatus("false");

        // Set the default role to 'user'
        user.setRole("user");

        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("inside login");
        try {
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );

            if (authentication.isAuthenticated()){
                if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase(String.valueOf(true))){
                    return
                            new ResponseEntity<String>("{\"token\":\""+
                                    jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                                            customerUsersDetailsService.getUserDetail().getRole())+ "\"}",
                                    HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"message\":\""+ "Wait for admin approval..!"+"\"}",
                            HttpStatus.BAD_REQUEST);
                }

            }

        } catch (Exception e) {
            log.error("{}",e);
        }
        return new ResponseEntity<String>("{\"message\":\""+ "Bad Credentials ..!"+"\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUser() {
        try {
                if (jwtFilter.isAdmin()){
                        return new ResponseEntity<>(userDAO.findAllUser(),HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestmap) {
            try{
                if (jwtFilter.isAdmin()){
                    Optional<User> optionalUser=userDAO.findById(Long.parseLong(requestmap.get("id")));
                    if(!optionalUser.isEmpty()){
                        userDAO.updateStatus(requestmap.get("status"),Long.parseLong(requestmap.get("id")));
//                    sendMailToAllAdmin(requestmap.get("status"),
//                            optionalUser.get().getEmail(),
//                            userDAO.getAllAdmin());
                        notifyAdminsAndUser(requestmap.get("status"),
                                optionalUser.get().getEmail(),
                                userDAO.getAllAdmin());

                        return CafeUtils.getResponseEntity("User Status Updated",HttpStatus.OK);
                    }
                }else {
                    return CafeUtils.getResponseEntity("User does not exist...",HttpStatus.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }



//    private void sendMailToAllAdmin(String status, String email,List<String> allAdmin){
//        allAdmin.remove(jwtFilter.getCurrentUserName());
//        if (status !=null && status.equalsIgnoreCase("true")){
//            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUserName(), "Account Approved","User:-"+ email + "\n is approved by \n ADMIN:- " + jwtFilter.getCurrentUserName(),allAdmin);
//
//        }else{
//            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUserName(), "Account Disabled","User:-"+ email + "\n is disabled by \n ADMIN:- " + jwtFilter.getCurrentUserName(),allAdmin);
//
//        }
//
//    }

    private void notifyAdminsAndUser(String status, String userEmail, List<String> allAdmins) {
        String admin = jwtFilter.getCurrentUserName();

        // Remove current admin from CC list if present
        allAdmins.remove(admin);

        String subject;
        String adminMessage;
        String userMessage;

        if ("true".equalsIgnoreCase(status)) {
            subject = "Account Approved";
            adminMessage = "User: " + userEmail + "\nhas been APPROVED by ADMIN: " + admin;
            userMessage = "Hi, your Cafe account has been APPROVED by admin. You can now log in.";
        } else {
            subject = "Account Disabled";
            adminMessage = "User: " + userEmail + "\nhas been DISABLED by ADMIN: " + admin;
            userMessage = "Hi, your Cafe account has been DISABLED by admin. Please contact support.";
        }

        // Send email to end user directly
        emailUtils.sendSimpleMessage(userEmail, subject, userMessage, null);

        // Send email to admins with CC
        emailUtils.sendSimpleMessage(admin, subject, adminMessage, allAdmins);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestmap) {
        try {
            User userObj=userDAO.findByEmail(jwtFilter.getCurrentUserName());
            if(!userObj.equals(null)){

                if(userObj.getPassword().equals(requestmap.get("oldPassword"))){
                    userObj.setPassword(requestmap.get("newPassword"));
                    userDAO.save(userObj);
                    return CafeUtils.getResponseEntity("Password updated Suceessfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Incorrrect old Password",HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try{
            User userObj=userDAO.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(userObj) && !Strings.isNullOrEmpty(userObj.getEmail())){
                emailUtils.forgetMail(userObj.getEmail(),
                        "Creationinls  by Cafe Management System",
                        userObj.getPassword());
            }
            return CafeUtils.getResponseEntity("Check your mail Creaditionls",HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
