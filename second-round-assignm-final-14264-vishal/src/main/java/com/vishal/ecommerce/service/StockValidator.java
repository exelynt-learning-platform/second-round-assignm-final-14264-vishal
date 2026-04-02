package com.vishal.ecommerce.service;

import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class StockValidator {

    public void validateStock(Product product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStockQuantity() + ", Requested: " + requestedQuantity);
        }
    }

    public void validateStockForUpdate(Product product, int newQuantity) {
        validateStock(product, newQuantity);
    }

    public void validateStockForAddition(Product product, int currentQuantity, int additionalQuantity) {
        int total = currentQuantity + additionalQuantity;
        if (product.getStockQuantity() < total) {
            throw new BadRequestException("Insufficient stock for total quantity. Available: " +
                    product.getStockQuantity() + ", Requested total: " + total);
        }
    }
}