package com.doubeye.experiments.zbus;
/*
import io.zbus.mq.Broker;
import io.zbus.mq.Message;
import io.zbus.mq.Producer;
import io.zbus.mq.ProducerConfig;

import java.io.IOException;
*
/**
 * Created by doubeye(doubeye@sina.com) on 2017/5/23.
 */
/*
public class ZBusProducer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Broker broker = new Broker();
        try {
            broker.addServer("localhost:15555");

            ProducerConfig config = new ProducerConfig(broker);
            Producer producer = new Producer(config);
            producer.declareTopic("test");

            Message message = new Message();
            message.setTopic("test");
            message.setTag("group1.test");
            String messageContent = "hello zbus world at" + System.currentTimeMillis();
            System.out.println(messageContent);
            message.setBody(messageContent);

            Message response = producer.publish(message);
            System.out.println(response);
        } finally {
            broker.close();
        }
    }
}
*/
public class ZBusProducer {

}