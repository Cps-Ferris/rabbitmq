package cn.cps.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQClient {

    public static Connection getConnection(){

        // 创建Connection工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.181.22");
        factory.setPort(5672);
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("/test");

        // 创建Connection
        Connection conn = null;
        try {
            conn = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回
        return conn;
    }

}
