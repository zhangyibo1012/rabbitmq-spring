package cn.zyblogs.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSpringApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testAdmin()throws Exception{

        rabbitAdmin.declareExchange(new DirectExchange("test.direct" , false , false));

        rabbitAdmin.declareExchange(new TopicExchange("test.topic" , false , false));

        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout" , false , false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue" ,false));

        rabbitAdmin.declareQueue(new Queue("test.topic.queue" ,false));

        rabbitAdmin.declareQueue(new Queue("test.fanout.queue" ,false));

        // 绑定
        rabbitAdmin.declareBinding(new Binding("test.direct.queue", Binding.DestinationType.QUEUE, "test.direct", "direct", new HashMap<>()));

        // 绑定 直接创建队列 交换机 绑定关系 指定路由key
        rabbitAdmin.declareBinding(BindingBuilder
                .bind(new Queue("test.topic.queue" , false))
                .to(new TopicExchange("test.topic" ,false,false))
                .with("user.#")
                );

        // 绑定 直接创建队列 交换机 绑定关系 FanoutExchange类型不走路由键 性能最快
        rabbitAdmin.declareBinding(BindingBuilder
                .bind(new Queue("test.fanout.queue" , false))
                .to(new FanoutExchange("test.fanout" ,false,false))
        );

         // 清空队列 false  不等待
          rabbitAdmin.purgeQueue("test.topic.queue",false );

    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() throws Exception{
        // 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc","信息描述" );
        messageProperties.getHeaders().put("type","自定义消息类型" );
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);

        // 转换发送
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, message1 -> {
            System.err.println("=======添加额外设置=========");
            message1.getMessageProperties().getHeaders().put("desc","额外修改的信息描述" );
            message1.getMessageProperties().getHeaders().put("attr","额外新加的属性" );
            return message1;
        });
    }

    @Test
    public void testSendMessage2() throws Exception{
        // 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);
        rabbitTemplate.send("topic001" , "spring.abc" , message);

        // 转换发送
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send .");
        rabbitTemplate.convertAndSend("topic002", "rabbit.amqp", "hello object message send .");

    }

}
