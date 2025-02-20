package com.inn.cafe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="users")
public class User implements Serializable {

    @Serial
    private  static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "role")
    private String role;

    public User(){

    };

    public User(int id, String name, String phoneNumber, String email, String password, String status, String role) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(status, user.status) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phoneNumber, email, password, status, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
