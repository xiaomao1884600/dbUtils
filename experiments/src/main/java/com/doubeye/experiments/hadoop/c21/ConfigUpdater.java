package com.doubeye.experiments.hadoop.c21;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ConfigUpdater {
    public static final String PATH = "/config";

    private ActiveKeyValueStore store;
    private Random random = new Random();

    public ConfigUpdater(String hosts) throws IOException, InterruptedException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    public void run() throws KeeperException, InterruptedException {
        while (true) {
            String value = random.nextInt(1000) + "";
            store.write(PATH, value);
            System.out.printf("Set %s to %s\n", PATH, value);
            TimeUnit.SECONDS.sleep(random.nextInt(10));
        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        while (true) {
            try {
                ConfigUpdater configUpdater = new ConfigUpdater(args[0]);
                configUpdater.run();
            } catch (KeeperException.SessionExpiredException e) {
                //
            } catch (KeeperException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
