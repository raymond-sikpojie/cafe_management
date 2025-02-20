package com.inn.cafe.controller;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.entities.Product;
import com.inn.cafe.models.ProductDTO;
import com.inn.cafe.services.ProductService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/add")
    ResponseEntity<String> addNewProduct(@RequestBody(required = true) Product productRequest) {
        try {
            productService.addProduct(productRequest);
            return CafeUtils.getResponseEntity(CafeConstants.SUCCESS, HttpStatus.OK);
        } catch (NotFoundException e) {
            return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll")
    ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<ProductDTO> products = productService.getAllProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCategory(@RequestBody(required = true) Product productRequest) {
        try {
            productService.updateProduct(productRequest);
            return CafeUtils.getResponseEntity(CafeConstants.SUCCESS, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity("Product not found", HttpStatus.NOT_FOUND);

        } catch (InvalidInputDataException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productId) {
        try {
            productService.deleteProduct(productId);
            return CafeUtils.getResponseEntity(CafeConstants.SUCCESS, HttpStatus.OK);
        } catch (NotFoundException e) {
            return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateProductStatus(@RequestBody(required = true) Map<String, String> updateRequest) {
        try {
            productService.updateStatus(updateRequest);
            return CafeUtils.getResponseEntity(CafeConstants.SUCCESS, HttpStatus.OK);
        } catch (NotFoundException e) {
            return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getByCategory/{categoryId}")
    ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        try {
            List<ProductDTO> productList = productService.getProductsByCategory(categoryId);
            return new ResponseEntity<>(productList, HttpStatus.OK);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getById/{productId}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable Integer productId) {
        try {
            ProductDTO product = productService.getProductsById(productId);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (NotAuthorizedException e) {
            return new ResponseEntity<>( HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
