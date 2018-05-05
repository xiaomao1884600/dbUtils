package com.doubeye.commons.utils.randomizer;
import java.util.Random;

/**
 * @author doubeye
 * 范围随机值
 */
public class RangeRandomizer implements NumberRandomizer{

    /**
     * 随机值的最小值（包含）
     */
    private Number lower;
    /**
     * 随机值的最大值（不包含）
     */
    private Number upperWithout;

    private Random random = new Random();
    public RangeRandomizer(Number lower, Number upperWithout) {
        this.lower = lower;
        this.upperWithout = upperWithout;
    }
    @Override
    public Number get() {
        if (upperWithout instanceof Integer) {
            return  lower.intValue() + random.nextInt(upperWithout.intValue());
        } else if (upperWithout instanceof Double) {
            return lower.doubleValue() + ((upperWithout.doubleValue() - lower.doubleValue()) * random.nextDouble());
        } else if (upperWithout instanceof Long) {
            return lower.longValue() + ((upperWithout.longValue() - lower.longValue()) * random.nextLong());
        } else if (upperWithout instanceof Float) {
            return lower.floatValue() + ((upperWithout.floatValue() - lower.floatValue()) * random.nextFloat());
        }
        return 0;
    }
}
