package com.drspaceman.atomicio.db

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun localDateToDateString(localDate: LocalDate?): String?
    {
        return localDate?.toString()
    }

    @TypeConverter
    fun dateStringToLocalDate(dateString: String?): LocalDate?
    {
        return dateString?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    @TypeConverter
    fun localTimeToDateString(localTime: LocalTime?): String?
    {
        return localTime?.toString()
    }

    @TypeConverter
    fun dateStringToLocalTime(dateString: String?): LocalTime?
    {
        return dateString?.let {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        }
    }
}
