package com.doubeye.commons.application;

import java.util.*;

/**
 * @author doubeye
 * @version 1.0.0
 * 一些对象在创建后需要由代码或应用服务进行善后处理，为了避免这些对象忘记释放，
 * 而造成潜在的资源占用，用此类来记录这些对象
 */
public class SelfManagedClosableDaemon {
    private static Map<Object, CreationInformation> createdObjects = new HashMap<>();
    public static void addClosable(Object closable, CreationInformation information) {
        createdObjects.put(closable, information);
    }

    /**
     * 移除指定对象
     * @param closable Closable对象
     */
    public static void removeClosable(Object closable) {
        createdObjects.remove(closable);
    }

    /**
     * 关闭所有注册的closable
     */
    public void closeAll() {
        List<Object> successClosed = new ArrayList<>();
        Set<Object> objects = createdObjects.keySet();
        for (Object closable : objects) {
            CreationInformation creationInformation = createdObjects.get(closable);
            //TODO 执行close
            successClosed.add(closable);
        }
        for (Object closable : successClosed) {
            createdObjects.remove(closable);
        }
    }
}
