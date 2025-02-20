package com.inn.cafe.repository;

import com.inn.cafe.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // TODO: WORK ON THESE QUERIES. LEARN MORE ABOUT JOINS
    @Query("SELECT c FROM Category c JOIN c.products p WHERE c.id = p.category.id GROUP BY c HAVING COUNT(CASE WHEN p.status = 'false' THEN 1 END) = 0")
    List<Category> findCategoriesAndAllProductsTrue();


//    @Query("SELECT c FROM Category c WHERE NOT EXISTS (SELECT p FROM c.products p WHERE p.status = 'false') AND c.name = ?1")
//    List<Category> findByNameAndAllProductsTrue(String name);


    List<Category> findByName(String name);

    @Modifying
    @Query("UPDATE Category c SET c.name = ?2 WHERE c.id=?1")
    void updateCategory(int id, String name);
}
