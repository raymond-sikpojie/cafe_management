package com.inn.cafe.services;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.entities.Category;

import java.util.List;

public interface CategoryService {
    void addNewCategory(Category categoryRequest) throws NotAuthorizedException;

    List<Category> getAllCategories(String name);

    void updateCategory(Category categoryRequest) throws NotFoundException, InvalidInputDataException;
}
