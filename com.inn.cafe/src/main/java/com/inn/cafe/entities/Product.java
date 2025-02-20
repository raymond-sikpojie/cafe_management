package com.inn.cafe.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
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
@Table(name="products")
public class Product implements Serializable {

    @Serial
    private  static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", nullable = false)
    @JsonBackReference // Indicates its a child in the relationshio. It prevent the problem of infinite recursion
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private int price;

    @Column(name = "status")
    private String status = "true";


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
