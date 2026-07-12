package com.duoc.bff_inscripciones.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_SOLICITUD_INSCRIPCION = "solicitud-inscripcion.exchange";
    public static final String ROUTING_KEY_SOLICITUD_INSCRIPCION = "solicitud-inscripcion.routingkey";

    @Bean
    public DirectExchange solicitudInscripcionExchange() {
        return new DirectExchange(EXCHANGE_SOLICITUD_INSCRIPCION);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}