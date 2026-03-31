package com.vishal.ecommerce.service.Impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vishal.ecommerce.dto.req.ProductReqDto;
import com.vishal.ecommerce.dto.res.ProductResDto;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.ProductRepository;
import com.vishal.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductResDto addProduct(ProductReqDto request) {

        if (productRepository.findByName(request.getName()) != null) {
        throw new BadRequestException("Product with same name already exists");
    }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        

        productRepository.save(product);

        return mapToDto(product);

    }

    @Override
    public List<ProductResDto> getAllProducts() {

        return productRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
}

    

    @Override
    public ProductResDto getProductById(Long id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        return mapToDto(product);

    }

    @Override
    @Transactional

    public ProductResDto updateProduct(Long id, ProductReqDto request) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        productRepository.save(product);

        return mapToDto(product);

    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.delete(product);
    }

    private ProductResDto mapToDto(Product product) {
    ProductResDto dto = new ProductResDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setDescription(product.getDescription());
    dto.setPrice(product.getPrice());
    dto.setStock(product.getStock());
    return dto;
}

    
}
