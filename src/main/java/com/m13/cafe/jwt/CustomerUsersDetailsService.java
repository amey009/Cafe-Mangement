package com.m13.cafe.jwt;

import com.m13.cafe.dao.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service

public class CustomerUsersDetailsService implements UserDetailsService {
        @Autowired
        UserDAO userDAO;

        private com.m13.cafe.model.User userDetail;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            log.info("Inside loadUserByUsername{}",username);
            userDetail= userDAO.findByEmailId(username);
            if (!Objects.isNull(userDetail)){
                return new User(userDetail.getEmail(), userDetail.getPassword(),new ArrayList<>());
            }
            else
                throw new UsernameNotFoundException("User Not Found....!");
        }

//        public com.m13.cafe.model.User getUserDetail(){
//            com.m13.cafe.model.User user=userDetail;
//            user.setPassword(null);
//            return userDetail;
//        }

    public com.m13.cafe.model.User getUserDetail(){
            return userDetail;
    }


}
