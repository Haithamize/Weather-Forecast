package com.haithamghanem.weatherwizard.constants_and_utilities

import android.content.Context
import android.os.LocaleList
import java.util.*

class HelperClass(base: Context?) :
    android.content.ContextWrapper(base) {
    companion object {
        fun changeLang(
            context: Context,
            newLocale: Locale?
        ): HelperClass {
            var context = context
            val res = context.resources
            val configuration = res.configuration
            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)
            return HelperClass(context)
        }

    }
}