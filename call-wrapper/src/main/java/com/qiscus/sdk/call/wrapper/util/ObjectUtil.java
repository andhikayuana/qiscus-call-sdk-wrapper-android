package com.qiscus.sdk.call.wrapper.util;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public final class ObjectUtil {
    private ObjectUtil() {

    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
