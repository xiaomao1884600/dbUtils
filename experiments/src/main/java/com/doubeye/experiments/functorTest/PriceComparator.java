package com.doubeye.experiments.functorTest;

import java.util.Comparator;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/19.
 */
public class PriceComparator implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        // return (((SETLItem)o1).getPrice() - ((SETLItem)o2).getPrice());
        return 0;
    }
}
