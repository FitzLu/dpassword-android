package com.dpass.android.utils

import android.support.v4.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable
import android.graphics.PorterDuff
import android.support.annotation.ColorInt



class ColorTint {

    companion object {

        /**
         * Return a tint drawable
         *
         * @param drawable
         * @param color
         * @param forceTint
         * @return
         */
        fun getTintDrawable(drawable: Drawable, @ColorInt color: Int, forceTint: Boolean): Drawable {
            if (forceTint) {
                drawable.clearColorFilter()
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawable.invalidateSelf()
                return drawable
            }
            val wrapDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrapDrawable, color)
            return wrapDrawable
        }

    }

}