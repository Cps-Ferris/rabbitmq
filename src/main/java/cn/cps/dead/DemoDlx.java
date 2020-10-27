package cn.cps.dead;

import cn.cps.config.RabbitMQClient;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Cai Peishen
 * @Date: 2020/10/23 18:00
 * @Description: 延迟队列 又称 死信队列
 */
public class DemoDlx {

    // 队列名
    String queueName = "delay_queue";
    String exchange = "delay.exchange";
    String routingKey = "delay.#";
    String msg = "Hello RabbitMQ DLX Message!";

    // 死信队列的设置
    private static String deadQueueName = "dlx.queue";
    private static String deadExchange = "dlx.exchange";
    private static String deadRoutingKey = "#";

    @Test
    public void DlxPublish() throws IOException {
        Connection connection = RabbitMQClient.getConnection();
        Channel channel = connection.createChannel();

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .expiration("5000")
                .build();
        //发送消息
        channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
    }


    @Test
    public void DlxConsumer() throws IOException {
        Connection connection = RabbitMQClient.getConnection();
        Channel channel = connection.createChannel();
        // 声明一个普通的交换机 和 队列 以及路由

        channel.exchangeDeclare(exchange, "topic", true, false, null);

        // 设置死信队列
        Map<String , Object> arguments = new HashMap<String , Object>();
        arguments.put("x-dead-letter-exchange" , deadExchange);
        channel.exchangeDeclare(deadExchange , "topic" , true , false , null);
        channel.queueDeclare(deadQueueName , true , false , false , null);
        channel.queueBind(deadQueueName , deadExchange , deadRoutingKey);

        // 创建Queue
        channel.queueDeclare(queueName , true , false , false , arguments);

        channel.queueBind(queueName , exchange , deadRoutingKey);

        // 监听死信队列
        channel.basicConsume(deadQueueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("---------dlx consume message---------");
                System.err.println("consumerTag: " + consumerTag);
                System.err.println("envelope: " + envelope);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
        
        
        System.in.read();
    }


}
