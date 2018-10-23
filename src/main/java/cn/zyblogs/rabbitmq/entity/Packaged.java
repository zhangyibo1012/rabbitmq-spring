package cn.zyblogs.rabbitmq.entity;

import lombok.*;

/**
 * @Title: Packaged.java
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
public class Packaged {

    private String id;

    private String name;

    private String description;
}
