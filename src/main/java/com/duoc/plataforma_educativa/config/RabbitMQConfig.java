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
 * Configuración del servicio de colas RabbitMQ (Semana 7).
 *
 * Define:
 *  - Cola durable "inscripciones.queue" (sobrevive a un reinicio del broker).
 *  - Exchange tipo Direct "inscripciones.exchange".
 *  - Binding entre la cola y el exchange mediante la routing key "inscripciones.routingkey".
 *  - Conversor de mensajes a JSON (Jackson2JsonMessageConverter), para poder enviar
 *    y recibir objetos Java (no solo texto plano) a través de la cola.
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_INSCRIPCIONES = "inscripciones.queue";
    public static final String EXCHANGE_INSCRIPCIONES = "inscripciones.exchange";
    public static final String ROUTING_KEY_INSCRIPCIONES = "inscripciones.routingkey";

    @Bean
    public Queue queue() {
        // durable = true -> la cola persiste aunque RabbitMQ se reinicie
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
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(); // permite enviar/recibir objetos, no solo texto
    }

    
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}