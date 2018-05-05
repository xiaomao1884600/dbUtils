package com.doubeye.commons.utils.randomizer;




/**
 * @author doubeye
 * 随机生成器
 */
public interface Randomizer<E> {
    /**
     * 获得随机值
     * @return 随机值
     */
    public E get();
}
