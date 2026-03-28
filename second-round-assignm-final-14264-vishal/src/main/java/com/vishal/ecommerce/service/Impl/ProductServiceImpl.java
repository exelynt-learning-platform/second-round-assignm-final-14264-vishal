package com.vishal.ecommerce.service.Impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vishal.ecommerce.dto.req.ProductReqDto;
import com.vishal.ecommerce.dto.res.ProductResDto;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.repository.ProductRepository;
import com.vishal.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductResDto addProduct(ProductReqDto request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        productRepository.save(product);

        ProductResDto response = new ProductResDto();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());

        return response;
    }

    @Override
    public List<ProductResDto> getAllProducts() {

        List<Product> products = productRepository.findAll();
        List<ProductResDto> response = new ArrayList<>();

        for (Product product : products) {
            ProductResDto dto = new ProductResDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            response.add(dto);
        }

        return response;
    }

    @Override
    public ProductResDto getProductById(Long id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        ProductResDto response = new ProductResDto();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());

        return response;
    }

    @Override
    public ProductResDto updateProduct(Long id, ProductReqDto request) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        productRepository.save(product);

        ProductResDto response = new ProductResDto();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());

        return response;
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        productRepository.delete(product);
    }
    
}
