package com.inn.cafe.impl;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.entities.Category;
import com.inn.cafe.entities.Product;
import com.inn.cafe.jwt.JwtRequestFilter;
import com.inn.cafe.models.ProductDTO;
import com.inn.cafe.repository.CategoryRepository;
import com.inn.cafe.repository.ProductRepository;
import com.inn.cafe.services.ProductService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Override
    public void addProduct(Product productRequest) throws NotFoundException {
        if (jwtRequestFilter.isAdmin()) {
            if (ObjectUtils.isNotEmpty(productRequest)) {
                Category category = categoryRepository.findById(productRequest.getCategory().getId())
                        .orElseThrow(() -> new NotFoundException("Category not found"));
                productRequest.setCategory(category);
                productRepository.save(productRequest);
                log.info("Product saved to the database");
            }
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(CafeUtils::mapProductToDTO).toList();
    }

    @Transactional
    @Override
    public void updateProduct(Product productRequest) throws NotFoundException, InvalidInputDataException {
        if (jwtRequestFilter.isAdmin()) {
            if (ObjectUtils.anyNotNull(productRequest) && StringUtils.isNoneEmpty(
                    productRequest.getName(),
                    productRequest.getStatus(),
                    productRequest.getDescription(),
                    String.valueOf(productRequest.getId()),
                    String.valueOf(productRequest.getPrice())
            )) {
                Optional<Product> productOptional = productRepository.findById(productRequest.getId());
                if (productOptional.isPresent()) {
                    Product product = productOptional.get();
                    productRepository.updateProduct(productRequest.getId(), productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getStatus());
                } else {
                    throw new NotFoundException("Product not found");
                }
                log.info("Product has been updated");
            } else {
                throw new InvalidInputDataException("Invalid Data provided");
            }
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }

    @Override
    public void deleteProduct(int productId) throws NotFoundException, NotAuthorizedException {
        if (jwtRequestFilter.isAdmin()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            productRepository.delete(product);
            log.info("Product has been deleted");
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }

    @Override
    public void updateStatus(Map<String, String> updateStatusRequest) throws NotFoundException, NotAuthorizedException {
        if (jwtRequestFilter.isAdmin()) {
            Product product = productRepository.findById(Integer.valueOf(updateStatusRequest.get("id")))
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            product.setStatus(updateStatusRequest.get("status"));
            productRepository.save(product);
            log.info("Product has been updated");
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }

    @Override
    public List<ProductDTO> getProductsByCategory(Integer categoryId) throws NotAuthorizedException {
        if (jwtRequestFilter.isAdmin()) {
            List<Product> products = productRepository.findProductsByCategoryAndStatus(categoryId);
            return products.stream().map((product -> CafeUtils.mapProductToDTO(product))).toList();
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }

    @Override
    public ProductDTO getProductsById(Integer productId) throws NotFoundException, NotAuthorizedException {
        if (jwtRequestFilter.isAdmin()) {
            log.info("Fetching product from the database");
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            return CafeUtils.mapProductToDTO(product);
        } else {
            log.error("User not authorized");
            throw new NotAuthorizedException("Not Authorized to carry out this operation");
        }
    }
}
