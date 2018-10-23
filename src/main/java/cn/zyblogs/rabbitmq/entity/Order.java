package cn.zyblogs.rabbitmq.entity;

import lombok.*;

/**
 * @Title: Order.java
 * @Package cn.zyblogs.rabbitmq.entity
 * @Description: TODO
 * @Author ZhangYB
 * @Version V1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

    private String id;

    private String name;

    private String content;
}
