package com.doubeye.experiments.zbus;

public class ZBusComsumer {

}
/*
import io.zbus.mq.ConsumeHandler;
import io.zbus.mq.ConsumeThread;
import io.zbus.mq.Message;
import io.zbus.mq.MqClient;
import io.zbus.net.EventDriver;

import java.io.IOException;
*/
/**
 * Created by doubeye(doubeye@sina.com) on 2017/5/23.
 */
/*
public class ZBusComsumer {
    public static void main(String[] args) {
        EventDriver eventDriver = new EventDriver();
        MqClient client = new MqClient("localhost:15555", eventDriver);

        ConsumeThread thread = new ConsumeThread(client);
        // need to by the same with ZBusProducer`s producer`s topic
        thread.setTopic("test");
        thread.setConsumeHandler(new ConsumeHandler() {
            @Override
            public void handle(Message message, MqClient mqClient) throws IOException {
                System.out.println(message);
            }
        });
        thread.start();
    }
}
*/