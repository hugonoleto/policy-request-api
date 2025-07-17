package com.acme.insurance.policy.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_NOTIFICATION;
import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_PAYMENT;
import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_STATE_CHANGED;
import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_SUBSCRIPTION;

@Configuration
public class RabbitMQConfig {

    private static final String POLICY_EXCHANGE = "policy.exchange";

    @Bean
    public DirectExchange policyExchange() {
        return new DirectExchange(POLICY_EXCHANGE);
    }

    @Bean
    public Queue policyStateChangedQueue() {
        return new Queue(POLICY_STATE_CHANGED, true);
    }

    @Bean
    public Queue policyPaymentQueue() {
        return new Queue(POLICY_PAYMENT, true);
    }

    @Bean
    public Queue policySubscriptionQueue() {
        return new Queue(POLICY_SUBSCRIPTION, true);
    }

    @Bean
    public Queue policyNotificationQueue() {
        return new Queue(POLICY_NOTIFICATION, true);
    }

    @Bean
    public Binding stateChangedBinding() {
        return BindingBuilder.bind(policyStateChangedQueue())
                .to(policyExchange())
                .with(POLICY_STATE_CHANGED);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(policyPaymentQueue())
                .to(policyExchange())
                .with(POLICY_PAYMENT);
    }

    @Bean
    public Binding subscriptionBinding() {
        return BindingBuilder.bind(policySubscriptionQueue())
                .to(policyExchange())
                .with(POLICY_SUBSCRIPTION);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(policyNotificationQueue())
                .to(policyExchange())
                .with(POLICY_NOTIFICATION);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        RetryInterceptorBuilder.StatelessRetryInterceptorBuilder retryInterceptorBuilder =
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(1000, 2.0, 10000);

        factory.setAdviceChain(retryInterceptorBuilder.build());
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonJsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}