package cn.zyblogs.rabbitmq.convert;

import javafx.animation.ScaleTransition;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;


/**
 * @Title: TextMessageConverter.java
 * @Package cn.zyblogs.rabbitmq.convert
 * @Description: TODO  MessageConverter转换器 自定义转换器需要实现这个接口 重写方法
 * @Author ZhangYB
 * @Version V1.0
 */
public class TextMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

        return new Message(object.toString().getBytes(), messageProperties);

    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {

        String contentType = message.getMessageProperties().getContentType();
        if (null != contentType && contentType.contains("text")){
            return new String(message.getBody());
        }
        return message.getBody();
    }
}
