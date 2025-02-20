package com.inn.cafe.impl;

import com.inn.cafe.repository.BillRepository;
import com.inn.cafe.repository.CategoryRepository;
import com.inn.cafe.repository.ProductRepository;
import com.inn.cafe.services.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BillRepository billRepository;

    @Override
    public Map<String, Object> getCount() {
        Map<String, Object> details = new HashMap<>();
        details.put("categories", categoryRepository.count());
        details.put("products", productRepository.count());
        details.put("bills", billRepository.count());

        return details;
    }
}
