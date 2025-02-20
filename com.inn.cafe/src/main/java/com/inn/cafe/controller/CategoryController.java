package com.inn.cafe.controller;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.entities.Category;
import com.inn.cafe.services.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<String> addNewCategory(@RequestBody(required = true) Category categoryRequest) {
        try {
            categoryService.addNewCategory(categoryRequest);
            return CafeUtils.getResponseEntity(CafeConstants.SUCCESS, HttpStatus.OK);
        } catch (NotAuthorizedException e) {
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(required = false) String name) {
        try{
            List<Category> categories= categoryService.getAllCategories(name);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCategory(@RequestBody(required = true) Category categoryRequest) {
        try {
            categoryService.updateCategory(categoryRequest);
            return CafeUtils.getResponseEntity(CafeConstants.SUCCESS, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);

        }
        catch (InvalidInputDataException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
