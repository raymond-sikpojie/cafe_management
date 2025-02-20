package com.inn.cafe.repository;

import com.inn.cafe.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//    @Query(value = "SELECT u FROM User u where u.email = ?1")
//    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    @Transactional
    @Modifying
//    @Query("update User u set u.id = ?1, u.name = ?2, u.phoneNumber = ?3, u.email = ?4, u.password = ?5")
    @Query("UPDATE User u SET u.status = ?1 WHERE u.id=?2")
    void updateUserStatus(String status, int id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = ?2 WHERE u.email = ?1")
    void updatePassword(String email, String password);

    @Query("SELECT u FROM User u WHERE u.role = 'admin'")
    List<User> findAdminUsers();
}
