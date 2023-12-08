package com.example.statemachine.service;

import com.example.statemachine.domain.ProductEntity;
import com.example.statemachine.domain.ProductEvent;
import com.example.statemachine.domain.ProductState;
import com.example.statemachine.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository repository;
    private final StateMachineFactory<ProductState, ProductEvent> stateMachineFactory;
    private final ProductStateChangeInterceptor productStateChangeInterceptor;

    @Override
    @Transactional
    public ProductEntity newProduct(ProductEntity productEntity) {
        productEntity.setState(ProductState.ORDERED);
        return repository.save(productEntity);
    }

    @Override
    @Transactional
    public ProductEntity startAssembly(Long productId) {
        ProductEntity product = repository.getReferenceById(productId);
        StateMachine<ProductState, ProductEvent> stateMachine = build(product);
        sendEvent(product, stateMachine, ProductEvent.START_ASSEMBLE);
        return product;
    }

    @Override
    @Transactional
    public ProductEntity endAssembly(Long productId) {
        ProductEntity product = repository.getReferenceById(productId);
        StateMachine<ProductState, ProductEvent> stateMachine = build(product);
        sendEvent(product, stateMachine, ProductEvent.END_ASSEMBLE);
        return product;
    }

    @Override
    @Transactional
    public ProductEntity shipProduct(Long productId) {
        ProductEntity product = repository.getReferenceById(productId);
        sendEvent(product, build(product), ProductEvent.SHIP);
        return product;
    }

    @Override
    @Transactional
    public ProductEntity cancelProduct(Long productId) {
        ProductEntity product = repository.getReferenceById(productId);
        sendEvent(product, build(product), ProductEvent.CANCEL);
        return product;
    }

    private void sendEvent(ProductEntity product, StateMachine<ProductState, ProductEvent> sm, ProductEvent event){
        Message<ProductEvent> msg = MessageBuilder.withPayload(event)
                .setHeader("product", product)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<ProductState, ProductEvent> build(ProductEntity product){

        StateMachine<ProductState, ProductEvent> sm = stateMachineFactory.getStateMachine(Long.toString(product.getId()));
        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(productStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(product.getState(), null, null, null));
                });
        sm.start();

        return sm;
    }
}
