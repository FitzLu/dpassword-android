package com.dpass.android.net.factory

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonFactory {

    fun create(): Gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

}