package com.qiscus.sdk.call.agora.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @author tfkbudi (tfkbudi@gmail.com)
 * @since 2/13/18
 */

public final class DimUtil {
    private DimUtil() {

    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
