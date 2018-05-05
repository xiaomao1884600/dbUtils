package com.doubeye.experiments.hadoop.c09;

import org.apache.hadoop.io.WritableComparable;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntPair implements WritableComparable<IntPair> {
    private int first;
    private int second;

    public IntPair() {

    }

    public IntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public void set(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(first);
        dataOutput.writeInt(second);
    }

    @Override
    public int hashCode() {
        return first * 163 + second;
    }

    public boolean equals(Object o) {
        if (o instanceof IntPair) {
            IntPair ip = (IntPair) o;
            return first == ip.first && second == ip.second;
        }
        return false;
    }

    @Override
    public String toString() {
        return first + "\t" + second;
    }

    @Override
    public int compareTo(@NotNull IntPair o) {
        int cmp = compare(first, second);
        if (cmp != 0) {
            return cmp;
        }
        return compare(second, o.second);
    }



    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }

    public static int compare(int a, int b) {
        return (Integer.compare(a, b));
    }
}
