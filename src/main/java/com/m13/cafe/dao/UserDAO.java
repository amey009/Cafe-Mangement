package com.m13.cafe.dao;

import com.m13.cafe.model.User;
import com.m13.cafe.DTO.UserDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {

    @Query(name = "User.findByEmailId")
    User findByEmailId(@Param("email") String email);

    /** select new com.m13.cafe.DTO.UserDTO
     *This is called a DTO projection.
     *It means you're not returning the full User entity,
    * but only selected fields packed into a custom wrapper class (UserDTO).
     * */
    @Query("select new com.m13.cafe.DTO.UserDTO(u.id,u.name,u.email,u.contactNumber,u.status) from User u where u.role='user'")
    List<UserDTO> findAllUser();

    @Query("select u.email from User u where u.role='admin'")
    List<String> getAllAdmin();


    @Transactional
    @Modifying
    @Query("update User u set u.status=:status where u.id=:id")
    Integer updateStatus(@Param("status") String status,@Param("id") Long id);

    User findByEmail(String email);
}
