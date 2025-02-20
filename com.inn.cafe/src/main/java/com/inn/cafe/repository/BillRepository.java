package com.inn.cafe.repository;

import com.inn.cafe.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {

    @Query("SELECT b FROM Bill b WHERE b.createdBy = ?1 ORDER BY b.id desc")
    List<Bill> getBillByUsername(String currentUser);

    @Query("SELECT b FROM Bill b ORDER BY b.id desc")
    List<Bill> getAllBills();
}
