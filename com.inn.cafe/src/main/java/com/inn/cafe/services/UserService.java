package com.inn.cafe.services;

import com.inn.cafe.Exceptions.UpdateUserException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.Exceptions.UserSignUpException;
import com.inn.cafe.entities.User;
import com.inn.cafe.models.JwtResponse;
import com.inn.cafe.models.UserDTO;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    void userSignUp(User userRequest) throws UserSignUpException;

    ResponseEntity<JwtResponse> userLogin(User userRequest);

    List<UserDTO> getAllUsers();

    void updateUserStatus(User userRequest) throws UpdateUserException, NotFoundException;

    String checkToken();

    void changePassword(Map<String, String> requestMap) throws NotFoundException, UpdateUserException;

    void forgotPassword(Map<String, String> requestMap) throws NotFoundException, MessagingException;

}
