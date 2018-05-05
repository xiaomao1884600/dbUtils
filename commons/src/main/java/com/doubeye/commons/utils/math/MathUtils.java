package com.doubeye.commons.utils.math;

import java.util.Arrays;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0,0
 * Math类工具
 */
@SuppressWarnings("unused")
public class MathUtils {
    /**
     * 找到数组中最大的值
     * @param values 数值数组
     * @return 数组中的最大值
     */
    public static long multiMax(long[] values) {
        if (values.length == 0) {
            return Long.MAX_VALUE;
        } else if (values.length == 1) {
            return values[0];
        } else {
            long result = values[0];

            for (long value : values) {
                result = Math.max(result, value);
            }
            return result;
        }
    }

    /**
     * 找到数组中最小的值
     * @param values 数值数组
     * @return 数组中的最小值
     */
    public static long multiMin(long[] values) {
        if (values.length == 0) {
            return Long.MIN_VALUE;
        } else if (values.length == 1) {
            return values[0];
        } else {
            long result = values[0];
            for (long value : values) {
                result = Math.min(result, value);
            }
            return result;
        }
    }

    /**
     * 找到数组中最大的值
     * @param values 数值数组
     * @return 数组中的最大值
     */
    public static int multiMax(int[] values) {
        if (values.length == 0) {
            return Integer.MAX_VALUE;
        } else if (values.length == 1) {
            return values[0];
        } else {
            int result = values[0];
            for (int value : values) {
                result = Math.max(result, value);
            }
            return result;
        }
    }

    /**
     * 找到数组中最小的值
     * @param values 数值数组
     * @return 数组中的最小值
     */
    public static int multiMin(int[] values) {
        if (values.length == 0) {
            return Integer.MIN_VALUE;
        } else if (values.length == 1) {
            return values[0];
        } else {
            int result = values[0];
            for (int value : values) {
                result = Math.min(result, value);
            }
            return result;
        }
    }

    public static double[] getDoubleExponential(double[] values, int size, float alpha, float beta) {
        DoubleExponentialSmoothingForLinearSeries.Model model = DoubleExponentialSmoothingForLinearSeries.fit(values, alpha, beta);
        return model.forecast(size);
    }

}
