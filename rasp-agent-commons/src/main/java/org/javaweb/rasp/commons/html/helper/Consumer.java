package org.javaweb.rasp.commons.html.helper;

/**
 A functional interface (ala Java's {link java.util.function.Consumer} interface, implemented here for cross compatibility with Android.
 @param <T> the input type
 */
public interface Consumer<T> {

    /**
     * Execute this operation on the supplied argument. It is expected to have side effects.
     *
     * @param t the input argument
     */
    void accept(T t);
}
