package com.doubeye.experiments.functorTest;

public class SETLItem {

}
/*
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.core.comparator.IsGreaterThanOrEqual;

import java.util.Comparator;
*/
/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/19.
 */
/*
public class SETLItem {
    private String name;
    private String code;
    private int price;
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static void main(String[] args) {
        Comparator comparator = new PriceComparator();
        BinaryPredicate bp = new IsGreaterThanOrEqual(comparator);
        SETLItem item1 = new SETLItem();
        item1.setPrice(100);
        SETLItem item2 = new SETLItem();
        item2.setPrice(99);

        if (bp.test(item1, item2)) {
            System.out.println("Item1 costs more than Item2!");
        } else {
            System.out.println("Item2 costs more than Item1!");
        }

        SETLItem item3 = new SETLItem();
        item3.setPrice(101);
        if (bp.test(item1, item3)) {
            System.out.println("Item1 costs more than Item3!");
        } else {
            System.out.println("Item3 costs more than Item1!");
        }
    }
}
*/
