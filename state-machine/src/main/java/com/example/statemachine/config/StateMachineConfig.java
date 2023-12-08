package com.example.statemachine.config;


import com.example.statemachine.domain.ProductEvent;
import com.example.statemachine.domain.ProductState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<ProductState, ProductEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<ProductState, ProductEvent> states) throws Exception {

        states.withStates()
                .initial(ProductState.ORDERED)
                .state(ProductState.ASSEMBLING)
                .state(ProductState.PACKAGING)
                .state(ProductState.SHIPPED)
                .end(ProductState.SHIPPED)
                .end(ProductState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProductState, ProductEvent> transitions) throws Exception {
        transitions
                .withExternal().source(ProductState.ORDERED).target(ProductState.ASSEMBLING).event(ProductEvent.START_ASSEMBLE)
                .and()
                .withExternal().source(ProductState.ASSEMBLING).target(ProductState.PACKAGING).event(ProductEvent.END_ASSEMBLE)
                .and()
                .withExternal().source(ProductState.PACKAGING).target(ProductState.SHIPPED).event(ProductEvent.SHIP)
                .and()
                .withExternal().source(ProductState.ORDERED).target(ProductState.CANCELLED).event(ProductEvent.CANCEL)
                .and()
                .withExternal().source(ProductState.ASSEMBLING).target(ProductState.CANCELLED).event(ProductEvent.CANCEL);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ProductState, ProductEvent> config) throws Exception {
        StateMachineListenerAdapter<ProductState, ProductEvent> adapter = new StateMachineListenerAdapter<>() {


            @Override
            public void stateChanged(State<ProductState, ProductEvent> from, State<ProductState, ProductEvent> to) {
                log.info(String.format("State Changed from : %s, to: %s", from, to));
            }

        };

        config.withConfiguration().listener(adapter);
    }
}
