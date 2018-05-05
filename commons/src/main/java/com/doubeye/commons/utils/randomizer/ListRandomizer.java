package com.doubeye.commons.utils.randomizer;

import java.util.List;
import java.util.Random;

/**
 * @author douebye
 * 根据指定的列表生成随机值
 */
public class ListRandomizer<E> implements Randomizer<E> {

    /**
     * 随机的取值列表
     */
    private List<E> elements;
    private int size;
    private Random random = new Random();

    public ListRandomizer(List<E> elements) {
        this.elements = elements;
        size = elements.size();
    }

    @Override
    public E get() {
        return elements.get(random.nextInt(size));
    }
}
