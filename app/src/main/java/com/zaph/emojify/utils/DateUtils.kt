package com.zaph.emojify.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

object DateUtils {

    fun getFormattedCurrentDate(format:String):String{
        return SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(Date())
    }
}