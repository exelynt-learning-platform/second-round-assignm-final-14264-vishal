package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.ProductReqDto;
import com.vishal.ecommerce.dto.res.ProductResDto;

import java.util.*;

public interface ProductService {


            List<ProductResDto> getAllProducts();

                ProductResDto getProductById(Long id);

                    ProductResDto createProduct(ProductReqDto request);



}
