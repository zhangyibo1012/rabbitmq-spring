package cn.zyblogs.rabbitmq.constant;

/**
 * @Title: RabbitMq.java
 * @Package cn.zyblogs.rabbitmq.api.constant
 * @Description: TODO
 * @Author ZhangYB
 * @Version V1.0ADDRESS
 */
public class RabbitMqServer {

    public final static String IP = "192.168.32.144";
    public final static int PORT = 5672;
    public final static String VIRTUAL_HOST = "/";
    public final static String USER_NAME = "guest";
    public final static String PASS_WORD = "guest";

    public static void main(String[] args) {
        System.out.println(IP+":" + PORT);
    }

}
