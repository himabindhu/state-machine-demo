package com.example.statemachine.service;

import com.example.statemachine.domain.ProductEntity;

public interface ProductService {

    ProductEntity newProduct(ProductEntity productEntity);
    ProductEntity startAssembly(Long productId);
    ProductEntity endAssembly(Long productId);
    ProductEntity shipProduct(Long productId);
    ProductEntity cancelProduct(Long productId);
}
