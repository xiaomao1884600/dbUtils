package com.doubeye.temperary;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author doubeye
 * 测试一下嵌套语句的用法
 */
@SuppressWarnings("WeakerAccess")
public class NestedLambda {


    public static void main(String[] args) {
        Optional.of(new Outer()).map(Outer::getNested).map(Nested::getInner).map(Inner::getS).ifPresent(System.out::println);
        Outer outer = new Outer();
        resolve(() -> outer.getNested().getInner().getS()).ifPresent(System.out::println);

    }

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        }
        catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
@SuppressWarnings("unused")
class Outer {
    Outer () {

    }

    private Nested nested;

    public Nested getNested() {
        return nested;
    }
}
class Nested {
    private Inner inner;

    public Inner getInner() {
        return inner;
    }
}

class Inner {
    private String s;

    public String getS() {
        return s;
    }
}
