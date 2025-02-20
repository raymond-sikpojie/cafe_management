package com.inn.cafe.repository;

import com.inn.cafe.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Integer> {

    @Modifying
    @Query("UPDATE Product p SET p.name = :name, p.description = :description, p.price = :price, p.status = :status WHERE p.id = :id")
    void updateProduct(
            @Param("id") int id,
            @Param("name") String name,
            @Param("description") String description,
            @Param("price") int price,
            @Param("status") String status
    );

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.id = ?1 AND p.status = 'true'")
    List<Product> findProductsByCategoryAndStatus(int categoryId);


}
