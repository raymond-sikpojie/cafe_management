package com.inn.cafe.impl;

import com.inn.cafe.Exceptions.UpdateUserException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.Exceptions.UserSignUpException;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.entities.User;
import com.inn.cafe.jwt.CustomerUserDetailsService;
import com.inn.cafe.jwt.JwtRequestFilter;
import com.inn.cafe.jwt.JwtService;
import com.inn.cafe.models.JwtResponse;
import com.inn.cafe.models.UserDTO;
import com.inn.cafe.repository.UserRepository;
import com.inn.cafe.services.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtService jwtService;

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public void userSignUp(User userRequest) throws UserSignUpException {
        //TODO: Need to hash password before saving into Db
        // Hash the password here before saving
        // String hashedPassword = passwordEncoder.encode(newPassword);

        Optional<User> user = userRepository.findByEmail(userRequest.getEmail());
        if (user.isEmpty()) {
            log.info("Saving user to the database");
            userRepository.save(userRequest);
        } else {
            throw new UserSignUpException("This user already exists");
        }
    }

    @Override
    public ResponseEntity<JwtResponse> userLogin(User userRequest) {
        String loginEmail = userRequest.getEmail();
        String loginPassword = userRequest.getPassword();
        JwtResponse jwtResponse = new JwtResponse();

        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginEmail, loginPassword));

            if (auth.isAuthenticated()) {
                User user = customerUserDetailsService.getUser(userRequest.getEmail());
                if (user.getStatus().equalsIgnoreCase("true")) {
                    String token = jwtService.generateToken(user.getEmail(), user.getRole());
                    jwtResponse.setMessage("JwtToken");
                    jwtResponse.setToken(token);
                    log.info("Login Successful");
                    return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
                } else {
                    jwtResponse.setMessage("Wait for admin approval");
                    log.info("Error while logging in");
                    return new ResponseEntity<>(jwtResponse, HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        jwtResponse.setMessage("Invalid login credentials");
        return new ResponseEntity<>(jwtResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByRole(CafeConstants.USER);
        return users.stream().map(user -> CafeUtils.mapUserToDTO(user)).toList();
    }

//    @Override
//    public List<UserDTO> getAllUsers() {
//        List<User> users = userRepository.findByRole(CafeConstants.USER);
//        return users.stream().map(user -> UserDTO
//                .builder()
//                .id(user.getId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .phoneNumber(user.getPhoneNumber())
//                .status(user.getStatus())
//                .role("")
//                .build()
//        ).toList();
//    }

    @Override
    public void updateUserStatus(User userRequest) throws UpdateUserException, NotFoundException {
        if (jwtRequestFilter.isAdmin()) {
            Optional<User> optionalUser = userRepository.findById(userRequest.getId());
            if (optionalUser.isPresent()) {
                userRepository.updateUserStatus(userRequest.getStatus(), userRequest.getId());
                String userEmail = optionalUser.get().getEmail();

                List<User> adminUsers = userRepository.findAdminUsers();

                List<String> adminEmailList = adminUsers.stream().map(User::getEmail).collect(Collectors.toList());
                sendMailToAllAdmin(userRequest.getStatus(), userEmail, adminEmailList);

            } else {
                throw new NotFoundException("User not found");
            }
        } else {
            throw new UpdateUserException("Not authorized to perform this operation");
        }
    }

    @Override
    public String checkToken() {
        return "true";
    }

    @Override
    public void changePassword(Map<String, String> requestMap) throws NotFoundException, UpdateUserException {
        String userEmail = jwtRequestFilter.getCurrentUser();
        String oldPassword = requestMap.get("oldPassword");
        String newPassword = requestMap.get("newPassword");
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getPassword().equalsIgnoreCase(oldPassword)) {
            log.info("updating user password");
            userRepository.updatePassword(userEmail, newPassword);
        } else {
            log.info("Old password does not match new password");
            throw new UpdateUserException("Something went wrong");
        }
    }

    @Override
    public void forgotPassword(Map<String, String> requestMap) throws NotFoundException, MessagingException {
        String userEmail = requestMap.get("email");
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));

        if (StringUtils.isNoneEmpty(user.getEmail()) && StringUtils.equalsIgnoreCase(requestMap.get("email"), user.getEmail())) {
            emailUtils.forgotPasswordEmail(userEmail, "Recover Forgotten Password for " + user.getName(), user.getPassword());
            log.info("Password recovery email sent");
        }
    }

    private void sendMailToAllAdmin(String status, String userName, List<String> adminEmailList) {
        String currentAdminEmail = jwtRequestFilter.getCurrentUser();
        adminEmailList.remove(currentAdminEmail); // remove the current user since they're the main recipient

        if (status != null && status.equalsIgnoreCase("true")) {
            String emailBody = "Account " + userName + ",   \n has been approved by \nADMIN: " + jwtRequestFilter.getCurrentUser();

            sendMail(currentAdminEmail,
                    "Account Approved",
                    "Account: " + userName + ",   \n has been approved by \nADMIN: " + jwtRequestFilter.getCurrentUser(),
                    adminEmailList
            );
        } else {
            sendMail(currentAdminEmail,
                    "Account Disabled",
                    "Account " + userName + ",   \n has been disabled by \nADMIN: " + jwtRequestFilter.getCurrentUser(),
                    adminEmailList
            );
        }
    }

    private void sendMail(String currentAdminEmail, String emailSubject, String emailBody, List<String> adminEmailList) {
        emailUtils.sendSimpleMailMessage(
                currentAdminEmail,
                emailSubject,
                emailBody,
                adminEmailList);
    }
}
