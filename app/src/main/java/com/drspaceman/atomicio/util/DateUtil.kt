package com.drspaceman.atomicio.util

import android.os.Build
import java.sql.Date
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale
import java.util.Calendar
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.LONG

object DateUtil {

    fun getDayOfWeek(localDate: LocalDate): DayOfWeek? {
        return if (isSdkPreOreo()) {
            val dayOfWeekString = localDateToCalendar(localDate)
                .getDisplayName(
                    DAY_OF_WEEK,
                    LONG,
                    Locale.getDefault()
                )?.toUpperCase(Locale.getDefault())

            dayOfWeekString?.let {
                DayOfWeek.valueOf(it)
            }
        } else {
            localDate.dayOfWeek
        }
    }


    fun dateStringToLocalDate(dateString: String?): LocalDate? {
        val date = dateString ?: return null



        return if (isSdkPreOreo()) {

            val formatterBuilder = DateTimeFormatterBuilder()

            val formatter = DateTimeFormatter.withLocale

            val localDate = LocalDate.now()


            val calendar: Calendar = dateToCalendar(Date.valueOf(date))

            val time = Date.valueOf(date).toInstant()

            val instant = Instant.ofEpochMilli(time)

            val localDate = LocalDate.from()

//            val localDate = LocalDate.of(
//                calendar.get(YEAR),
//                calendar.get(MONTH),
//                calendar.get(DAY_OF_MONTH)
//            )

            null

        } else {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }


    private fun isSdkPreOreo(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O
    }

    private fun localDateToDate(dateToConvert: LocalDate): Date {
        return Date.valueOf(dateToConvert.toString())
    }

    private fun localDateTimeToCalendar(localDateTime: LocalDateTime): Calendar {
        val date: Date = localDateTimeToDate(localDateTime)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    private fun localDateTimeToDate(dateTimeToConvert: LocalDateTime): Date {
        return Date.valueOf(dateTimeToConvert.toString())
    }

    private fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    private fun localDateToCalendar(localDate: LocalDate): Calendar {
        val date: Date = localDateToDate(localDate)
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }
}