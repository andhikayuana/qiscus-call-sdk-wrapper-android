package com.qiscus.sdk.call.wrapper.util;

import java.util.Arrays;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public final class HashCode {
    private HashCode() {

    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}
