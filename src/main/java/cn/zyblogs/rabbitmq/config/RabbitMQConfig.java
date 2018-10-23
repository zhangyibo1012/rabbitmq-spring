package cn.zyblogs.rabbitmq.config;

import cn.zyblogs.rabbitmq.adapter.MessageDelegate;
import cn.zyblogs.rabbitmq.constant.RabbitMqServer;
import cn.zyblogs.rabbitmq.convert.ImageMessageConverter;
import cn.zyblogs.rabbitmq.convert.PDFMessageConverter;
import cn.zyblogs.rabbitmq.convert.TextMessageConverter;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Title: RabbitConfig.java
 * @Package cn.zyblogs.rabbitmq.config
 * @Description: TODO
 * @Author ZhangYB
 * @Version V1.0
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(RabbitMqServer.IP);
        cachingConnectionFactory.setPort(RabbitMqServer.PORT);
        cachingConnectionFactory.setUsername(RabbitMqServer.USER_NAME);
        cachingConnectionFactory.setPassword(RabbitMqServer.PASS_WORD);
        cachingConnectionFactory.setVirtualHost(RabbitMqServer.VIRTUAL_HOST);
        cachingConnectionFactory.setConnectionTimeout(60000 * 2 );
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 必须设置为true 否则Spring容器不会加载RabbitAdmin类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        // 队列持久
        return new Queue("queue001", true);
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        // 队列持久
        return new Queue("queue002", true);
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        // 队列持久
        return new Queue("queue003", true);
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        // 队列持久
        return new Queue("image_queue", true);
    }

    @Bean
    public Queue queue_pdf() {
        // 队列持久
        return new Queue("pdf_queue", true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

           // 设置监控队列
            container.setQueues(queue001(),queue002(),queue003(),queue_image(),queue_pdf());

            // 当前消费者的数量
        container.setConcurrentConsumers(1);

        // 最大设置5
        container.setMaxConcurrentConsumers(5);

           // 是否重回队列
        container.setDefaultRequeueRejected(false);

        //  自动签收
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);

        // tag标签
        container.setConsumerTagStrategy(queueName -> queueName + "_" + UUID.randomUUID().toString());

        // 设置具体监听的方式  也可以使用适配器监听
//        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
//            String msg = new String(message.getBody());
//            System.err.println("-----------消费者:" + msg);
//        });

        // 适配器方式1: 默认适配器方法名为handleMessage
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        // 可以自定义设置
//        adapter.setDefaultListenerMethod("consumeMessage");
        // 可以添加一个转换器 从字节数组转换为String类型
//        container.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);

        // 适配器方式2 队列名和方法名称也可以进行匹配绑定
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        // 可以添加一个转换器 从字节数组转换为String类型
//        container.setMessageConverter(new TextMessageConverter());
//        Map<String , String>  queueOrTagToMethodName = new HashMap<>(2);
//        queueOrTagToMethodName.put("queue001", "method1");
//        queueOrTagToMethodName.put("queue002", "method2");
//        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        container.setMessageListener(adapter);

        // 1.1转换器json
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);

        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
//         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//         adapter.setDefaultListenerMethod("consumeMessage");
//         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//         // 指定信任所有包 默认信任"java.util", "java.lang"
//         javaTypeMapper.setTrustedPackages("*");
//         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//         adapter.setMessageConverter(jackson2JsonMessageConverter);
//         container.setMessageListener(adapter);

        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
//         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//         adapter.setDefaultListenerMethod("consumeMessage");
//         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//
//         Map<String, Class<?>> idClassMapping = new HashMap<>(16);
//         idClassMapping.put("order", cn.zyblogs.rabbitmq.entity.Order.class);
//         idClassMapping.put("packaged", cn.zyblogs.rabbitmq.entity.Packaged.class);
//
//         javaTypeMapper.setIdClassMapping(idClassMapping);
//
//         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//         adapter.setMessageConverter(jackson2JsonMessageConverter);
//         container.setMessageListener(adapter);


        //1.4 ext convert

        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        //全局的转换器
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConvert = new TextMessageConverter();
        convert.addDelegate("text", textConvert);
        convert.addDelegate("html/text", textConvert);
        convert.addDelegate("xml/text", textConvert);
        convert.addDelegate("text/plain", textConvert);

        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
        convert.addDelegate("json", jsonConvert);
        convert.addDelegate("application/json", jsonConvert);

        ImageMessageConverter imageConverter = new ImageMessageConverter();
        convert.addDelegate("image/png", imageConverter);
        convert.addDelegate("image", imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        convert.addDelegate("application/pdf", pdfConverter);


        adapter.setMessageConverter(convert);
        container.setMessageListener(adapter);

        container.setExposeListenerChannel(true);
        return container;
    }
}
