package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.ProductReqDto;
import com.vishal.ecommerce.dto.res.ProductResDto;

import java.util.*;

public interface ProductService {

        ProductResDto addProduct(ProductReqDto request);

            List<ProductResDto> getAllProducts();

                ProductResDto getProductById(Long id);

                    ProductResDto updateProduct(Long id, ProductReqDto request);

                        void deleteProduct(Long id);


}
