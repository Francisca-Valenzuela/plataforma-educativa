package com.duoc.plataforma_educativa.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del servicio de colas RabbitMQ (Semana 7 + EFT S9).
 *
 * Define dos flujos:
 *  1) "inscripciones.queue" (Semana 7, ya existente): resumen de una
 *     inscripción YA creada, para persistir en RESUMEN_COMPRA.
 *  2) "solicitud-inscripcion.queue" (EFT S9, nuevo): solicitud de
 *     inscripción entrante desde el BFF, antes de persistir.
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_INSCRIPCIONES = "inscripciones.queue";
    public static final String EXCHANGE_INSCRIPCIONES = "inscripciones.exchange";
    public static final String ROUTING_KEY_INSCRIPCIONES = "inscripciones.routingkey";

    public static final String QUEUE_SOLICITUD_INSCRIPCION = "solicitud-inscripcion.queue";
    public static final String EXCHANGE_SOLICITUD_INSCRIPCION = "solicitud-inscripcion.exchange";
    public static final String ROUTING_KEY_SOLICITUD_INSCRIPCION = "solicitud-inscripcion.routingkey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_INSCRIPCIONES, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_INSCRIPCIONES);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_INSCRIPCIONES);
    }

    @Bean
    public Queue solicitudInscripcionQueue() {
        return new Queue(QUEUE_SOLICITUD_INSCRIPCION, true);
    }

    @Bean
    public DirectExchange solicitudInscripcionExchange() {
        return new DirectExchange(EXCHANGE_SOLICITUD_INSCRIPCION);
    }

    @Bean
    public Binding solicitudInscripcionBinding(Queue solicitudInscripcionQueue,
                                                DirectExchange solicitudInscripcionExchange) {
        return BindingBuilder.bind(solicitudInscripcionQueue)
                .to(solicitudInscripcionExchange)
                .with(ROUTING_KEY_SOLICITUD_INSCRIPCION);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}