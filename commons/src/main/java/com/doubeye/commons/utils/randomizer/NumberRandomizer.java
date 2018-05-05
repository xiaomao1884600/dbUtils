package com.doubeye.commons.utils.randomizer;

/**
 * @author doubeye
 * @param <E> 随机数接口
 */
public interface NumberRandomizer<E extends Number> extends Randomizer {
    /**
     * 返回一个随机数
     * @return 随机数
     */
    @Override
    Number get();
}
