package com.example.statemachine.service;

import com.example.statemachine.domain.ProductEntity;
import com.example.statemachine.domain.ProductEvent;
import com.example.statemachine.domain.ProductState;
import com.example.statemachine.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductStateChangeInterceptor extends StateMachineInterceptorAdapter<ProductState, ProductEvent> {

    private final ProductRepository paymentRepository;

    @Override
    public void preStateChange(State<ProductState, ProductEvent> state, Message<ProductEvent> message, Transition<ProductState, ProductEvent> transition, StateMachine<ProductState, ProductEvent> stateMachine, StateMachine<ProductState, ProductEvent> rootStateMachine) {
        Optional.ofNullable(message).flatMap(msg ->
                        Optional.ofNullable(msg.getHeaders().getOrDefault("product", null))).
                ifPresent(orderId1 -> {
                    ProductEntity productEntity = message.getHeaders().get("product", ProductEntity.class);
                    if (productEntity != null) {
                        productEntity.setState(state.getId());
                        paymentRepository.save(productEntity);
                    }
                });
    }
}