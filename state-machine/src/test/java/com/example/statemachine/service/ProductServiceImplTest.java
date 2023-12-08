package com.example.statemachine.service;

import com.example.statemachine.domain.ProductEntity;
import com.example.statemachine.domain.ProductState;
import com.example.statemachine.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ProductServiceImplTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    ProductEntity product;

    @BeforeEach
    void setUp() {
        product = ProductEntity.builder().id(123456L).productName("solar_panel1").build();
    }

    @Transactional
    @Test
    void assemble() {
        ProductEntity savedProduct = productService.newProduct(product);
        productService.startAssembly(savedProduct.getId());
        ProductEntity result = productRepository.getReferenceById(savedProduct.getId());
        assert result.getState().name().equals("ASSEMBLING");
        System.out.println(result);
    }
}
