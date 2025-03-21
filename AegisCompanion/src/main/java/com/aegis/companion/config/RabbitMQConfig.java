package com.aegis.companion.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("com.smartcommunity.smart_community_platform.model.dto.notification"); // 信任指定包
        return classMapper;
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true); // 启用 mandatory 模式
        template.setReturnsCallback(returned -> {
            log.error("消息未路由到队列: {}", returned); // 记录未路由的消息
        });
        return template;
    }

    // 停车事件交换器
    @Bean
    public TopicExchange parkingExchange() {
        return new TopicExchange("parking.exchange", true, false);
    }

    // 停车事件队列
    @Bean
    public Queue parkingQueue() {
        return new Queue("parking.queue", true);
    }

    // 绑定关系
    @Bean
    public Binding parkingBinding() {
        return BindingBuilder.bind(parkingQueue())
                .to(parkingExchange())
                .with("parking.message.#");
    }

    //++++++++++++++++++++++++++++++++++++++社区活动
    @Bean
    public TopicExchange activityExchange() {
        return new TopicExchange("activity.exchange");
    }

    @Bean
    public Queue activityQueue() {
        return new Queue("activity.queue");
    }

    @Bean
    public Binding activityBinding() {
        return BindingBuilder.bind(activityQueue())
                .to(activityExchange())
                .with("activity.message.#");
    }

    //++++++++++++++++++++++++++++++++++++++医疗预约
    @Bean
    public TopicExchange medicalExchange() {
        return new TopicExchange("medical.exchange");
    }

    @Bean
    public Queue medicalQueue() {
        return new Queue("medical.queue");
    }

    @Bean
    public Binding medicalBinding() {
        return BindingBuilder.bind(medicalQueue())
                .to(medicalExchange())
                .with("medical.message.#");
    }

    //++++++++++++++++++++++++++++++++++++++工单系统
    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange("ticket.exchange", true, false);
    }

    @Bean
    public Queue ticketQueue() {
        return new Queue("ticket.queue", true);
    }

    @Bean
    public Binding ticketBinding() {
        return BindingBuilder.bind(ticketQueue())
                .to(ticketExchange())
                .with("ticket.message.#");
    }
}


