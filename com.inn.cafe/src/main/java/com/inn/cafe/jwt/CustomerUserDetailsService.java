package com.inn.cafe.jwt;

import com.inn.cafe.entities.User;
import com.inn.cafe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User customer = getUser(email);
        String userEmail = customer.getEmail();
        String password = customer.getPassword();
        return new org.springframework.security.core.userdetails.User(userEmail, password, new ArrayList<>());
    }

    public com.inn.cafe.entities.User getUser(String email) throws UsernameNotFoundException {
        com.inn.cafe.entities.User customer;
        log.info("Getting user with email: " + email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            customer = optionalUser.get();
            return customer;
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

//    public UserDTO getUserDTO(String email) throws UsernameNotFoundException {
//        User customer = getUser(email);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setEmail(customer.getEmail());
//        userDTO.setId(customer.getId());
//        userDTO.setName(customer.getName());
//        userDTO.setRole(customer.getRole());
//        userDTO.setPhoneNumber(customer.getPhoneNumber());
//        userDTO.setStatus(customer.getStatus());
//        return userDTO;
//    }
}
