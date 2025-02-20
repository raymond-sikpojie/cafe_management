package com.inn.cafe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name="bills")
@NoArgsConstructor
@AllArgsConstructor
public class Bill implements Serializable {

    @Serial
    private  static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "payment")
    private String paymentMethod;

    @Column(name = "total")
    private int totalAmount;

    @Column(name = "productdetails", columnDefinition = "json")
    private String productDetails;

    @Column(name = "createdby")
    private String createdBy;
}
