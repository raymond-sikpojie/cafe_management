package com.inn.cafe.services;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.entities.Product;
import com.inn.cafe.models.ProductDTO;

import java.util.List;
import java.util.Map;

public interface ProductService {
    void  addProduct(Product productRequest) throws NotFoundException;

    List<ProductDTO> getAllProducts();

    void updateProduct(Product productRequest) throws NotFoundException, InvalidInputDataException;

    void deleteProduct(int productId) throws NotFoundException, NotAuthorizedException;

    void updateStatus(Map<String, String> updateStatusRequest) throws NotFoundException, NotAuthorizedException;

    List<ProductDTO> getProductsByCategory(Integer categoryId) throws NotAuthorizedException;

    ProductDTO getProductsById(Integer productId) throws NotFoundException, NotAuthorizedException;
}
