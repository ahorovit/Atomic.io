package com.drspaceman.atomicio.util

import java.sql.Date
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object DateUtil {


    fun localDateToDate(dateToConvert: LocalDate): Date {
        return Date.valueOf(dateToConvert.toString())
    }

    fun localDateTimeToCalendar(localDateTime: LocalDateTime): Calendar {
        val date: Date = localDateTimeToDate(localDateTime)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }


    fun localDateTimeToDate(dateTimeToConvert: LocalDateTime): Date {
        return Date.valueOf(dateTimeToConvert.toString())
    }


    fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    fun getDayOfWeek(localDate: LocalDate): DayOfWeek? {
        val calendar = localDateToCalendar(localDate)

        return DayOfWeek.valueOf(
            calendar.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault()
            ).toUpperCase()
        )
    }

    fun localDateToCalendar(localDate: LocalDate): Calendar {
        val date: Date = localDateToDate(localDate)
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }


}