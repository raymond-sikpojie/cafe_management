package com.inn.cafe.impl;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.entities.Category;
import com.inn.cafe.jwt.JwtRequestFilter;
import com.inn.cafe.repository.CategoryRepository;
import com.inn.cafe.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Override
    public void addNewCategory(Category categoryRequest) throws NotAuthorizedException {
        if (jwtRequestFilter.isAdmin()) {
            if (ObjectUtils.isNotEmpty(categoryRequest)) {
                categoryRepository.save(categoryRequest);
                log.info("Category saved to the database");
            }
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }

    @Override
    public List<Category> getAllCategories(String name) {
        List<Category> allCategories;
        if (StringUtils.isEmpty(name)) {
            allCategories = categoryRepository.findCategoriesAndAllProductsTrue();
        } else {
            allCategories = categoryRepository.findByName(name);
        }
        return allCategories;
    }

    @Transactional
    @Override
    public void updateCategory(Category categoryRequest) throws NotFoundException, InvalidInputDataException {
        if (jwtRequestFilter.isAdmin()) {
            if (ObjectUtils.anyNotNull(categoryRequest) && StringUtils.isNotEmpty(categoryRequest.getName())) {
                Optional<Category> category = categoryRepository.findById(categoryRequest.getId());
                if (category.isPresent()) {
                    categoryRepository.updateCategory(categoryRequest.getId(), categoryRequest.getName());
                } else {
                    throw new NotFoundException("Category not found");
                }

                log.info("Category has been updated");
            } else {
                throw new InvalidInputDataException("Invalid Data");
            }
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }
}
