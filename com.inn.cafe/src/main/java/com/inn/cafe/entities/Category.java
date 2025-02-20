package com.inn.cafe.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="categories")
public class Category implements Serializable {

    @Serial
    private  static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonManagedReference // Indicates its a parent in the relationshio. It prevent the problem of infinite recursion
    private List<Product> products;
}
