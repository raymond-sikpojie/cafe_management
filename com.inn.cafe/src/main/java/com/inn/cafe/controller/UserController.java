package com.inn.cafe.controller;

import com.inn.cafe.Exceptions.UpdateUserException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.Exceptions.UserSignUpException;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.entities.User;
import com.inn.cafe.jwt.JwtRequestFilter;
import com.inn.cafe.models.JwtResponse;
import com.inn.cafe.models.UserDTO;
import com.inn.cafe.services.UserService;
import com.inn.cafe.utils.CafeUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody(required = true) User userRequest) {
        return userService.userLogin(userRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody(required = true) User userRequest) {
        try {
            if (StringUtils.isEmpty(userRequest.getEmail()) || StringUtils.isEmpty(userRequest.getName())) {
                throw new UserSignUpException("Invalid Data Sent to the API");
            }
            userService.userSignUp(userRequest);
            return CafeUtils.getResponseEntity(CafeConstants.USER_SIGNUP_SUCCESS, HttpStatus.OK);

        } catch (UserSignUpException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.USER_ALREADY_EXISTS, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.USER_SIGNUP_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            if (jwtRequestFilter.isAdmin()) {
                List<UserDTO> users = userService.getAllUsers();
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateuser(@RequestBody(required = true) User userRequest) {
        try {
            userService.updateUserStatus(userRequest);
            return CafeUtils.getResponseEntity(CafeConstants.USER_STATUS_UPDATED, HttpStatus.OK);

        } catch (UpdateUserException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity("Not authorized to perform this operation", HttpStatus.UNAUTHORIZED);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.USER_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkToken")
    public ResponseEntity<String> checkToken() {
        try {
          String token = userService.checkToken();
            return CafeUtils.getResponseEntity("true", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody(required = true) Map<String, String> requestMap){
        try {
            userService.changePassword(requestMap);
            return CafeUtils.getResponseEntity(CafeConstants.PASSWORD_UPDATED, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.USER_NOT_FOUND, HttpStatus.NOT_FOUND);

        } catch (UpdateUserException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody(required = true) Map<String, String> requestMap) {
        try{
            userService.forgotPassword(requestMap);
            return CafeUtils.getResponseEntity("Your login Credentials have been sent to your email", HttpStatus.OK);

        } catch (NotFoundException | MessagingException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.FORGOT_PASSWORD_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
